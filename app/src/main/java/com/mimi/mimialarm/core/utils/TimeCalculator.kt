package com.mimi.mimialarm.core.utils

import com.mimi.data.model.MyAlarm
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by MihyeLee on 2017. 7. 10..
 */
class TimeCalculator {
    companion object {
        fun getMilliSecondsForScheduling(alarm: MyAlarm): Long {
            val today: GregorianCalendar = GregorianCalendar()
            val targetDate: GregorianCalendar = GregorianCalendar()
            today.time = Date()
            targetDate.time = alarm.completedAt

            if (alarm.repeat) {
                val dayOfWeekMap: MutableMap<Int, Boolean> = HashMap()
                dayOfWeekMap[Enums.DAY_OF_WEEK.MONDAY.value] = alarm.monDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.TUESDAY.value] = alarm.tuesDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.WEDNESDAY.value] = alarm.wednesDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.THURSDAY.value] = alarm.thursDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.FRIDAY.value] = alarm.friDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.SATURDAY.value] = alarm.saturDay
                dayOfWeekMap[Enums.DAY_OF_WEEK.SUNDAY.value] = alarm.sunDay

                var dayOfWeek: Int = today.get(GregorianCalendar.DAY_OF_WEEK)
                if (today.get(GregorianCalendar.HOUR_OF_DAY) >= targetDate.get(GregorianCalendar.HOUR_OF_DAY)
                        && today.get(GregorianCalendar.MINUTE) >= targetDate.get(GregorianCalendar.MINUTE)) {
                    targetDate.add(GregorianCalendar.DAY_OF_MONTH, 1)
                    dayOfWeek = plusDayOfWeek(dayOfWeek, 1)
                }

                var jump: Int = 0
                for (i in 0..6) {
                    if (dayOfWeekMap[dayOfWeek] ?: false) {
                        jump = i
                        break
                    } else {
                        dayOfWeek++
                    }
                }
                targetDate.add(GregorianCalendar.DAY_OF_MONTH, jump)
            }

            if (today.timeInMillis < targetDate.timeInMillis) {
                return targetDate.timeInMillis - today.timeInMillis
            } else {
                targetDate.add(GregorianCalendar.DAY_OF_MONTH, -1)
                return today.timeInMillis - targetDate.timeInMillis
            }
        }

        fun plusDayOfWeek(current: Int, plusValue: Int) : Int {
            var ret: Int = current + plusValue
            if (ret > Enums.DAY_OF_WEEK.SATURDAY.value) {
                ret = Enums.DAY_OF_WEEK.SUNDAY.value
            }
            return ret
        }

        fun minusDayOfWeek(current: Int, minusValue: Int) : Int {
            var ret: Int = current - minusValue
            if (ret <= 0) {
                ret = Enums.DAY_OF_WEEK.SATURDAY.value
            }
            return ret
        }
    }

}