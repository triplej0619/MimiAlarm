package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.ShareToFriendsEvent
import com.mimi.mimialarm.core.infrastructure.StartRewardedAdEvent
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 6. 27..
 */
class SettingsViewModel @Inject constructor(
        private val uiManager: UIManager,
        private val bus: Bus,
        private val applicationDataManager: ApplicationDataManager
) : BaseViewModel() {
    var version: ObservableField<String> = ObservableField("")
    var themeList: ObservableArrayList<Boolean> = ObservableArrayList()
    var themeSelectedIndex: Int = 0
    var alarmCloseMethod: ObservableInt = ObservableInt(0)

    val changeTheme1Command: Command = object : Command {
        override fun execute(arg: Any) {
            changeSelectedTheme(0, themeSelectedIndex)
        }
    }
    val changeTheme2Command: Command = object : Command {
        override fun execute(arg: Any) {
            changeSelectedTheme(1, themeSelectedIndex)
        }
    }
    val changeTheme3Command: Command = object : Command {
        override fun execute(arg: Any) {
            changeSelectedTheme(2, themeSelectedIndex)
        }
    }
    val changeTheme4Command: Command = object : Command {
        override fun execute(arg: Any) {
            changeSelectedTheme(3, themeSelectedIndex)
        }
    }
    val changeTheme5Command: Command = object : Command {
        override fun execute(arg: Any) {
            changeSelectedTheme(4, themeSelectedIndex)
        }
    }

    val changeAlarmCloseMethodInWindowCommand: Command = object : Command {
        override fun execute(arg: Any) {
            changeAlarmCloseMethod(0)
        }
    }
    val changeAlarmCloseMethodInAppCommand: Command = object : Command {
        override fun execute(arg: Any) {
            changeAlarmCloseMethod(1)
        }
    }

    val shareToFriendsCommand: Command = object : Command {
        override fun execute(arg: Any) {
            bus.post(ShareToFriendsEvent())
        }
    }

    init {
        bus.register(this)
        for (index in 0..5) {
            themeList.add(false)
        }
        themeSelectedIndex = applicationDataManager.getCurrentTheme()
        themeList[themeSelectedIndex] = true
        alarmCloseMethod.set(applicationDataManager.getAlarmCloseMethod())
    }

    fun changeSelectedTheme(newIndex: Int, oldIndex: Int) {
        if(!themeList[newIndex]) {
            themeList[oldIndex] = true
            themeList[newIndex] = false
            bus.post(StartRewardedAdEvent(newIndex, object : Command {
                override fun execute(arg: Any) {
                    themeList[oldIndex] = false
                    themeList[newIndex] = true
                }
            }))
        }
    }

    fun changeAlarmCloseMethod(methodIndex: Int) {
        applicationDataManager.setAlarmCloseMethod(methodIndex)
    }

    fun release() {
        bus.unregister(this)
    }
}