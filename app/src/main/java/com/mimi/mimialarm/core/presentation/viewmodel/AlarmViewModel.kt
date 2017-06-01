package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.MyAlarm
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel @Inject constructor(private val uiManager: UIManager) : BaseViewModel() {

    var alarmCount: ObservableInt = ObservableInt(0)
    var alarmList: MutableList<AlarmListItemViewModel> = ArrayList()
    private var realm: Realm by Delegates.notNull()

    val addAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            showAddAlarmView()
        }
    }

    init {
//        alarmList.add(AlarmListItemViewModel())
//        alarmList.add(AlarmListItemViewModel())
//        alarmList.add(AlarmListItemViewModel())
//
//        alarmList.get(0).friDay = ObservableBoolean(false)
//        alarmList.get(0).tuesDay = ObservableBoolean(false)
//        alarmList.get(2).monday = ObservableBoolean(false)
//        alarmList.get(2).tuesDay = ObservableBoolean(false)
//        alarmList.get(2).wednesDay = ObservableBoolean(false)
//        alarmList.get(2).thurseDay = ObservableBoolean(false)

        realm = Realm.getDefaultInstance()
        loadAlarmList();
    }

    fun release() {
        realm.close()
    }

    fun loadAlarmList() {
        var results: RealmResults<MyAlarm> = realm.where(MyAlarm::class.java).findAll()
        for (result in results) {
            alarmList.add(savedDataToAlarmListItem(result))
        }
        alarmCount.set(results.size)
    }

    fun savedDataToAlarmListItem(alarm: MyAlarm): AlarmListItemViewModel {
        var ret: AlarmListItemViewModel = AlarmListItemViewModel()
        ret.friDay.set(alarm.friDay ?: false)
        ret.monday.set(alarm.monday ?: false)
        ret.thurseDay.set(alarm.thurseDay ?: false)
        ret.tuesDay.set(alarm.tuesDay ?: false)
        ret.wednesDay.set(alarm.wednesDay ?: false)
        ret.saturDay.set(alarm.saturDay ?: false)
        ret.sunDay.set(alarm.sunDay ?: false)

        ret.endTime = alarm.completedAt

        if(alarm.snoozeCount != null && alarm.snoozeInterval != null) {
            ret.snooze.set(true)
        }
        ret.snoozeCount.set(alarm.snoozeCount ?: 0)
        ret.snoozeInterval.set(alarm.snoozeInterval ?: 0)
        ret.isEnable.set(alarm.enable ?: false)

        return ret
    }

    fun showAddAlarmView() {
        uiManager.startAlarmDetailActivity()
    }

    fun clickListItem(position: Int) {
        showAlarmDetailView()
    }

    fun showAlarmDetailView() {
//        bus.post(StartAlarmDetailActivityEvent())
        uiManager.startAlarmDetailActivity()
    }
}