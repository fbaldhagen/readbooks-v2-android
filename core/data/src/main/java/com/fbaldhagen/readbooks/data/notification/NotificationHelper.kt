package com.fbaldhagen.readbooks.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fbaldhagen.readbooks.data.di.NotificationIcon
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @NotificationIcon private val notificationIcon: Int
) {
    companion object {
        const val CHANNEL_ID = "reading_reminders"
        const val NOTIFICATION_ID_DAILY = 1001
        const val NOTIFICATION_ID_STREAK = 1002
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reading Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reading goal and streak reminders"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun buildHomePendingIntent(): PendingIntent {
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun postDailyReminderNotification(todayMinutes: Int, goalMinutes: Int) {
        if (!hasNotificationPermission()) return
        val remaining = goalMinutes - todayMinutes
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle("Reading goal reminder")
            .setContentText("$remaining minutes left to reach your daily goal!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(buildHomePendingIntent())
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun postStreakReminderNotification(streakDays: Int) {
        if (!hasNotificationPermission()) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle("Your streak is at risk!")
            .setContentText("Read today to keep your $streakDays day streak alive 🔥")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(buildHomePendingIntent())
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STREAK, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}