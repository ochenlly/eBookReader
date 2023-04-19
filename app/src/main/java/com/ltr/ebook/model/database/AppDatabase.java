package com.ltr.ebook.model.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class},version = 6,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    //获取与用户相关的数据库操作对象
    public abstract UserDao userDao();
}
