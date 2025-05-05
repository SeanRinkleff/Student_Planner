package com.example.studentplanner.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentplanner.util.NotificationUtils

class TaskReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskTitle = inputData.getString("task_title") ?: return Result.failure()
        NotificationUtils.createNotificationChannel(applicationContext)
        NotificationUtils.showNotification(applicationContext, taskTitle)
        return Result.success()
    }
}
