package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.mimialarm.BR
import com.mimi.mimialarm.android.infrastructure.AddTimerEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.MyTimer
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TextChanger
import com.squareup.otto.Bus
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 6. 22..
 */
class TimerViewModel @Inject constructor(private val uiManager: UIManager, private val bus: Bus) : BaseViewModel() {

    val MINUTE_IN_SECONDS: Int = 60
    val HOUR_IN_SECONDS: Int = MINUTE_IN_SECONDS * 60

    var timerCount: ObservableInt = ObservableInt(0)
    var hour: ObservableField<String> = ObservableField<String>("00")
    var minute: ObservableField<String> = ObservableField<String>("10")
    var second: ObservableField<String> = ObservableField<String>("00")
    var timerListLive: MutableLiveData<ArrayList<TimerListItemViewModel>> = MutableLiveData()
    var timerList: ArrayList<TimerListItemViewModel> = ArrayList<TimerListItemViewModel>()
    var realm: Realm by Delegates.notNull()

    val timeTextChanger: TextChanger = object : TextChanger {
        override fun getChangedText(oldText: String): String {
            var changedTime: String = ""
            if(oldText.isNotEmpty() && oldText.toInt() > 59) {
                changedTime = "59"
            } else {
                changedTime = oldText
            }
            return changedTime
        }
    }

    val addTimerCommand: Command = object : Command {
        override fun execute(arg: Any) {
            addTimer()
        }
    }

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
        realm.close()
    }

    fun loadTimerList() {
        val results: RealmResults<MyTimer> = realm.where(MyTimer::class.java).findAll()
        for (result in results) {
            updateOrInsertListItem(realm.copyFromRealm(result))
        }
        timerListLive.postValue(timerList)
        timerCount.set(results.size)
    }

    fun updateOrInsertListItem(timer: MyTimer) {
        for (item in timerList) {
            if (item.id?.equals(timer.id) ?: false) {
                item.copyFromTimer(timer)
                return
            }
        }

        timerList.add(TimerListItemViewModel.Companion.timerDataToTimerListItem(timer))
    }

    fun addTimer() {
        realm.executeTransaction {
            val currentIdNum = realm.where(MyTimer::class.java).max(MyTimer.FIELD_ID)
            val timer: MyTimer = thisToTimer(currentIdNum?.toInt()?.plus(1) ?: 0)
            timerList.add(TimerListItemViewModel.Companion.timerDataToTimerListItem(timer))
            realm.insert(timer)
        }
        timerCount.set(timerCount.get() + 1)
        timerListLive.postValue(timerList)

        bus.post(AddTimerEvent())
    }

    fun thisToTimer(id: Int) : MyTimer {
        val timer: MyTimer = MyTimer()
        timer.id = id
        timer.createdAt = Date()
        timer.seconds = (hour.get().toInt() * HOUR_IN_SECONDS) + (minute.get().toInt() * MINUTE_IN_SECONDS) + second.get().toInt()

        val calendar: GregorianCalendar = GregorianCalendar()
        calendar.time = timer.createdAt
        calendar.add(GregorianCalendar.HOUR, hour.get().toInt())
        calendar.add(GregorianCalendar.MINUTE, minute.get().toInt())
        calendar.add(GregorianCalendar.SECOND, second.get().toInt())
        timer.completedAt = calendar.time

        timer.activated = false

        return timer
    }

}