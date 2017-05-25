package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.android.presentation.view.AlarmFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Singleton
@Component(dependencies = arrayOf(ApplicationComponent::class),
        modules = arrayOf(ViewModelModule::class))
interface ActivityComponent {
    fun inject(alarmFragment: AlarmFragment)
}
