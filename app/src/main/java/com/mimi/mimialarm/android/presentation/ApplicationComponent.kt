package com.mimi.mimialarm.android.presentation

import dagger.Component
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: MimiAlarmApplication)
}
