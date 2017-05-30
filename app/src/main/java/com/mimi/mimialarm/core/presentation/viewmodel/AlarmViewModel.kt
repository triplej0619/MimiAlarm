package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.mimi.mimialarm.core.model.MyAlarm
import com.squareup.otto.Bus
import io.realm.Realm
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel @Inject constructor(private val bus: Bus) : BaseViewModel() {

    var alarmList: MutableList<AlarmListItemViewModel> = ArrayList()
    private var realm: Realm by Delegates.notNull()

    init {
        alarmList.add(AlarmListItemViewModel())
        alarmList.add(AlarmListItemViewModel())
        alarmList.add(AlarmListItemViewModel())

        alarmList.get(0).friDay = ObservableBoolean(false)
        alarmList.get(0).tuesDay = ObservableBoolean(false)
        alarmList.get(2).monday = ObservableBoolean(false)
        alarmList.get(2).tuesDay = ObservableBoolean(false)
        alarmList.get(2).wednesDay = ObservableBoolean(false)
        alarmList.get(2).thurseDay = ObservableBoolean(false)

        realm = Realm.getDefaultInstance()
    }

    fun release() {
    }

    fun clickListItem(position: Int) {
        showAlarmDetailView()
    }

    fun showAlarmDetailView() {
        bus.post(StartAlarmDetailActivityEvent())
    }
}