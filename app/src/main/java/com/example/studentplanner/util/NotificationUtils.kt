package com.example.studentplanner.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.studentplanner.R
import com.example.studentplanner.worker.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationUtils {
    const val CHANNEL_ID = "task_reminder_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminders"
            val descriptionText = "Channel for assignment due notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, taskTitle: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Assignment Due")
            .setContentText("Don't forget: $taskTitle is due!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(taskTitle.hashCode(), builder.build())
    }

    fun scheduleNotification(context: Context, taskTitle: String, dueDateString: String) {
        // Parse the due date string (expecting format "MM/dd/yyyy")
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val dueDateMillis = try {
            formatter.parse(dueDateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }

        val delay = dueDateMillis - System.currentTimeMillis()

        // Only schedule if due date is in the future
        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("task_title" to taskTitle))
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

}
