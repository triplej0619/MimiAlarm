package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.ActivateAlarmEvent
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.Command2
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import java.util.*
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 10..
 */
class AlarmOnViewModel @Inject constructor(
        private val uiManager: UIManager,
        private val dbManager: DBManager,
        private val alarmManager: AlarmManager,
        private val bus: Bus,
        private val applicationDataManager: ApplicationDataManager
) : BaseViewModel() {

    val DEFAULT_VOLUME: Int = 80

    var alarmId: Int? = null
    var isAm: ObservableBoolean = ObservableBoolean(false)
    var snooze: ObservableBoolean = ObservableBoolean(true)
    var snoozeInterval: Int = 0
    var snoozeCount: Int = 0
    var endTime: Date = Date()
    var enable: Boolean = false
    var sound: Boolean = true
    var vibration: Boolean = true
    var mediaSrc: String = ""
    var soundVolume: Int = DEFAULT_VOLUME

    var hour: ObservableInt = ObservableInt(0)
    var minute: ObservableInt = ObservableInt(0)
    var day: ObservableInt = ObservableInt(0)
    var dayOfWeek: ObservableInt = ObservableInt(0)
    var month: ObservableInt = ObservableInt(0)

    var alarm: MyAlarm? = null
    var finishInWindow = ObservableBoolean(true)

    init {
        finishInWindow.set(applicationDataManager.getAlarmCloseMethod() == 0)
    }

    val finishViewCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.finishForegroundView()
        }
    }

    val finishWithResetCommand: Command = object : Command {
        override fun execute(arg: Any) {
            resetAlarm()
            uiManager.finishForegroundView()
        }
    }

    val startCommand: Command2 = object : Command2 {
        override fun execute(arg: Any, arg2: Any) {
            if(alarmId != -1) {
                loadAlarm()
                playMedia(arg)
                playVibration(arg2)
            }
        }
    }

    fun resetAlarm() {
        LogUtils.printDebugLog(this@AlarmOnViewModel.javaClass, "resetAlarm()")
        if(enable && alarm != null && alarm!!.usedSnoozeCount!! < alarm!!.snoozeCount!!) {
            alarm?.usedSnoozeCount = alarm?.usedSnoozeCount?.inc()
            dbManager.updateAlarm(alarm!!)
            alarmManager.startAlarm(alarmId!!, TimeCalculator.getSnoozeTime(alarm!!) * 1000)
            bus.post(ActivateAlarmEvent())
        }
    }

    fun loadAlarm() {
        LogUtils.printDebugLog(this@AlarmOnViewModel.javaClass, "loadAlarm()")
        if(alarmId != null) {
            alarm = dbManager.findAlarmWithId(alarmId)
            alarm?.let {
                DataMapper.alarmToAlarmOnViewModel(alarm!!, this)
            }
        }
    }

    fun playVibration(arg: Any) {
        if(vibration && arg is Command) {
            arg.execute(Unit)
        }
    }

    fun playMedia(arg: Any) {
        if(mediaSrc.isNotEmpty() && sound && arg is Command) {
            arg.execute(Unit)
        }
    }
}