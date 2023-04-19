package com.ltr.ebook.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    //查询所有数据
    List<User> getAll();

    @Insert
    //插入一组用户数据
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("select * from user where fictionId=:fictionId and userId=:userId")
    //根据小说 ID 和用户 ID 查询指定的用户数据
    User get(String fictionId,int userId);

    @Query("select * from user where userId=:userId and flag=1")
    //查询指定用户的书架列表
    List<User> getBookList(int userId);

    @Query("update user set position=:position,readChapter=:readChapter where id=:id")
    //根据 ID 更新指定的用户数据的阅读章节和章节名
    void update(int id,int position,String readChapter);

    @Update
    //更新指定的用户数据
    void updateAll(User user);
}
