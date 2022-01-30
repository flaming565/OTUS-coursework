package com.beautydiary.notifications.di

import com.beautydiary.notifications.NotificationUtils
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationUtils() }
}