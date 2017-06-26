package com.mimi.mimialarm.core.infrastructure

import com.mimi.mimialarm.core.utils.Command


/**
 * Created by MihyeLee on 2017. 5. 31..
 */
interface UIManager {
    fun finishForegroundActivity()

    fun startAlarmDetailActivityForNew()
    fun startAlarmDetailActivityForUpdate(alarmId: Int?)

    fun showAlertDialog(msg: String, title: String, cancelable: Boolean)
    fun showAlertDialog(msg: String, title: String, cancelable: Boolean, okCallback: Command?, cancelCallback: Command?)

    fun showToast(msg: String)
}