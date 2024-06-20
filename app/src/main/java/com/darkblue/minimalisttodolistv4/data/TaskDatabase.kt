package com.darkblue.minimalisttodolistv4.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Task::class, DeletedTask::class],
    version = 1
)
abstract class ContactDatabase: RoomDatabase() {

    abstract val dao: TaskDao
}