package com.mimi.mimialarm.core.model

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.TimerListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.TimerViewModel
import java.util.*

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class DataMapper {
    companion object {
        val DEFAULT_VOLUME: Int = 80

        fun alarmToListItemViewModel(alarm: MyAlarm) : AlarmListItemViewModel {
            val listItem: AlarmListItemViewModel = AlarmListItemViewModel()
            alarmToListItemViewModel(alarm, listItem)
            return listItem
        }

        fun alarmToListItemViewModel(alarm: MyAlarm, viewModel: AlarmListItemViewModel) {
            viewModel.id = alarm.id

            viewModel.friDay.set(alarm.friDay ?: false)
            viewModel.monDay.set(alarm.monDay ?: false)
            viewModel.thursDay.set(alarm.thursDay ?: false)
            viewModel.tuesDay.set(alarm.tuesDay ?: false)
            viewModel.wednesDay.set(alarm.wednesDay ?: false)
            viewModel.saturDay.set(alarm.saturDay ?: false)
            viewModel.sunDay.set(alarm.sunDay ?: false)

            viewModel.endTime = alarm.completedAt

            if (alarm.snoozeCount != null && alarm.snoozeInterval != null) {
                viewModel.snooze.set(true)
            }
            viewModel.snoozeCount.set(alarm.snoozeCount ?: 0)
            viewModel.snoozeInterval.set(alarm.snoozeInterval ?: 0)
            viewModel.isEnable.set(alarm.enable ?: false)
        }

        fun alarmToDetailViewModel(alarm: MyAlarm, viewModel: AlarmDetailViewModel) {
            viewModel.endTime.set(alarm.completedAt)

            viewModel.friDay.set(alarm.friDay ?: false)
            viewModel.monDay.set(alarm.monDay ?: false)
            viewModel.tuesDay.set(alarm.tuesDay ?: false)
            viewModel.wednesDay.set(alarm.wednesDay ?: false)
            viewModel.thursDay.set(alarm.thursDay ?: false)
            viewModel.saturDay.set(alarm.saturDay ?: false)
            viewModel.sunDay.set(alarm.sunDay ?: false)

            viewModel.vibration.set(alarm.vibration ?: false)
            viewModel.sound.set(alarm.media ?: false)
            viewModel.mediaSrc = alarm.mediaSrc ?: ""
            viewModel.soundVolume.set(alarm.volume ?: DEFAULT_VOLUME)

            viewModel.snooze.set(alarm.snooze ?: false)
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
            alarm.completedAt = viewModel.endTime.get()

            alarm.repeat = viewModel.repeat.get()
            alarm.monDay = viewModel.monDay.get()
            alarm.tuesDay = viewModel.tuesDay.get()
            alarm.wednesDay = viewModel.wednesDay.get()
            alarm.thursDay = viewModel.thursDay.get()
            alarm.friDay = viewModel.friDay.get()
            alarm.saturDay = viewModel.saturDay.get()
            alarm.sunDay = viewModel.sunDay.get()

            alarm.snooze = viewModel.snooze.get()
            alarm.snoozeInterval = viewModel.snoozeInterval.get()
            alarm.snoozeCount = viewModel.snoozeCount.get()

            alarm.mediaSrc = viewModel.mediaSrc
            alarm.media = viewModel.sound.get()
            alarm.volume = viewModel.soundVolume.get()
            alarm.vibration = viewModel.vibration.get()

            alarm.enable = true
        }

        fun timerToListItemViewModel(newTimer: MyTimer) : TimerListItemViewModel {
            val listItem: TimerListItemViewModel = TimerListItemViewModel()
            timerToListItemViewModel(newTimer, listItem)
            return listItem
        }

        fun timerToListItemViewModel(newTimer: MyTimer, item: TimerListItemViewModel) {
            item.id = newTimer.id
            item.activated = newTimer.activated

            if(newTimer.activated) {
                val diff: Long = newTimer.completedAt!!.time - Date().time

                val hour: Long = diff / (60 * 60)
                val minute: Long = (diff % (60 * 60)) / 60
                val second: Long = diff % 60
                item.hour.set(hour.toInt())
                item.minute.set(minute.toInt())
                item.second.set(second.toInt())
            } else {
                item.hour.set(newTimer.seconds!! / (60 * 60))
                item.minute.set((newTimer.seconds!! % (60 * 60)) / 60)
                item.second.set(newTimer.seconds!! % 60)
            }
        }

        fun viewModelToTimer(viewModel: TimerViewModel, id: Int): MyTimer? {
            val timer: MyTimer = MyTimer()
            timer.id = id
            timer.createdAt = Date()

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
            timer.seconds = (hourInt * viewModel.HOUR_IN_SECONDS) + (minInt * viewModel.MINUTE_IN_SECONDS) + secInt
            if (timer.seconds == 0) {
                return null;
            }

            val calendar: GregorianCalendar = GregorianCalendar()
            calendar.time = timer.createdAt
            calendar.add(GregorianCalendar.HOUR, hourInt)
            calendar.add(GregorianCalendar.MINUTE, minInt)
            calendar.add(GregorianCalendar.SECOND, secInt)
            timer.completedAt = calendar.time

            timer.activated = false

            return timer
        }
    }
}