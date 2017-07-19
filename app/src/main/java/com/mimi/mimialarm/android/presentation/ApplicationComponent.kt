package com.mimi.mimialarm.android.presentation

import com.mimi.data.DBManager
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.squareup.otto.Bus
import dagger.Component
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: MimiAlarmApplication)

    fun bus(): Bus
    fun uiManager(): UIManager
    fun dbManager(): DBManager
    fun alarmManager(): AlarmManager
    fun applicationDataManager() : ApplicationDataManager
}
