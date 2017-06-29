package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
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

    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var alarmCount: ObservableInt = ObservableInt(0)
    var alarmListLive: MutableLiveData<ArrayList<AlarmListItemViewModel>> = MutableLiveData()
    var alarmList: ArrayList<AlarmListItemViewModel> = ArrayList<AlarmListItemViewModel>()
    var realm: Realm by Delegates.notNull()

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
            deleteMode.set(false)
            setDeleteMode(false)
        }
    }

    val deleteAlarmsCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("정말 삭제하시겠습니까?", "", true, object: Command { // TODO text -> resource
                override fun execute(arg: Any) {
                    deleteAlarms()
                }
            }, null)
        }
    }

    val deleteAllCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("전부 삭제하시겠습니까?", "", true, object: Command { // TODO text -> resource
                override fun execute(arg: Any) {
                    deleteAllAlarm()
                }
            }, null)
        }
    }

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
        realm.close()
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
        val results: RealmResults<MyAlarm> = realm.where(MyAlarm::class.java).findAll()
        for (result in results) {
            updateOrInsertListItem(realm.copyFromRealm(result))
        }
        alarmListLive.postValue(alarmList)
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
        for (item in alarmList) {
            if (item.selectForDelete.get()) {
                realm.executeTransaction {
                    val alarm: MyAlarm = realm.where(MyAlarm::class.java).equalTo(MyAlarm.FIELD_ID, item.id).findFirst()
                    alarm.deleteFromRealm()
                }
            }
        }
        reLoadAlarmList()
        cancelDeleteModeCommand.execute(Unit)
    }

    fun deleteAllAlarm() {
        realm.executeTransaction {
            val alarm = realm.where(MyAlarm::class.java).findAll()
            alarm.deleteAllFromRealm()
        }
        reLoadAlarmList()
        cancelDeleteModeCommand.execute(Unit)
    }
}