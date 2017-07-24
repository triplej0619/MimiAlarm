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
    var alarmCloseMethod: ObservableInt = ObservableInt(0)

    val changeThemeCommand: Command = object : Command {
        override fun execute(arg: Any) {
            bus.post(StartRewardedAdEvent())
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
        themeList[applicationDataManager.getCurrentTheme()] = true
        alarmCloseMethod.set(applicationDataManager.getAlarmCloseMethod())
    }

    fun release() {
        bus.unregister(this)
    }
}