package com.mimi.mimialarm.android.presentation

import com.mimi.data.DBManager
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.SettingsViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.TimerOnViewModel
import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Module
class ViewModelModule {
    @Provides
    fun provideAlarmViewModel(uiManager: UIManager, dbManager: DBManager, alarmManager: AlarmManager, bus: Bus): AlarmViewModel
            = AlarmViewModel(uiManager, dbManager, alarmManager, bus)

    @Provides
    fun provideAlarmDetailViewModel(uiManager: UIManager, bus: Bus, dbManager: DBManager, alarmManager: AlarmManager): AlarmDetailViewModel
            = AlarmDetailViewModel(uiManager, bus, dbManager, alarmManager)

    @Provides
    fun provideTimerOnViewModel(uiManager: UIManager, dbManager: DBManager): TimerOnViewModel
            = TimerOnViewModel(uiManager, dbManager)

    @Provides
    fun provideSettingsViewModel(uiManager: UIManager, bus: Bus, applicationDataManager: ApplicationDataManager): SettingsViewModel
            = SettingsViewModel(uiManager, bus, applicationDataManager)
}
