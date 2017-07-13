package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.*
import com.mimi.mimialarm.core.infrastructure.ChangeAlarmStatusEvent
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel(val bus: Bus) : BaseViewModel() {

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

    fun changeEnableStatus() {
        enable.set(!enable.get())
        changeAlarmStatus(enable.get())
    }

    fun changeAlarmStatus(activation: Boolean) {
        id?.let { bus.post(ChangeAlarmStatusEvent(id!!, activation)) }
    }
}