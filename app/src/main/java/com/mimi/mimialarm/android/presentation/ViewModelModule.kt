package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmViewModel
import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Module
class ViewModelModule {
    @Provides
    fun provideAlarmViewModel(uiManager: UIManager): AlarmViewModel = AlarmViewModel(uiManager)

    @Provides
    fun provideAlarmDetailViewModel(uiManager: UIManager, bus: Bus): AlarmDetailViewModel = AlarmDetailViewModel(uiManager, bus)
}
