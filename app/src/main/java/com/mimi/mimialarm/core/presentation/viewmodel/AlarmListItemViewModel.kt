package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel : BaseViewModel() {
    var monday: ObservableBoolean = ObservableBoolean(false)
    var tuesDay: ObservableBoolean = ObservableBoolean(false)
    var wednesDay: ObservableBoolean = ObservableBoolean(false)
    var thurseDay: ObservableBoolean = ObservableBoolean(false)
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

    var isEnable: ObservableBoolean = ObservableBoolean(true)
}