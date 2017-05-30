package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.mimialarm.android.infrastructure.FinishForegroundActivityEvent
import com.mimi.mimialarm.core.model.MyAlarm
import com.squareup.otto.Bus
import io.realm.Realm
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
class AlarmDetailViewModel @Inject constructor(private val bus: Bus) : BaseViewModel() {

    val endTime: ObservableField<Date> = ObservableField<Date>()
    val snoozeInterval: ObservableInt = ObservableInt()
    val snoozeCount: ObservableInt = ObservableInt()
    val mediaSrc: ObservableField<String> = ObservableField<String>()
    var monday: ObservableBoolean = ObservableBoolean(true)
    var tuesDay: ObservableBoolean = ObservableBoolean(true)
    var wednesDay: ObservableBoolean = ObservableBoolean(true)
    var thurseDay: ObservableBoolean = ObservableBoolean(true)
    var friDay: ObservableBoolean = ObservableBoolean(true)
    var saturDay: ObservableBoolean = ObservableBoolean(true)
    var sunDay: ObservableBoolean = ObservableBoolean(true)

    var days: Set<String> = HashSet<String>()
    var realm: Realm by Delegates.notNull()

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
    }

//    public val addAlarmCommand: Unit = addAlarm()

    fun addAlarm() {
        saveAlarm()
        closeView()
    }

    fun saveAlarm() {
        realm.executeTransaction {
            val newAlarm = realm.createObject(MyAlarm::class.java, 0)
            newAlarm.createdAt = Date()
            newAlarm.completedAt = endTime.get()

            newAlarm.monday = monday.get()
            newAlarm.tuesDay = tuesDay.get()
            newAlarm.wednesDay = wednesDay.get()
            newAlarm.thurseDay = thurseDay.get()
            newAlarm.friDay = friDay.get()
            newAlarm.saturDay = saturDay.get()
            newAlarm.sunDay = sunDay.get()

            newAlarm.snoozeInterval = snoozeInterval.get()
            newAlarm.snoozeCount = snoozeCount.get()

            newAlarm.mediaSrc = mediaSrc.get()
        }
    }

    fun closeView() {
        bus.post(FinishForegroundActivityEvent())
    }
}