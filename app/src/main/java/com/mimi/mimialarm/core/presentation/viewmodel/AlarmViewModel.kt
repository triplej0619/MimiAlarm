package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.ChangeAlarmStatusEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.infrastructure.UpdateAlarmEvent
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
    var showActivatedAlarmList = ObservableBoolean(false)

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

    val showActivateAlarmListCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.startActivatedAlarmListView()
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
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "reLoadAlarmList()")
        clear()
        loadAlarmList()
    }

    fun loadAlarmList() {
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "loadAlarmList()")
        val alarms: List<MyAlarm> = dbManager.findAllAlarm()
        for (alarm in alarms) {
            updateOrInsertListItem(alarm)
        }
        showActivatedAlarmList.set(alarms.filter { it.usedSnoozeCount!! > 0 }.isNotEmpty())
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
        uiManager.startAlarmDetailViewForNew()
    }

    fun clickListItem(position: Int) {
        if(!deleteMode.get()) {
            showAlarmDetailView(position)
        } else {
            alarmList[position].selectForDelete.set(!alarmList[position].selectForDelete.get())
        }
    }

    fun showAlarmDetailView(position: Int) {
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "showAlarmDetailView()")
        uiManager.startAlarmDetailViewForUpdate(alarmList[position].id)
    }

    fun setDeleteMode(mode: Boolean) {
        for(listItem in alarmList) {
            listItem.deleteMode.set(mode)
            listItem.selectForDelete.set(false)
        }
    }

    fun deleteAlarms() {
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "deleteAlarms()")
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
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "deleteAllAlarm()")
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
        LogUtils.printDebugLog(this@AlarmViewModel.javaClass, "answerChangeAlarmStatusEvent() activation : " + event.activation)
        val alarm: MyAlarm? = dbManager.findAlarmWithId(event.id)
        alarm?.let {
            alarm.enable = event.activation
            if (event.activation) {
                val time = TimeCalculator.getMilliSecondsForScheduling(alarm)
                alarmManager.startAlarm(event.id, time)
                bus.post(UpdateAlarmEvent(time / 1000))
            } else {
                alarm.usedSnoozeCount = 0
                alarmManager.cancelAlarm(event.id)
            }
            dbManager.updateAlarm(alarm)
        }
    }
}