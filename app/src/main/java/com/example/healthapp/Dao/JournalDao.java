package com.example.healthapp.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.healthapp.entities.Journal;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert
    void insertJournal(Journal journal);


    @Update
    void updateJournal(Journal journal);


    @Delete
    void deleteJournal(Journal journal);


    @Query("Select * from journals")
    List<Journal> getAllJournals();



}
