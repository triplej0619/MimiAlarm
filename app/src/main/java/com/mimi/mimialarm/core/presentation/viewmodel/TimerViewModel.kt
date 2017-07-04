package com.mimi.mimialarm.core.presentation.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.data.DBManager
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.android.infrastructure.AddTimerEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.model.DataMapper
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TextChanger
import com.squareup.otto.Bus
import java.util.*
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 6. 22..
 */
class TimerViewModel @Inject constructor(
        private val uiManager: UIManager,
        private val bus: Bus,
        private val dbManager: DBManager
) : BaseViewModel() {

    val MINUTE_IN_SECONDS: Int = 60
    val HOUR_IN_SECONDS: Int = MINUTE_IN_SECONDS * 60

    var deleteMode: ObservableBoolean = ObservableBoolean(false)
    var timerCount: ObservableInt = ObservableInt(0)
    var hour: ObservableField<String> = ObservableField<String>("00")
    var minute: ObservableField<String> = ObservableField<String>("10")
    var second: ObservableField<String> = ObservableField<String>("00")
    var timerListLive: MutableLiveData<ArrayList<TimerListItemViewModel>> = MutableLiveData()
    var timerList: ArrayList<TimerListItemViewModel> = ArrayList<TimerListItemViewModel>()

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

    val deleteAllCommand: Command = object : Command {
        override fun execute(arg: Any) {
            uiManager.showAlertDialog("전부 삭제하시겠습니까?", "", true, object: Command { // TODO text -> resource
                override fun execute(arg: Any) {
                    deleteAllTimer()
                }
            }, null)
        }
    }

    fun release() {
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
        val timers: List<MyTimer> = dbManager.findAllTimer()
        for (timer in timers) {
            updateOrInsertListItem(timer)
        }
        timerListLive.postValue(timerList)
        timerCount.set(timers.size)
    }

    fun updateOrInsertListItem(timer: MyTimer) {
        for (item in timerList) {
            if (item.id?.equals(timer.id) ?: false) {
                DataMapper.timerToListItemViewModel(timer, item)
                return
            }
        }

        timerList.add(DataMapper.timerToListItemViewModel(timer))
    }

    fun addTimer() {
        val timer: MyTimer? = DataMapper.viewModelToTimer(this, dbManager.getNextTimerId())
        if(timer != null) {
            dbManager.addTimer(timer)
            addTimerListItem(timer)
        }
    }

    fun addTimerListItem(timer: MyTimer) {
        timerList.add(DataMapper.timerToListItemViewModel(timer))
        timerCount.set(timerCount.get() + 1)
        timerListLive.postValue(timerList)

        bus.post(AddTimerEvent())
    }

    fun setDeleteMode(mode: Boolean) {
        for(listItem in timerList) {
            listItem.deleteMode.set(mode)
            listItem.selectForDelete.set(false)
        }
    }

    fun deleteTimers() {
        for (item in timerList) {
            if (item.selectForDelete.get()) {
                dbManager.deleteTimerWithId(item.id)
            }
        }
        reLoadTimerList()
        cancelDeleteModeCommand.execute(Unit)
    }

    fun deleteAllTimer() {
        dbManager.deleteAllTimer()
        reLoadTimerList()
        cancelDeleteModeCommand.execute(Unit)
    }

    fun clickListItem(position: Int) {
        if(deleteMode.get()) {
            timerList[position].selectForDelete.set(!timerList[position].selectForDelete.get())
        }
    }

}