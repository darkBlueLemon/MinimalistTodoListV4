package com.minimalisttodolist.pleasebethelastrecyclerview.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.minimalisttodolist.pleasebethelastrecyclerview.data.model.RecurrenceType
import java.time.Instant
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
fun calculateNextDueDate(dueDate: Long?, recurrenceType: RecurrenceType): Long? {
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
