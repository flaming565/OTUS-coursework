package com.beautydiary.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.beautydiary.domain.ext.int
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationUtils : KoinComponent {
    private val prefs =
        get<Context>().getSharedPreferences("NotificationUtils", Context.MODE_PRIVATE)
    private var scheduledHours by prefs.int(DEFAULT_HOUR)
    private var scheduledMinutes by prefs.int(DEFAULT_MINUTE)

    fun scheduleDailyNotification(hours: Int = scheduledHours, minutes: Int = scheduledMinutes) {
        scheduledHours = hours
        scheduledMinutes = minutes
        enqueueWorkRequest<DailyNotificationWorker>(DAILY_NOTIFICATION_WORK, getInitialDelay())
    }

    fun scheduleNextDailyNotification() {
        enqueueWorkRequest<DailyNotificationWorker>(DAILY_NOTIFICATION_WORK, getInitialDelay())
    }

    private inline fun <reified T : ListenableWorker> enqueueWorkRequest(
        workName: String,
        initialDelay: Long = 0L
    ) {
        val workRequest = OneTimeWorkRequestBuilder<T>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(get())
            .beginUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            .enqueue()
    }

    private fun getInitialDelay(): Long {
        val now = Calendar.getInstance()

        val scheduledDate = Calendar.getInstance()
        scheduledDate.set(Calendar.HOUR_OF_DAY, scheduledHours)
        scheduledDate.set(Calendar.MINUTE, scheduledMinutes)
        scheduledDate.set(Calendar.SECOND, 0)

        if (scheduledDate.before(now)) {
            scheduledDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        return scheduledDate.timeInMillis - now.timeInMillis
    }

    companion object {
        private const val DEFAULT_HOUR = 20
        private const val DEFAULT_MINUTE = 0
        const val DAILY_NOTIFICATION_WORK = "beautydiary_daily_notification_work"
        const val DAILY_NOTIFICATION_ID = 27
    }
}