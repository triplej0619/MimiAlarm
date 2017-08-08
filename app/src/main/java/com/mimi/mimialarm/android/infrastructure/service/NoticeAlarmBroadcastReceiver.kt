package com.mimi.mimialarm.android.infrastructure.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.mimi.mimialarm.android.utils.LogUtil

/**
 * Created by MihyeLee on 2017. 8. 7..
 */
class NoticeAlarmBroadcastReceiver : WakefulBroadcastReceiver() {
    companion object {
        val KEY_ALARM_ID = "KEY_ALARM_ID"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.printDebugLog(this@NoticeAlarmBroadcastReceiver.javaClass, "onReceive")

        val newIntent: Intent = Intent(context, PreNoticeAlarmService::class.java)
        intent?.let { newIntent.putExtra(PreNoticeAlarmService.KEY_ALARM_ID, intent.getIntExtra(KEY_ALARM_ID, -1)) }
        context?.startService(newIntent)
    }

}