package com.example.kamle.childinformatorwithroom;

/**
 * Created by kamle on 20.04.2018.
 */

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person")
    List<com.example.kamle.childinformatorwithroom.Person> getAllPeople();

    @Insert
    void addPerson(com.example.kamle.childinformatorwithroom.Person person);

    @Update
    void update(com.example.kamle.childinformatorwithroom.Person person);

    @Delete
    void delete(com.example.kamle.childinformatorwithroom.Person person);

    @Query("DELETE FROM person")
    void clearDB();
}
