package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.MyAlarm
import com.mimi.mimialarm.core.utils.Command
import io.realm.Realm
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates



/**
 * Created by MihyeLee on 2017. 5. 29..
 */
class AlarmDetailViewModel @Inject constructor(private val uiManager: UIManager) : BaseViewModel() {

    var endTime: ObservableField<Date> = ObservableField<Date>()
    var repeat: ObservableBoolean = ObservableBoolean(true)
    var snooze: ObservableBoolean = ObservableBoolean(true)
    var snoozeInterval: ObservableInt = ObservableInt(0)
    var snoozeCount: ObservableInt = ObservableInt(0)

    var mediaIndex: ObservableInt = ObservableInt(0)
    var mediaSrc: ObservableField<String> = ObservableField<String>("")
    var sound: ObservableBoolean = ObservableBoolean(true)
    var vibration: ObservableBoolean = ObservableBoolean(true)

//    var monday: Boolean = true
//    @Bindable get() = field
//    set(value) {
//        field = value
//        notifyPropertyChanged(BR.monday)
//    }
    var monDay: ObservableBoolean = ObservableBoolean(true)
    var tuesDay: ObservableBoolean = ObservableBoolean(true)
    var wednesDay: ObservableBoolean = ObservableBoolean(true)
    var thursDay: ObservableBoolean = ObservableBoolean(true)
    var friDay: ObservableBoolean = ObservableBoolean(true)
    var saturDay: ObservableBoolean = ObservableBoolean(false)
    var sunDay: ObservableBoolean = ObservableBoolean(false)

    var realm: Realm by Delegates.notNull()

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
        realm.close()
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

    val addAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            addAlarm()
        }
    }

    fun addAlarm() {
        saveAlarm()
        closeView()
    }

    fun saveAlarm() {
        realm.executeTransaction {
            val currentIdNum = realm.where(MyAlarm::class.java).max(MyAlarm.FIELD_ID)

            val newAlarm = realm.createObject(MyAlarm::class.java, currentIdNum ?: 0)
            newAlarm.createdAt = Date()
            newAlarm.completedAt = endTime.get()

            newAlarm.monday = monDay.get()
            newAlarm.tuesDay = tuesDay.get()
            newAlarm.wednesDay = wednesDay.get()
            newAlarm.thurseDay = thursDay.get()
            newAlarm.friDay = friDay.get()
            newAlarm.saturDay = saturDay.get()
            newAlarm.sunDay = sunDay.get()

            newAlarm.snoozeInterval = snoozeInterval.get()
            newAlarm.snoozeCount = snoozeCount.get()

            newAlarm.mediaSrc = mediaSrc.get()

            newAlarm.enable = true
        }
    }

    fun closeView() {
//        bus.post(FinishForegroundActivityEvent())
        uiManager.finishForegroundActivity()
    }
}