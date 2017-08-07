package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.ActivateAlarmEvent
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.Command2
import com.mimi.mimialarm.core.utils.DateUtil
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
    var usedSnoozeCount: Int = 0
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
    var willExpire = ObservableBoolean(false)

    init {
        finishInWindow.set(applicationDataManager.getAlarmCloseMethod() == 0)
    }

    val finishViewCommand: Command = object : Command {
        override fun execute(arg: Any) {
            updateEnableStatus()
            setNextDayAlarm()
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

    fun updateEnableStatus() {
        alarm?.let {
            if((!alarm!!.snooze and !alarm!!.repeat)
                    or
                    (alarm!!.snooze and (usedSnoozeCount > snoozeCount))) {
                alarm!!.enable = false
            }
            alarm!!.usedSnoozeCount = 0
            dbManager.updateAlarm(alarm!!)
        }
    }

    fun setNextDayAlarm() {
        if(alarm!!.repeat) {
            alarmManager.startAlarm(alarmId!!, TimeCalculator.getMilliSecondsForScheduling(alarm!!))
            bus.post(ActivateAlarmEvent())
        }
    }

    fun resetAlarm() {
        LogUtil.printDebugLog(this@AlarmOnViewModel.javaClass, "resetAlarm()")
        if(enable && alarm != null) {
            if(alarm!!.snooze && usedSnoozeCount <= snoozeCount) {
                alarmManager.startAlarm(alarmId!!, TimeCalculator.getSnoozeTime(alarm!!) * 1000)
                bus.post(ActivateAlarmEvent())
                val text = String.format("(%d분 뒤) %s", alarm!!.snoozeInterval, DateUtil.dateToFormattedString(DateUtil.getAfterDate((TimeCalculator.getSnoozeTime(alarm!!) * 1000).toInt()), "HH:mm")) // TODO text -> resource
                uiManager.addSnoozeNotification(text, alarmId!!)
            } else {
                setNextDayAlarm()
            }
        }
    }

    fun loadAlarm() {
        LogUtil.printDebugLog(this@AlarmOnViewModel.javaClass, "loadAlarm()")
        if(alarmId != null) {
            alarm = dbManager.findAlarmWithId(alarmId)
            alarm?.let {
                alarm!!.usedSnoozeCount = alarm!!.usedSnoozeCount!!.inc()
                DataMapper.alarmToAlarmOnViewModel(alarm!!, this)
                dbManager.updateAlarm(alarm!!)
                if((!finishInWindow.get() and !snooze.get())
                        or
                        (usedSnoozeCount > snoozeCount)) {
                    willExpire.set(true)
                }
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