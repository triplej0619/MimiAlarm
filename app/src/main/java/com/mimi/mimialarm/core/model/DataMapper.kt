package com.mimi.mimialarm.core.model

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.core.presentation.viewmodel.*
import com.mimi.mimialarm.core.utils.DateUtil
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import java.util.*

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class DataMapper {
    companion object {
        val DEFAULT_VOLUME: Int = 80

        fun alarmToListItemViewModel(alarm: MyAlarm, bus: Bus) : AlarmListItemViewModel {
            val listItem: AlarmListItemViewModel = AlarmListItemViewModel(bus)
            alarmToListItemViewModel(alarm, listItem)
            return listItem
        }

        fun alarmToListItemViewModel(alarm: MyAlarm, viewModel: AlarmListItemViewModel) {
            viewModel.id = alarm.id

            viewModel.monDay.set(alarm.monDay)
            viewModel.thursDay.set(alarm.thursDay)
            viewModel.tuesDay.set(alarm.tuesDay)
            viewModel.wednesDay.set(alarm.wednesDay)
            viewModel.friDay.set(alarm.friDay)
            viewModel.saturDay.set(alarm.saturDay)
            viewModel.sunDay.set(alarm.sunDay)

            viewModel.endTime = alarm.completedAt

            viewModel.snooze.set(alarm.snooze)
            viewModel.snoozeCount.set(alarm.snoozeCount ?: 0)
            viewModel.snoozeInterval.set(alarm.snoozeInterval ?: 0)
            viewModel.enable.set(alarm.enable)
        }

        fun alarmToDetailViewModel(alarm: MyAlarm, viewModel: AlarmDetailViewModel) {
            viewModel.endTime.set(alarm.completedAt)

            viewModel.repeat = alarm.repeat
            viewModel.monDay.set(alarm.monDay)
            viewModel.thursDay.set(alarm.thursDay)
            viewModel.tuesDay.set(alarm.tuesDay)
            viewModel.wednesDay.set(alarm.wednesDay)
            viewModel.friDay.set(alarm.friDay)
            viewModel.saturDay.set(alarm.saturDay)
            viewModel.sunDay.set(alarm.sunDay)

            viewModel.vibration.set(alarm.vibration)
            viewModel.sound = alarm.media
            viewModel.mediaSrc = alarm.mediaSrc ?: ""
            viewModel.soundVolume.set(alarm.volume ?: DEFAULT_VOLUME)

            viewModel.snooze = alarm.snooze
            viewModel.snoozeInterval.set(alarm.snoozeInterval ?: 0)
            viewModel.snoozeCount.set(alarm.snoozeCount ?: 0)
        }

        fun detailViewModelToAlarm(viewModel: AlarmDetailViewModel, id: Int) : MyAlarm {
            val alarm: MyAlarm = MyAlarm()
            detailViewModelToAlarm(viewModel, id, alarm)
            return alarm
        }

        fun detailViewModelToAlarm(viewModel: AlarmDetailViewModel, id: Int, alarm: MyAlarm) {
            alarm.id = id

            alarm.createdAt = Date()

            val calendar: GregorianCalendar = GregorianCalendar()
            calendar.time = viewModel.endTime.get()
            calendar.set(GregorianCalendar.SECOND, 0)
            calendar.set(GregorianCalendar.MILLISECOND, 0)
            alarm.completedAt = calendar.time

            alarm.repeat = viewModel.repeat
            if(alarm.repeat) {
                alarm.monDay = viewModel.monDay.get()
                alarm.tuesDay = viewModel.tuesDay.get()
                alarm.wednesDay = viewModel.wednesDay.get()
                alarm.thursDay = viewModel.thursDay.get()
                alarm.friDay = viewModel.friDay.get()
                alarm.saturDay = viewModel.saturDay.get()
                alarm.sunDay = viewModel.sunDay.get()
                if(!alarm.monDay and !alarm.tuesDay and !alarm.wednesDay and !alarm.thursDay and !alarm.friDay and !alarm.saturDay and !alarm.sunDay) {
                    alarm.repeat = false
                }
            }

            alarm.snooze = viewModel.snooze
            if(alarm.snooze) {
                alarm.snoozeInterval = viewModel.snoozeInterval.get()
                alarm.snoozeCount = viewModel.snoozeCount.get()
            } else {
                alarm.snoozeInterval = 0
                alarm.snoozeCount = 0
            }
            alarm.usedSnoozeCount = 0

            alarm.mediaSrc = viewModel.mediaSrc
            alarm.media = viewModel.sound
            alarm.volume = viewModel.soundVolume.get()
            alarm.vibration = viewModel.vibration.get()

            alarm.enable = true
        }

        fun timerToListItemViewModel(newTimer: MyTimer, bus: Bus, isAddedInUI: Boolean) : TimerListItemViewModel {
            val listItem: TimerListItemViewModel = TimerListItemViewModel(bus)
            timerToListItemViewModel(newTimer, listItem, isAddedInUI)
            return listItem
        }

        fun timerToListItemViewModel(newTimer: MyTimer, bus: Bus) : TimerListItemViewModel {
            val listItem: TimerListItemViewModel = TimerListItemViewModel(bus)
            timerToListItemViewModel(newTimer, listItem)
            return listItem
        }

        fun timerToListItemViewModel(timer: MyTimer, item: TimerListItemViewModel) {
            timerToListItemViewModel(timer, item, false)
        }

        fun timerToListItemViewModel(timer: MyTimer, item: TimerListItemViewModel, isAddedInUI: Boolean) {
            item.id = timer.id

            var remainSeconds = timer.remainSeconds
            if(timer.activated and !isAddedInUI) {
                remainSeconds = (timer.completedAt!!.time - Date().time) / 1000
            }
            item.hour.set(TimeCalculator.getHourFromSeconds(remainSeconds).toInt())
            item.minute.set(TimeCalculator.getMinuteFromSeconds(remainSeconds).toInt())
            item.second.set(TimeCalculator.getSecondFromSeconds(remainSeconds).toInt())

            item.wholeTimeInSecond = remainSeconds
            item.baseTime = timer.seconds

            item.calculateProgressedTime()

            item.setActivated(timer.activated)

        }

        fun viewModelToTimer(viewModel: TimerViewModel, id: Int): MyTimer? {
            val timer: MyTimer = MyTimer()
            timer.id = id

            var hourInt: Int = 0
            var minInt: Int = 0
            var secInt: Int = 0
            if (!viewModel.hour.get().isEmpty()) {
                hourInt = viewModel.hour.get().toInt()
            }
            if (!viewModel.minute.get().isEmpty()) {
                minInt = viewModel.minute.get().toInt()
            }
            if (!viewModel.second.get().isEmpty()) {
                secInt = viewModel.second.get().toInt()
            }
            timer.seconds = (hourInt * viewModel.HOUR_IN_SECONDS).toLong() + (minInt * viewModel.MINUTE_IN_SECONDS).toLong() + secInt.toLong()
            timer.remainSeconds = timer.seconds

            timer.createdAt = Date()
            timer.completedAt = DateUtil.getAfterDate(timer.createdAt!!, timer.seconds.toInt() * 1000)

            timer.activated = true

            return timer
        }

        fun alarmToAlarmOnViewModel(alarm: MyAlarm, viewModel: AlarmOnViewModel) {
            val calendar: GregorianCalendar = GregorianCalendar()
            if(calendar.get(GregorianCalendar.HOUR_OF_DAY) < 12) {
                viewModel.isAm.set(true)
            }
            viewModel.hour.set(calendar.get(GregorianCalendar.HOUR_OF_DAY))
            viewModel.minute.set(calendar.get(GregorianCalendar.MINUTE))
            viewModel.day.set(calendar.get(GregorianCalendar.DAY_OF_MONTH))
            viewModel.dayOfWeek.set(calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1)
            viewModel.month.set(calendar.get(GregorianCalendar.MONTH) + 1)

            viewModel.vibration = alarm.vibration
            viewModel.sound = alarm.media
            viewModel.mediaSrc = alarm.mediaSrc ?: ""
            viewModel.soundVolume = alarm.volume ?: DEFAULT_VOLUME

            viewModel.snoozeInterval = alarm.snoozeInterval ?: 0
            viewModel.snoozeCount = alarm.snoozeCount ?: 0
            viewModel.usedSnoozeCount = alarm.usedSnoozeCount ?: 0
            viewModel.snooze.set(alarm.snooze and (viewModel.usedSnoozeCount <= viewModel.snoozeCount))

            viewModel.enable = alarm.enable
        }

        fun timerToTimerOnViewModel(timer: MyTimer, viewModel: TimerOnViewModel) {
            viewModel.timerId = timer.id

            viewModel.hour.set(TimeCalculator.getHourFromSeconds(timer.seconds).toInt())
            viewModel.minute.set(TimeCalculator.getMinuteFromSeconds(timer.seconds).toInt())
            viewModel.second.set(TimeCalculator.getSecondFromSeconds(timer.seconds).toInt())
        }
    }
}