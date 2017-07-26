package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 26..
 */
class ActivatedAlarmListViewModel @Inject constructor(
        private val dbManager: DBManager,
        private val alarmManager: AlarmManager
) : BaseViewModel() {
    var alarmCount: ObservableInt = ObservableInt(0)
}