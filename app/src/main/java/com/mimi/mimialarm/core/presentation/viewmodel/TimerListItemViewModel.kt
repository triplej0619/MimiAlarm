package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
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
//    var paused: ObservableBoolean = ObservableBoolean(false)
//    var stop: ObservableBoolean = ObservableBoolean(false)
    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var selectForDelete: ObservableBoolean = ObservableBoolean(false)
    var timeSubscription: Disposable? = null
    val timeObservable: Observable<Int>

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
        timeObservable = Observable.create(ObservableOnSubscribe<Int> { e ->
            if (wholeTimeInSecond > 0) {
                wholeTimeInSecond -= 1
                hour.set(TimeCalculator.getHourFromSeconds(wholeTimeInSecond).toInt())
                minute.set(TimeCalculator.getMinuteFromSeconds(wholeTimeInSecond).toInt())
                second.set(TimeCalculator.getSecondFromSeconds(wholeTimeInSecond).toInt())
                e.onComplete()
            } else {
                activated.set(false)
            }
        }).repeatWhen({ t: Observable<Any> ->
            t.delay(1, TimeUnit.SECONDS)
        })
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
    }

    fun startTimer() {
        if(timeSubscription == null) {
            timeSubscription = timeObservable.subscribe()
        }
    }

    fun pauseTimer() {
        timeSubscription?.dispose()
        timeSubscription = null
    }
}