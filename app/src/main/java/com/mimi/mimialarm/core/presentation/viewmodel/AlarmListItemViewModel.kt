package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.*
import com.mimi.mimialarm.core.model.MyAlarm
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel : BaseViewModel() {

    companion object {
        fun alarmDataToAlarmListItem(newAlarm: MyAlarm, item: AlarmListItemViewModel) {
            item.id = newAlarm.id

            item.friDay.set(newAlarm.friDay ?: false)
            item.monDay.set(newAlarm.monDay ?: false)
            item.thursDay.set(newAlarm.thursDay ?: false)
            item.tuesDay.set(newAlarm.tuesDay ?: false)
            item.wednesDay.set(newAlarm.wednesDay ?: false)
            item.saturDay.set(newAlarm.saturDay ?: false)
            item.sunDay.set(newAlarm.sunDay ?: false)

            item.endTime = newAlarm.completedAt

            if (newAlarm.snoozeCount != null && newAlarm.snoozeInterval != null) {
                item.snooze.set(true)
            }
            item.snoozeCount.set(newAlarm.snoozeCount ?: 0)
            item.snoozeInterval.set(newAlarm.snoozeInterval ?: 0)
            item.isEnable.set(newAlarm.enable ?: false)
        }

        fun alarmDataToAlarmListItem(newAlarm: MyAlarm) : AlarmListItemViewModel {
            val item: AlarmListItemViewModel = AlarmListItemViewModel()
            alarmDataToAlarmListItem(newAlarm, item)
            return item
        }
    }

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

    var isEnable: ObservableBoolean = ObservableBoolean(true)

    fun copyFromAlarm(alarm: MyAlarm) {
        alarmDataToAlarmListItem(alarm, this)
    }
}