package com.example.doodletask

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserDetailsEntity::class], version = 1)
abstract class UsersRoom : RoomDatabase() {
    abstract fun usersDao(): UserDetailsDao
}