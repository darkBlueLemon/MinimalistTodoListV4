package com.darkblue.minimalisttodolistv4.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darkblue.minimalisttodolistv4.data.model.DeletedTask
import com.darkblue.minimalisttodolistv4.data.model.Task

@Database(
    entities = [Task::class, DeletedTask::class],
    version = 1
)
abstract class ContactDatabase: RoomDatabase() {

    abstract val dao: TaskDao
}