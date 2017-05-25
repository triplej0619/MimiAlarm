package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.android.presentation.service.ActivityManager
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Module
class ApplicationModule(private val application: MimiAlarmApplication) {
    @Provides @Singleton
    fun provideActivityManager(): ActivityManager = ActivityManager(application)

    @Provides @Singleton
    fun provideBus(): Bus = Bus(ThreadEnforcer.ANY)
}