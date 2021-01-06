package com.example.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.healthapp.LoginActivity;
import com.example.healthapp.R;
import com.example.healthapp.entities.Journal;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;

    Button btnSave, btnUpdate;
    EditText etTitle, etDescription;
    TextView tvJournalId;
    ListView lvJournal;

    ArrayAdapter<String> journalListAdapter;
    ArrayList<String> journalArray;
    ArrayList<Integer> journalID;

    public static MyJournalDB myJournalDB;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_journal, container, false);


        btnSave = root.findViewById(R.id.btn_save);
        btnUpdate = root.findViewById(R.id.btn_update);

        etTitle = root.findViewById(R.id.et_title);
        etDescription = root.findViewById(R.id.et_description);

        tvJournalId = root.findViewById(R.id.tv_journal_id);
        lvJournal = root.findViewById(R.id.lv_main);

        journalListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        journalArray = new ArrayList<String>();
        journalID = new ArrayList<Integer>();

        myJournalDB = Room.databaseBuilder(getContext(), MyJournalDB.class, "MyJournalDB").build();




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextIsEmpty())
                    return;
                saveJournal();


            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextIsEmpty())
                    return;
                updateJournal();
            }
        });

        lvJournal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvJournalId.setText(journalID.get(i).toString());

            }
        });

        lvJournal.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long i) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Remove Journal");
                alertDialogBuilder.setMessage("Are you sure you to remove the journal #" + journalID.get(position));
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // toast(getApplicationContext(),"Action Canceled");
                    }
                });

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Journal journal = new Journal(Integer.parseInt(journalID.get(position).toString()));
                        deleteJournal(journal);
                    }
                });

                alertDialogBuilder.show();
                return true;
            }
        });



        return root;

    }

    public void saveJournal() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Journal journal = new Journal(etTitle.getText().toString(), etDescription.getText().toString());
                myJournalDB.journalDao().insertJournal(journal);
                toast(signInClient.getApplicationContext(), "Journal Added");
                getAllJournal();
            }
        }).start();

    }

    public void updateJournal() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Journal journal = new Journal(Integer.parseInt(tvJournalId.getText().toString()), etTitle.getText().toString(), etDescription.getText().toString());
                myJournalDB.journalDao().updateJournal(journal);
                toast(signInClient.getApplicationContext(), "Journal Updated");
                getAllJournal();
            }
        }).start();



    }

    public void deleteJournal(final Journal jou) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                myJournalDB.journalDao().deleteJournal(jou);
                toast(signInClient.getApplicationContext(), "Journal Removed");
                getAllJournal();

            }
        }).start();



    }

    public void getAllJournal() {

        journalID.clear();
        journalArray.clear();

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                List<Journal> journals = myJournalDB.journalDao().getAllJournals();
                String journalInfor;
                for(Journal journal:journals)
                {
                    journalInfor ="#" + journal.getJournalID() +
                            "\nTitle: " + journal.getJournalTitle() +
                            "\nDescription: " + journal.getJournalDescription();

                    journalArray.add(journalInfor);
                    journalID.add(journal.getJournalID());

                }
                showDataInListView();

            }
        }).start();





    }

    public void showDataInListView() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                journalListAdapter.clear();
                journalListAdapter.addAll(journalArray);
                lvJournal.setAdapter(journalListAdapter);

            }
        });

    }




    private boolean editTextIsEmpty() {

        if(TextUtils.isEmpty(etTitle.getText().toString())){
            etTitle.setError("Cannot be Empty");
        }

        if(TextUtils.isEmpty(etDescription.getText().toString())){
            etDescription.setError("Cannot be Empty");
        }

        if(TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etDescription.getText().toString()))
        {
            return true;
        } else
            return false;

    }

    public void toast(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        getAllJournal();
    }

}