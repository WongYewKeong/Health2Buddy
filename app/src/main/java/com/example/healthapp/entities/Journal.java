package com.example.healthapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "journals")
public class Journal {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "journal_id")
    int journalID;

    @NonNull
    @ColumnInfo(name = "journal_title")
    String journalTitle;

    @NonNull
    @ColumnInfo(name = "journal_description")
    String journalDescription;

    public Journal(@NonNull int journalID) {
        this.journalID = journalID;
    }

    public Journal(@NonNull String journalTitle, @NonNull String journalDescription) {
        this.journalTitle = journalTitle;
        this.journalDescription = journalDescription;
    }

    public Journal(int journalID, @NonNull String journalTitle, @NonNull String journalDescription) {
        this.journalID = journalID;
        this.journalTitle = journalTitle;
        this.journalDescription = journalDescription;
    }


    public Journal() {

    }

    public int getJournalID() {
        return journalID;
    }

    public void setJournalID(int journalID) {
        this.journalID = journalID;
    }

    @NonNull
    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(@NonNull String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getJournalDescription() {
        return journalDescription;
    }

    public void setJournalDescription(String journalDescription) {
        this.journalDescription = journalDescription;
    }
}

