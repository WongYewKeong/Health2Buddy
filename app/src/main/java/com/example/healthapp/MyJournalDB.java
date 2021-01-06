package com.example.healthapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.healthapp.Dao.JournalDao;
import com.example.healthapp.entities.Journal;

@Database(entities = {Journal.class},version = 1)
public abstract class MyJournalDB extends RoomDatabase {

    public abstract JournalDao journalDao();


}
