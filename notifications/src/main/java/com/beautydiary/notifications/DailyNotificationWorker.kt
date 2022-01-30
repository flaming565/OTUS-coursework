package com.beautydiary.notifications

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.beautydiary.core_ui.activities.MainActivity
import com.beautydiary.domain.usecases.TaskActionsUseCase
import com.beautydiary.notifications.NotificationUtils.Companion.DAILY_NOTIFICATION_ID
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject


class DailyNotificationWorker(context: Context, params: WorkerParameters) : KoinComponent,
    Worker(context, params) {

    private val notificationUtils by inject<NotificationUtils>()
    private val taskActionsUseCase by inject<TaskActionsUseCase>()

    override fun doWork(): Result {
        return try {
            if (isAppInBackground() && taskActionsUseCase.todayTasksSync().isNotEmpty()) {
                sendNotification()
            }
            scheduleNextNotification()
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
            notification.setChannelId(NOTIFICATION_CHANNEL_ID)
        }

        notificationManager.notify(DAILY_NOTIFICATION_ID, notification.build())
    }

    private fun scheduleNextNotification() {
        Thread.sleep(3000)
        notificationUtils.scheduleNextDailyNotification()
    }

    private fun createNotification(): NotificationCompat.Builder {
        // todo: open todo-list?
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // TODO: change title and text and icon
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_daily_title))
            .setContentText(getString(R.string.notification_daily_description))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun isAppInBackground(): Boolean {
        val appProcess = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcess)
        return appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

    private fun getString(@StringRes id: Int) = get<Context>().resources.getString(id)

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "beautydiary_notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "beautydiary_notification_channel"
    }
}