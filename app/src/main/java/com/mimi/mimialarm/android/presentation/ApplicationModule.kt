package com.mimi.mimialarm.android.presentation

import com.mimi.data.DBManager
import com.mimi.data.RealmDBManager
import com.mimi.mimialarm.android.infrastructure.MimiAlarmManager
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Module
class ApplicationModule(private val application: MimiAlarmApplication) {
    @Provides @Singleton
    fun provideBus(): Bus = Bus(ThreadEnforcer.ANY)

    @Provides @Singleton
    fun provideUiManager(bus: Bus): UIManager = MimiActivityManager(application, bus)

    @Provides @Singleton
    fun provideDbManager(): DBManager = RealmDBManager().setRealm(Realm.getDefaultInstance())

    @Provides @Singleton
    fun provideAlarmManager(): AlarmManager = MimiAlarmManager(application)
}