package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.mimialarm.core.model.MyTimer
import com.mimi.mimialarm.core.utils.Command
import java.util.*

/**
 * Created by MihyeLee on 2017. 6. 22..
 */
class TimerListItemViewModel : BaseViewModel() {

    companion object {
        fun timerDataToTimerListItem(newTimer: MyTimer, item: TimerListItemViewModel) {
            item.id = newTimer.id
            item.activated = newTimer.activated

            if(newTimer.activated) {
                val diff: Long = newTimer.completedAt!!.time - Date().time

                val hour: Long = diff / (60 * 60)
                val minute: Long = (diff % (60 * 60)) / 60
                val second: Long = diff % 60
                item.hour.set(hour.toInt())
                item.minute.set(minute.toInt())
                item.second.set(second.toInt())
            } else {
                item.hour.set(newTimer.seconds!! / (60 * 60))
                item.minute.set((newTimer.seconds!! % (60 * 60)) / 60)
                item.second.set(newTimer.seconds!! % 60)
            }
        }

        fun timerDataToTimerListItem(newTimer: MyTimer) : TimerListItemViewModel {
            val item: TimerListItemViewModel = TimerListItemViewModel()
            timerDataToTimerListItem(newTimer, item)
            return item
        }
    }

    fun copyFromTimer(timer: MyTimer) {
        timerDataToTimerListItem(timer, this)
    }

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