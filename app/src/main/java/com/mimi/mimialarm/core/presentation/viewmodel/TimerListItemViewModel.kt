package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.ChangeTimerStatusEvent
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by MihyeLee on 2017. 6. 22..
 */
class TimerListItemViewModel(val bus: Bus) : BaseViewModel() {

    var id: Int? = null
    var activated: ObservableBoolean = ObservableBoolean(true)
    var hour: ObservableInt = ObservableInt(0)
    var minute: ObservableInt = ObservableInt(0)
    var second: ObservableInt = ObservableInt(0)
    var wholeTimeInSecond: Long = 0
    var progressed: ObservableInt = ObservableInt(0)
//    var paused: ObservableBoolean = ObservableBoolean(false)
//    var stop: ObservableBoolean = ObservableBoolean(false)
    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var selectForDelete: ObservableBoolean = ObservableBoolean(false)
    var timeSubscription: Disposable? = null
    val timeObservable: Observable<Int>
    var baseTime: Long = 0

    val changeSelectStatusCommand: Command = object : Command {
        override fun execute(arg: Any) {
            selectForDelete.set(!selectForDelete.get())
        }
    }

    val changeActivationCommand: Command = object : Command {
        override fun execute(arg: Any) {
            changeActivation()
        }
    }

    init {
        LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "init() id : $id")

        timeObservable = Observable.create(ObservableOnSubscribe<Int> { e ->
            LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "timeObservable - wholeTimeInSecond : $wholeTimeInSecond, id : $id")
            if (wholeTimeInSecond > 0) {
                wholeTimeInSecond -= 1
                hour.set(TimeCalculator.getHourFromSeconds(wholeTimeInSecond).toInt())
                minute.set(TimeCalculator.getMinuteFromSeconds(wholeTimeInSecond).toInt())
                second.set(TimeCalculator.getSecondFromSeconds(wholeTimeInSecond).toInt())
                calculateProgressedTime()
                e.onComplete()
            } else {
                activated.set(false)
                pauseTimer()
            }
        }).repeatWhen({ t: Observable<Any> ->
            t.delay(1000, TimeUnit.MILLISECONDS)
        })
    }

    fun setActivated(value: Boolean) {
        LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "setActivated() : $value, id : $id")
        activated.set(value)
        if(value) {
            startTimer()
        }
    }

    fun calculateProgressedTime() {
        progressed.set(100 - ((wholeTimeInSecond.toFloat() / baseTime.toFloat()) * 100.0f).toInt())
    }

    fun changeActivation() {
        id?.let {
            activated.set(!activated.get())

            if(activated.get()) {
                startTimer()
            } else {
                pauseTimer()
            }

            bus.post(ChangeTimerStatusEvent(id!!, activated.get(), TimeCalculator.getSecondsFromAll(hour.get().toLong(), minute.get().toLong(), second.get().toLong())))
        }
        LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "changeActivation() id : $id, " + activated.get())
    }

    fun startTimer() {
        LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "startTimer() id : $id")
        if(timeSubscription == null) {
            timeSubscription = timeObservable.subscribe()
        }
    }

    fun pauseTimer() {
        LogUtils.printDebugLog(this@TimerListItemViewModel.javaClass, "pauseTimer() id : $id")
        timeSubscription?.dispose()
        timeSubscription = null
    }
}