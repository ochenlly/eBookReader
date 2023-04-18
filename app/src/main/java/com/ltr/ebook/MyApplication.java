package com.ltr.ebook;

import android.app.Application;

import androidx.room.Room;

import com.ltr.ebook.model.database.AppDatabase;

public class
MyApplication extends Application {
    private AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "eBookReader").fallbackToDestructiveMigration().build();
    }

    public AppDatabase getDatabase() {
        return db;
    }
}
