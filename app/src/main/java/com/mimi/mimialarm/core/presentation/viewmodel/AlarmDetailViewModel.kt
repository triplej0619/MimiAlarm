package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.AddAlarmEvent
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.UpdateAlarmEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import java.util.*
import javax.inject.Inject



/**
 * Created by MihyeLee on 2017. 5. 29..
 */
class AlarmDetailViewModel @Inject constructor(
        private val uiManager: UIManager,
        private val bus: Bus,
        private val dbManager: DBManager,
        private val alarmManager: AlarmManager
) : BaseViewModel() {

    val DEFAULT_VOLUME: Int = 80

    var savedAlarm: MyAlarm? = null
    var id: Int? = null

    var endTime: ObservableField<Date> = ObservableField<Date>(Date())
    var repeat: ObservableBoolean = ObservableBoolean(true)
    val repeatLive: LiveData<Boolean> = MutableLiveData()
    var snooze: ObservableBoolean = ObservableBoolean(true)
    var snoozeInterval: ObservableInt = ObservableInt(0)
    var snoozeCount: ObservableInt = ObservableInt(0)

    var mediaSrc: String = ""
    var sound: ObservableBoolean = ObservableBoolean(true)
    var vibration: ObservableBoolean = ObservableBoolean(true)
    var soundVolume: ObservableInt = ObservableInt(DEFAULT_VOLUME)

//    var monDay: Boolean = true
//    @Bindable get() = field
//    set(value) {
//        field = value
//        notifyPropertyChanged(BR.monDay)
//    }
    var monDay: ObservableBoolean = ObservableBoolean(true)
    var tuesDay: ObservableBoolean = ObservableBoolean(true)
    var wednesDay: ObservableBoolean = ObservableBoolean(true)
    var thursDay: ObservableBoolean = ObservableBoolean(true)
    var friDay: ObservableBoolean = ObservableBoolean(true)
    var saturDay: ObservableBoolean = ObservableBoolean(false)
    var sunDay: ObservableBoolean = ObservableBoolean(false)

    fun release() {
    }

    val changeMondayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            monDay.set(!monDay.get())
        }
    }

    val changeTuesDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            tuesDay.set(!tuesDay.get())
        }
    }

    val changeWednesDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            wednesDay.set(!wednesDay.get())
        }
    }

    val changeThursDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            thursDay.set(!thursDay.get())
        }
    }

    val changeFriDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            friDay.set(!friDay.get())
        }
    }

    val changeSaturDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            saturDay.set(!saturDay.get())
        }
    }

    val changeSunDayStatus: Command = object : Command {
        override fun execute(arg: Any) {
            sunDay.set(!sunDay.get())
        }
    }

    val finishViewCommand: Command = object : Command {
        override fun execute(arg: Any) {
            closeView()
        }
    }

    val addOrUpdateAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            if(id == null) {
                addAlarm()
            } else {
                updateAlarm();
            }
        }
    }

    fun loadAlarmData() {
        LogUtils.printDebugLog(this@AlarmDetailViewModel.javaClass, "loadAlarmData")

        val alarm: MyAlarm? = dbManager.findAlarmWithId(id)
        alarm?.let { DataMapper.alarmToDetailViewModel(alarm, this) }
    }

    fun addAlarm() {
        LogUtils.printDebugLog(this@AlarmDetailViewModel.javaClass, "addAlarm")

        saveAlarmInDB()
        savedAlarm?.let {
            val time = TimeCalculator.getMilliSecondsForScheduling(savedAlarm!!)
            alarmManager.startAlarm(id!!, time)
            bus.post(AddAlarmEvent(id, time / 1000))
        }
        closeView()
    }
    
    fun updateAlarm() {
        LogUtils.printDebugLog(this@AlarmDetailViewModel.javaClass, "updateAlarm")

        updateAlarmInDB()
        alarmManager.cancelAlarm(savedAlarm?.id!!)
        savedAlarm?.let {
            val time = TimeCalculator.getMilliSecondsForScheduling(savedAlarm!!)
            alarmManager.startAlarm(id!!, time)
            bus.post(UpdateAlarmEvent(time / 1000))
        }
        closeView()
    }

    fun updateAlarmInDB() {
        LogUtils.printDebugLog(this@AlarmDetailViewModel.javaClass, "updateAlarmInDB")
        val alarm: MyAlarm = DataMapper.detailViewModelToAlarm(this, id!!)
        dbManager.updateAlarm(alarm)
        savedAlarm = alarm
    }

    fun saveAlarmInDB() {
        LogUtils.printDebugLog(this@AlarmDetailViewModel.javaClass, "saveAlarmInDB")

        id = dbManager.getNextAlarmId()
        val alarm: MyAlarm = DataMapper.detailViewModelToAlarm(this, id!!)
        dbManager.addAlarm(alarm)
        savedAlarm = alarm
    }

    fun closeView() {
        uiManager.finishForegroundActivity()
    }
}