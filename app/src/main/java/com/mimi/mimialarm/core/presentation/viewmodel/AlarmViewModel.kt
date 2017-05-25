package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.squareup.otto.Bus

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel : BaseViewModel {
    lateinit var bus: Bus

    var alarmList: MutableList<AlarmListItemViewModel> = ArrayList()

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

//        alarmList.get(0).friDay = false
//        alarmList.get(0).tuesDay = false
//        alarmList.get(2).monday = false
//        alarmList.get(2).tuesDay = false
//        alarmList.get(2).wednesDay = false
//        alarmList.get(2).thurseDay = false
    }

    constructor(bus: Bus) {
        bus.register(this)
    }

    fun release() {
        bus.unregister(this)
    }

    fun clickListItem(position: Int) {
        showAlarmDetailView()
    }

    fun showAlarmDetailView() {
        bus.post(StartAlarmDetailActivityEvent())
    }
}