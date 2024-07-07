package com.minimalisttodolist.pleasebethelastrecyclerview.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.minimalisttodolist.pleasebethelastrecyclerview.data.database.ContactDatabase
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.DeletedTask
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.RecurrenceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class NotificationActionReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", 0)
        when (intent.action) {
            "COMPLETE" -> {
                val notificationHelper = NotificationHelper(context)
                notificationHelper.cancelNotification(taskId)

                val db = Room.databaseBuilder(context, ContactDatabase::class.java, "contacts.db")
                    .fallbackToDestructiveMigration()
                    .build()
                val taskDao = db.dao

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val task = taskDao.getTaskById(taskId.toInt())
                        Log.d("TAG", "Processing task with id = $taskId")

                        task?.let {
                            Log.d("TAG", "Task found. Recurrence type: ${it.recurrenceType}")

                            if (it.recurrenceType != RecurrenceType.NONE) {
                                val nextDueDate = calculateNextDueDate(it.dueDate, it.recurrenceType)
                                val updatedTask = it.copy(dueDate = nextDueDate)
                                taskDao.upsertTask(updatedTask)
                                notificationHelper.scheduleNotification(updatedTask)
                                Log.d("TAG", "Recurring task updated and rescheduled")
                            } else {
                                taskDao.deleteTask(task)
                                taskDao.insertDeletedTask(
                                    DeletedTask(
                                        title = task.title,
                                        priority = task.priority,
                                        note = task.note,
                                        dueDate = task.dueDate,
                                        recurrenceType = task.recurrenceType,
                                        deletedAt = System.currentTimeMillis()
                                    )
                                )
                                Log.d("TAG", "Non-recurring task deleted and moved to history")
                            }
                        } ?: Log.d("TAG", "No task found for id = $taskId")

                    } catch (e: Exception) {
                        Log.e("TAG", "Error processing task completion", e)
                    } finally {
                        db.close()
                    }
                }
            }
            "SNOOZE" -> {
                val db = Room.databaseBuilder(context, ContactDatabase::class.java, "contacts.db").build()
                val taskDao = db.dao
                val notificationHelper = NotificationHelper(context)

                CoroutineScope(Dispatchers.IO).launch {
                    val task = taskDao.getTaskById(taskId)
                    task?.let {
                        notificationHelper.snoozeNotification(taskId, it)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDueDate(dueDate: Long?, recurrenceType: RecurrenceType): Long? {
        if (dueDate == null) return null
        val date = Instant.ofEpochMilli(dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val nextDate = when (recurrenceType) {
            RecurrenceType.DAILY -> date.plusDays(1)
            RecurrenceType.WEEKLY -> date.plusWeeks(1)
            RecurrenceType.MONTHLY -> date.plusMonths(1)
            RecurrenceType.YEARLY -> date.plusYears(1)
            else -> return null
        }
        return nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}