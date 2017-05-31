package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.android.presentation.service.ActivityManager
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.mimi.mimialarm.android.presentation.view.AlarmFragment
import dagger.Component

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@PerActivity
@Component(dependencies = arrayOf(ApplicationComponent::class),
        modules = arrayOf(ViewModelModule::class))
interface ActivityComponent {
    fun inject(alarmFragment: AlarmFragment)
    fun inject(activityManager: ActivityManager)
    fun inject(alarmDetailActivity: AlarmDetailActivity)
}