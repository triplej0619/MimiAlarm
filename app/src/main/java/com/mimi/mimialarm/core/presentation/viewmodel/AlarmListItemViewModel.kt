package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableBoolean
import com.mimi.mimialarm.BR

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel : BaseViewModel() {
    var monday: ObservableBoolean = ObservableBoolean(true)
    var tuesDay: ObservableBoolean = ObservableBoolean(true)
    var wednesDay: ObservableBoolean = ObservableBoolean(true)
    var thurseDay: ObservableBoolean = ObservableBoolean(true)
    var friDay: ObservableBoolean = ObservableBoolean(true)
    var saturDay: ObservableBoolean = ObservableBoolean(true)
    var sunDay: ObservableBoolean = ObservableBoolean(true)
}