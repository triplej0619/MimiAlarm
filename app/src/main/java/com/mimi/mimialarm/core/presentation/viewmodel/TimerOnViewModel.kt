package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.FailedLoadDataEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class TimerOnViewModel(
        private val uiManager: UIManager,
        private val dbManager: DBManager,
        private val bus: Bus
) : BaseViewModel() {

    var timerId: Int? = null
    var hour: ObservableInt = ObservableInt(0)
    var minute: ObservableInt = ObservableInt(0)
    var second: ObservableInt = ObservableInt(0)

    val finishViewCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.finishForegroundView()
        }
    }

    val startCommand: Command = object : Command {
        override fun execute(arg: Any) {
            loadAndDeactivatedTimer()
        }
    }

    fun loadAndDeactivatedTimer() {
        LogUtil.printDebugLog(this@TimerOnViewModel.javaClass, "loadAndDeactivatedTimer : $timerId")

        if(timerId != null) {
            val timer: MyTimer? = dbManager.findTimerWithId(timerId)
            if(timer != null) {
                DataMapper.timerToTimerOnViewModel(timer, this)

                timer.activated = false
                timer.remainSeconds = timer.seconds
                dbManager.updateTimer(timer)
            } else {
                bus.post(FailedLoadDataEvent(FailedLoadDataEvent.DATA_TYPE.TIMER))
            }
        }
    }

}