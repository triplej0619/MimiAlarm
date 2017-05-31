package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.*
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel : BaseViewModel() {
    var monday: ObservableBoolean = ObservableBoolean(false)
    var tuesDay: ObservableBoolean = ObservableBoolean(false)
    var wednesDay: ObservableBoolean = ObservableBoolean(false)
    var thurseDay: ObservableBoolean = ObservableBoolean(false)
    var friDay: ObservableBoolean = ObservableBoolean(false)
    var saturDay: ObservableBoolean = ObservableBoolean(false)
    var sunDay: ObservableBoolean = ObservableBoolean(false)

    var endTime: ObservableField<Date> = ObservableField<Date>()
    var snooze: ObservableBoolean = ObservableBoolean(false)
    var snoozeInterval: ObservableInt = ObservableInt(0)
    var snoozeCount: ObservableInt = ObservableInt(0)

    var isEnable: ObservableBoolean = ObservableBoolean(true)
}