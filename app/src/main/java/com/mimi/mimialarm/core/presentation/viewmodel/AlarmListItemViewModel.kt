package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.*
import com.mimi.mimialarm.core.infrastructure.ChangeAlarmStatusEvent
import com.mimi.mimialarm.core.infrastructure.StopAlarmItemEvent
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.DateUtil
import com.squareup.otto.Bus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel(val bus: Bus) : BaseViewModel(), Comparable<AlarmListItemViewModel> {

    val FULL_ALPHA = ObservableFloat(1.0f)
    val HALF_ALPHA = ObservableFloat(0.5f)

    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var selectForDelete: ObservableBoolean = ObservableBoolean(false)

    var id: Int? = null
    var monDay: ObservableBoolean = ObservableBoolean(false)
    var tuesDay: ObservableBoolean = ObservableBoolean(false)
    var wednesDay: ObservableBoolean = ObservableBoolean(false)
    var thursDay: ObservableBoolean = ObservableBoolean(false)
    var friDay: ObservableBoolean = ObservableBoolean(false)
    var saturDay: ObservableBoolean = ObservableBoolean(false)
    var sunDay: ObservableBoolean = ObservableBoolean(false)

    var isAm: ObservableBoolean = ObservableBoolean(false)
    var endTime: Date? = null
    set(value) {
        field = value

        val dateFormat = SimpleDateFormat("hh:mm", Locale.KOREA)
        endTimeString.set(dateFormat.format(value))

        val calendar: GregorianCalendar = GregorianCalendar()
        calendar.time = value
        val hourOfDay = calendar.get(GregorianCalendar.HOUR_OF_DAY)
        if(hourOfDay >= 12) {
            isAm.set(false)
        } else {
            isAm.set(true)
        }
    }
    var endTimeString: ObservableField<String> = ObservableField("")
    var snooze: ObservableBoolean = ObservableBoolean(false)
    var snoozeInterval: ObservableInt = ObservableInt(0)
    var snoozeCount: ObservableInt = ObservableInt(0)

    var enable: ObservableBoolean = ObservableBoolean(true)

    val changeSelectStatusCommand: Command = object : Command {
        override fun execute(arg: Any) {
            selectForDelete.set(!selectForDelete.get())
        }
    }

    val changeEnableStatusCommand: Command = object : Command {
        override fun execute(arg: Any) {
            changeEnableStatus()
        }
    }

    val stopCommand: Command = object : Command {
        override fun execute(arg: Any) {
            stop()
        }
    }

    fun changeEnableStatus() {
        enable.set(!enable.get())
        changeAlarmStatus(enable.get())
    }

    fun changeAlarmStatus(activation: Boolean) {
        id?.let { bus.post(ChangeAlarmStatusEvent(id!!, activation)) }
    }

    fun stop() {
        bus.post(StopAlarmItemEvent(id!!))
    }

    override fun compareTo(other: AlarmListItemViewModel): Int {
        val myTime = GregorianCalendar()
        myTime.time = endTime
        val yourTime = GregorianCalendar()
        yourTime.time = other.endTime

        if((myTime.get(GregorianCalendar.HOUR_OF_DAY) > yourTime.get(GregorianCalendar.HOUR_OF_DAY)) and
                (myTime.get(GregorianCalendar.MINUTE) > yourTime.get(GregorianCalendar.MINUTE))) {
            return 1
        } else if((myTime.get(GregorianCalendar.HOUR_OF_DAY) == yourTime.get(GregorianCalendar.HOUR_OF_DAY)) and
                (myTime.get(GregorianCalendar.MINUTE) == yourTime.get(GregorianCalendar.MINUTE))) {
            return 0
        }
        return -1
    }
}