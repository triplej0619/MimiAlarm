package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.mimialarm.core.utils.Command
import java.util.*

/**
 * Created by MihyeLee on 2017. 6. 22..
 */
class TimerListItemViewModel : BaseViewModel() {

    var id: Int? = null
    var activated: Boolean = false
    var hour: ObservableInt = ObservableInt(0)
    var minute: ObservableInt = ObservableInt(0)
    var second: ObservableInt = ObservableInt(0)
    var paused: ObservableBoolean = ObservableBoolean(false)
    var stop: ObservableBoolean = ObservableBoolean(false)
    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var selectForDelete: ObservableBoolean = ObservableBoolean(false)

    val changeSelectStatusCommand: Command = object : Command {
        override fun execute(arg: Any) {
            selectForDelete.set(!selectForDelete.get())
        }
    }
}