package com.example.kamle.childinformatorwithroom;

/**
 * Created by kamle on 20.04.2018.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Person {
    @PrimaryKey
    int id;

    @ColumnInfo(name="name")
    String name;

    @ColumnInfo(name="surname")
    String surname;

    @ColumnInfo(name="age")
    int age;

    @ColumnInfo(name="path")
    String path;

    @Ignore
    public Person(){ }

    public Person(String name, String surname, int age, String path){
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.path = path;
    }
    public int getID(){ return this.id; }
    public void setID(int id){ this.id = id; }
    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
