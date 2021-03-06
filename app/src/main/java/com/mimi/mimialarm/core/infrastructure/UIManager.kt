package com.mimi.mimialarm.core.infrastructure

import com.mimi.mimialarm.core.utils.Command


/**
 * Created by MihyeLee on 2017. 5. 31..
 */
interface UIManager {
    fun finishForegroundView()
    fun addSnoozeNotification(msg: String, id: Int)
    fun addPreNoticeNotification(msg: String, id: Int)

    fun startAlarmDetailViewForNew()
    fun startAlarmDetailViewForUpdate(alarmId: Int?)
    fun startActivatedAlarmListView()

    fun showAlertDialog(msg: String, title: String, cancelable: Boolean)
    fun showAlertDialog(msg: String, title: String, cancelable: Boolean, okCallback: Command?, cancelCallback: Command?)

    fun showToast(msg: String)

    fun startWebBrowserWithUrl(url: String)
}