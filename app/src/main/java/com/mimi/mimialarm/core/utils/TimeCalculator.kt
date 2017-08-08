package com.mimi.mimialarm.core.utils

import com.mimi.data.model.MyAlarm
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by MihyeLee on 2017. 7. 10..
 */
class TimeCalculator {
    companion object {

        val SECOND: Int = 1
        val MINUTE: Int = SECOND * 60
        val HOUR: Int = MINUTE * 60
        val DAY: Int = HOUR * 24

        fun getSnoozeTime(alarm: MyAlarm) : Long {
            alarm.snoozeInterval?.let {
                return alarm.snoozeInterval!!.toLong() * MINUTE
            }
            return 0
        }

        fun getMilliSecondsForScheduling(alarm: MyAlarm) : Long {
            val today: GregorianCalendar = GregorianCalendar()
            val targetDate: GregorianCalendar = GregorianCalendar()
            today.time = Date()
            targetDate.time = alarm.completedAt
            targetDate.set(GregorianCalendar.YEAR, today.get(GregorianCalendar.YEAR))
            targetDate.set(GregorianCalendar.MONTH, today.get(GregorianCalendar.MONTH))
            targetDate.set(GregorianCalendar.DAY_OF_MONTH, today.get(GregorianCalendar.DAY_OF_MONTH))

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
                        dayOfWeek = plusDayOfWeek(dayOfWeek, 1)
                    }
                }
                targetDate.add(GregorianCalendar.DAY_OF_MONTH, jump)
            }

            if (today.timeInMillis >= targetDate.timeInMillis) {
                targetDate.add(GregorianCalendar.DAY_OF_MONTH, 1)
            }
            return targetDate.timeInMillis - today.timeInMillis
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

        fun getHourFromSeconds(seconds: Long) : Long {
            return (seconds % DAY) / HOUR
        }

        fun getMinuteFromSeconds(seconds: Long) : Long {
            return (seconds % HOUR) / MINUTE
        }

        fun getDayFromSeconds(seconds: Long) : Long {
            return seconds / DAY
        }

        fun getSecondFromSeconds(seconds: Long) : Long {
            return seconds % MINUTE
        }

        fun getSecondsFromAll(hour: Long, minute: Long, second: Long) : Long {
            return (hour * HOUR) + (minute * MINUTE) + second
        }
    }

}