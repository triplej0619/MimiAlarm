package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmViewModel : BaseViewModel() {
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
}