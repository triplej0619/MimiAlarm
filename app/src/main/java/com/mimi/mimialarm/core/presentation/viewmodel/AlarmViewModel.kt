package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ChangeAlarmStatusEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel @Inject constructor(
        private val uiManager: UIManager,
        private val dbManager: DBManager,
        private val alarmManager: AlarmManager,
        private val bus: Bus
) : BaseViewModel() {

    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var alarmCount: ObservableInt = ObservableInt(0)
    var alarmListLive: MutableLiveData<ArrayList<AlarmListItemViewModel>> = MutableLiveData()
    var alarmList: ArrayList<AlarmListItemViewModel> = ArrayList<AlarmListItemViewModel>()

    val addAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            showAddAlarmView()
        }
    }

    val startDeleteModeCommand: Command = object : Command {
        override fun execute(arg: Any) {
            deleteMode.set(true)
            setDeleteMode(true)
        }
    }

    val cancelDeleteModeCommand: Command = object : Command {
        override fun execute(arg: Any) {
            if(deleteMode.get()) {
                deleteMode.set(false)
                setDeleteMode(false)
            }
        }
    }

    val deleteAlarmsCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("정말 삭제하시겠습니까?", "", true, object: Command { // TODO text -> string resource
                override fun execute(arg: Any) {
                    deleteAlarms()
                }
            }, null)
        }
    }

    val deleteAllCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("전부 삭제하시겠습니까?", "", true, object: Command { // TODO text -> string resource
                override fun execute(arg: Any) {
                    deleteAllAlarm()
                }
            }, null)
        }
    }

    init {
        bus.register(this)
    }

    fun release() {
        bus.unregister(this)
    }

    fun clear() {
        alarmList.clear()
        alarmCount.set(0)
        alarmListLive.postValue(alarmList)
    }

    fun reLoadAlarmList() {
        clear()
        loadAlarmList()
    }

    fun loadAlarmList() {
        val alarms: List<MyAlarm> = dbManager.findAllAlarm()
        for (alarm in alarms) {
            updateOrInsertListItem(alarm)
        }
        alarmListLive.postValue(alarmList)
        alarmCount.set(alarms.size)
    }

    fun updateOrInsertListItem(alarm: MyAlarm) {
        for (item in alarmList) {
            if (item.id?.equals(alarm.id) ?: false) {
                DataMapper.alarmToListItemViewModel(alarm, item)
                return
            }
        }

        alarmList.add(DataMapper.alarmToListItemViewModel(alarm, bus))
    }

    fun showAddAlarmView() {
        uiManager.startAlarmDetailActivityForNew()
    }

    fun clickListItem(position: Int) {
        if(!deleteMode.get()) {
            showAlarmDetailView(position)
        } else {
            alarmList[position].selectForDelete.set(!alarmList[position].selectForDelete.get())
        }
    }

    fun showAlarmDetailView(position: Int) {
        uiManager.startAlarmDetailActivityForUpdate(alarmList[position].id)
    }

    fun setDeleteMode(mode: Boolean) {
        for(listItem in alarmList) {
            listItem.deleteMode.set(mode)
            listItem.selectForDelete.set(false)
        }
    }

    fun deleteAlarms() {
        if(alarmList.filter { it.selectForDelete.get() }.isEmpty()) {
            uiManager.showToast("선택된 알람이 없습니다.") // TODO text -> resource
        } else {
            alarmList
                    .filter { it.selectForDelete.get() }
                    .forEach {
                        dbManager.deleteAlarmWithId(it.id)
                        if (it.enable.get()) {
                            alarmManager.cancelAlarm(it.id!!)
                        }
                    }
            reLoadAlarmList()
        }
        cancelDeleteModeCommand.execute(Unit)
    }

    fun deleteAllAlarm() {
        alarmList
                .filter { it.selectForDelete.get() }
                .forEach {
                    if(it.enable.get()) {
                        alarmManager.cancelAlarm(it.id!!)
                    }
                }
        dbManager.deleteAllAlarm()
        reLoadAlarmList()
        cancelDeleteModeCommand.execute(Unit)
    }

    @Subscribe
    fun answerChangeAlarmStatusEvent(event: ChangeAlarmStatusEvent) {
        if(event.activation) {
            val alarm: MyAlarm? = dbManager.findAlarmWithId(event.id)
            alarm?.let {
                val time = TimeCalculator.getMilliSecondsForScheduling(alarm)
                alarmManager.startAlarm(event.id, time)
            }
        } else {
            val alarm: MyAlarm? = dbManager.findAlarmWithId(event.id)
            alarm?.let {
                alarm.enable = false
                dbManager.updateAlarm(alarm)
            }
            alarmManager.cancelAlarm(event.id)
        }
    }
}