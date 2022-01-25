package com.example.doodletask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDetailsResponse: UserDetailsEntity):Long

    @Query("SELECT * FROM userdetailsentity")
    fun getAllUserDetails():List<UserDetailsEntity>
}