package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.android.presentation.service.MimiActivityManager
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.mimi.mimialarm.android.presentation.view.AlarmFragment
import com.mimi.mimialarm.android.presentation.view.MainActivity
import com.mimi.mimialarm.android.presentation.view.TimerFragment
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
    fun inject(alarmDetailActivity: AlarmDetailActivity)
}
