package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.Bindable
import android.databinding.ObservableBoolean
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

    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var timerCount: ObservableInt = ObservableInt(0)
    var hour: ObservableField<String> = ObservableField<String>("00")
    var minute: ObservableField<String> = ObservableField<String>("10")
    var second: ObservableField<String> = ObservableField<String>("00")
    var timerListLive: MutableLiveData<ArrayList<TimerListItemViewModel>> = MutableLiveData()
    var timerList: ArrayList<TimerListItemViewModel> = ArrayList<TimerListItemViewModel>()
    var realm: Realm by Delegates.notNull()

    val timeTextChanger: TextChanger = object : TextChanger {
        override fun isNeedChange(oldText: String): Boolean {
            if(oldText.isNotEmpty() && oldText.toInt() > 59) {
                return true
            }
            return false
        }

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

    val startDeleteModeCommand: Command = object : Command {
        override fun execute(arg: Any) {
            deleteMode.set(true)
            setDeleteMode(true)
        }
    }

    val cancelDeleteModeCommand: Command = object : Command {
        override fun execute(arg: Any) {
            deleteMode.set(false)
            setDeleteMode(false)
        }
    }

    val deleteTimersCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("정말 삭제하시겠습니까?", "", true, object: Command { // TODO text -> resource
                override fun execute(arg: Any) {
                    deleteTimers()
                }
            }, null)
        }
    }

    init {
        realm = Realm.getDefaultInstance()
    }

    fun release() {
        realm.close()
    }

    fun clear() {
        timerList.clear()
        timerCount.set(0)
        timerListLive.postValue(timerList)
    }

    fun reLoadTimerList() {
        clear()
        loadTimerList()
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
            val timer: MyTimer? = thisToTimer(currentIdNum?.toInt()?.plus(1) ?: 0)
            if(timer != null) {
                addTimerListItem(timer)
                realm.insert(timer)
            }
        }
    }

    fun addTimerListItem(timer: MyTimer) {
        timerList.add(TimerListItemViewModel.Companion.timerDataToTimerListItem(timer))
        timerCount.set(timerCount.get() + 1)
        timerListLive.postValue(timerList)

        bus.post(AddTimerEvent())
    }

    fun thisToTimer(id: Int) : MyTimer? {
        val timer: MyTimer = MyTimer()
        timer.id = id
        timer.createdAt = Date()

        var hourInt: Int = 0
        var minInt: Int = 0
        var secInt: Int = 0
        if(!hour.get().isEmpty()) {
            hourInt = hour.get().toInt()
        }
        if(!minute.get().isEmpty()) {
            minInt = minute.get().toInt()
        }
        if(!second.get().isEmpty()) {
            secInt = second.get().toInt()
        }
        timer.seconds = (hourInt * HOUR_IN_SECONDS) + (minInt * MINUTE_IN_SECONDS) + secInt
        if(timer.seconds == 0) {
            return null;
        }

        val calendar: GregorianCalendar = GregorianCalendar()
        calendar.time = timer.createdAt
        calendar.add(GregorianCalendar.HOUR, hourInt)
        calendar.add(GregorianCalendar.MINUTE, minInt)
        calendar.add(GregorianCalendar.SECOND, secInt)
        timer.completedAt = calendar.time

        timer.activated = false

        return timer
    }

    fun setDeleteMode(mode: Boolean) {
        for(listItem in timerList) {
            listItem.deleteMode.set(mode)
            listItem.selectForDelete.set(false)
        }
    }

    fun deleteTimers() {
        for (item in timerList) {
            if(item.selectForDelete.get()) {
                realm.executeTransaction {
                    val alarm: MyTimer = realm.where(MyTimer::class.java).equalTo(MyTimer.FIELD_ID, item.id).findFirst()
                    alarm.deleteFromRealm()
                }
            }
        }
        reLoadTimerList()
    }

    fun clickListItem(position: Int) {
        if(deleteMode.get()) {
            timerList[position].selectForDelete.set(!timerList[position].selectForDelete.get())
        }
    }

}