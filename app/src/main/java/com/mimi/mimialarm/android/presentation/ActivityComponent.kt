package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.android.infrastructure.service.AlarmDeactivateService
import com.mimi.mimialarm.android.infrastructure.service.PreNoticeAlarmService
import com.mimi.mimialarm.android.infrastructure.service.ReschedulingAlarmAfterBootService
import com.mimi.mimialarm.android.infrastructure.service.TimerDeactivateService
import com.mimi.mimialarm.android.presentation.view.*
import dagger.Component

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@PerActivity
@Component(dependencies = arrayOf(ApplicationComponent::class),
        modules = arrayOf(ViewModelModule::class))
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(alarmFragment: AlarmFragment)
    fun inject(timerFragment: TimerFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(alarmDetailActivity: AlarmDetailActivity)
    fun inject(alarmOnActivity: AlarmOnActivity)
    fun inject(timerOnActivity: TimerOnActivity)
    fun inject(activatedAlarmListActivity: ActivatedAlarmListActivity)

    fun inject(timerDeactivateService: TimerDeactivateService)
    fun inject(alarmDeactivateService: AlarmDeactivateService)
    fun inject(preNoticeAlarmService: PreNoticeAlarmService)
    fun inject(reschedulingAlarmAfterBootService: ReschedulingAlarmAfterBootService)
}
