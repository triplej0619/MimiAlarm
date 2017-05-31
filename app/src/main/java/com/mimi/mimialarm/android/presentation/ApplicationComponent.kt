package com.mimi.mimialarm.android.presentation

import com.mimi.mimialarm.core.infrastructure.UIManager
import com.squareup.otto.Bus
import dagger.Component
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
}
