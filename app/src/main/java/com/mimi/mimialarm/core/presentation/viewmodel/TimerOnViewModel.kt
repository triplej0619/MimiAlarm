package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class TimerOnViewModel(
        private val uiManager: UIManager,
        private val dbManager: DBManager
) : BaseViewModel() {

    val DEFAULT_VOLUME: Int = 70

    var timerId: Int? = null
    var hour: ObservableInt = ObservableInt(0)
    var minute: ObservableInt = ObservableInt(0)
    var second: ObservableInt = ObservableInt(0)

    val finishViewCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.finishForegroundActivity()
        }
    }

    val startCommand: Command = object : Command {
        override fun execute(arg: Any) {
            loadAndDeactivatedTimer()
        }
    }

    fun loadAndDeactivatedTimer() {
        LogUtils.printDebugLog(this@TimerOnViewModel.javaClass, "loadAndDeactivatedTimer")

        if(timerId != null) {
            val timer: MyTimer? = dbManager.findTimerWithId(timerId)
            timer?.let {
                DataMapper.timerToTimerOnViewModel(timer, this)

                timer.activated = false
                timer.remainSeconds = timer.seconds
                dbManager.updateTimer(timer)
            }
        }
    }

}