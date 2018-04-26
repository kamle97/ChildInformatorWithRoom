package com.example.kamle.childinformatorwithroom;

/**
 * Created by kamle on 20.04.2018.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Person.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract  PersonDao personDao();
}
