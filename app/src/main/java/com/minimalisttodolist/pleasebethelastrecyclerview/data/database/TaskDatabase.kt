package com.minimalisttodolist.pleasebethelastrecyclerview.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.DeletedTask
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.Task

@Database(
    entities = [Task::class, DeletedTask::class],
    version = 1
)
abstract class ContactDatabase: RoomDatabase() {

    abstract val dao: TaskDao
}