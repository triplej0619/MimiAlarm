package com.mimi.mimialarm

import com.mimi.mimialarm.core.infrastructure.AlarmManager
import dagger.Component
import javax.inject.Singleton

/**
 * Created by MihyeLee on 2017. 8. 9..
 */
@Singleton
@Component(modules = arrayOf(TestModule::class))
interface TestComponent {
    fun inject(test: TimerTest)

    fun alarmManager(): AlarmManager
}