package com.darkblue.minimalisttodolistv4.data.database

import androidx.room.*
import com.darkblue.minimalisttodolistv4.data.model.DeletedTask
import com.darkblue.minimalisttodolistv4.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("""
        SELECT * FROM task 
        ORDER BY priority DESC, 
                 CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, 
                 dueDate ASC
    """)
    fun getTasksSortedByPriority(): Flow<List<Task>>

    @Query("""
        SELECT * FROM task 
        ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, 
                 dueDate ASC
    """)
    fun getTasksSortedByDueDate(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY title ASC")
    fun getTasksOrderedAlphabetically(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY title DESC")
    fun getTasksOrderedAlphabeticallyRev(): Flow<List<Task>>

    // Deleted Tasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedTask(deletedTask: DeletedTask)

    @Delete
    suspend fun deleteDeletedTask(deletedTask: DeletedTask)

    @Query("SELECT * FROM deleted_tasks ORDER BY deletedAt DESC")
    fun getDeletedTasks(): Flow<List<DeletedTask>>
}