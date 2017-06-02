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

    val addAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            showAddAlarmView()
        }
    }

    var realm: Realm by Delegates.notNull()

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
        realm.close()
    }

    fun loadAlarmList() {
        val results: RealmResults<MyAlarm> = realm.where(MyAlarm::class.java).findAll()
        for (result in results) {
            updateOrInsertListItem(realm.copyFromRealm(result))
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
        uiManager.startAlarmDetailActivityForNew()
    }

    fun clickListItem(position: Int) {
        showAlarmDetailView(position)
    }

    fun showAlarmDetailView(position: Int) {
        uiManager.startAlarmDetailActivityForUpdate(alarmList[position].id)
    }
}