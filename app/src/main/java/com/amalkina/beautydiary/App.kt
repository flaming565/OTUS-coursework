package com.amalkina.beautydiary

import android.app.Application
import com.beautydiary.data.di.dataModule
import com.beautydiary.domain.di.domainModule
import com.beautydiary.feature_tasks.di.tasksModule
import com.beautydiary.featurestatistics.di.statisticsModule
import com.beautydiary.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@App)
            modules(
                domainModule,
                dataModule,
                homeModule,
                statisticsModule,
                tasksModule
            )
        }
    }

}