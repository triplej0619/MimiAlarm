package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.StopAlarmItemEvent
import com.mimi.mimialarm.core.model.DataMapper
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 26..
 */
class ActivatedAlarmListViewModel @Inject constructor(
        private val dbManager: DBManager,
        private val alarmManager: AlarmManager,
        private val bus: Bus
) : BaseViewModel() {
    var alarmCount: ObservableInt = ObservableInt(0)
    var alarmList: ArrayList<AlarmListItemViewModel> = ArrayList<AlarmListItemViewModel>()
    var alarmListLive: MutableLiveData<ArrayList<AlarmListItemViewModel>> = MutableLiveData()

    init {
        bus.register(this)
    }

    fun release() {
        bus.unregister(this)
    }

    fun loadAlarmList() {
        LogUtils.printDebugLog(this@ActivatedAlarmListViewModel.javaClass, "loadAlarmList()")
        val alarms: List<MyAlarm> = dbManager.findAllAlarm().filter { it.usedSnoozeCount!! > 0 }
        alarms.forEach { alarmList.add(DataMapper.alarmToListItemViewModel(it, bus)) }
        alarmListLive.postValue(alarmList)
        alarmCount.set(alarms.size)
    }

    @Subscribe
    fun answerStopAlarmItemEvent(event: StopAlarmItemEvent) {
        LogUtils.printDebugLog(this@ActivatedAlarmListViewModel.javaClass, "answerStopAlarmItemEvent() : " + event.id)

        alarmManager.cancelAlarm(event.id)

        val alarm = dbManager.findAlarmWithId(event.id)
        alarm?.let {
            alarm.usedSnoozeCount = 0
            dbManager.updateAlarm(alarm)
        }
        alarmList.remove(alarmList.find { it.id == event.id })
        alarmListLive.postValue(alarmList)
        alarmCount.set(alarmList.size)
    }
}