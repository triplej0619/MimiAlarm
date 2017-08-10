package com.mimi.mimialarm

import android.os.Handler
import android.os.Message
import android.support.test.runner.AndroidJUnit4
import com.mimi.mimialarm.android.infrastructure.MimiAlarmManager
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import javax.inject.Inject
import org.junit.Before



/**
 * Created by MihyeLee on 2017. 8. 9..
 */
@RunWith(AndroidJUnit4::class)
class TimerTest {

    var alarmManager: MimiAlarmManager = mock()

//    @Inject lateinit var alarmManager: AlarmManager

    @Before fun setUp() {
        val component: TestComponent = DaggerTestComponent.builder().testModule(TestModule()).build()
        component.inject(this)
    }

}