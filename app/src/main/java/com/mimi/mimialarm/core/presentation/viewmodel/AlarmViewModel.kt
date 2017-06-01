package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableInt
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.MyAlarm
import com.mimi.mimialarm.core.utils.Command
import io.realm.Realm
import io.realm.RealmResults
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel @Inject constructor(private val uiManager: UIManager) : BaseViewModel() {

    var alarmCount: ObservableInt = ObservableInt(0)
    var alarmList: MutableList<AlarmListItemViewModel> = ArrayList<AlarmListItemViewModel>()
    private var realm: Realm by Delegates.notNull()

    val addAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            showAddAlarmView()
        }
    }

    init {
        realm = Realm.getDefaultInstance()
//        loadAlarmList();
    }

    fun release() {
        realm.close()
    }

    fun loadAlarmList() {
        val results: RealmResults<MyAlarm> = realm.where(MyAlarm::class.java).findAll()
        for (result in results) {
            updateOrInsertListItem(result)
        }
        alarmCount.set(results.size)
    }

    fun updateOrInsertListItem(alarm: MyAlarm) {
        for (item in alarmList) {
            if (item.id?.equals(alarm.id) ?: false) {
                item.copyFromAlarm(alarm)
                return
            }
        }

        alarmList.add(AlarmListItemViewModel.alarmDataToAlarmListItem(alarm))
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