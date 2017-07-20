package com.mimi.mimialarm.android.infrastructure.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.mimi.mimialarm.android.presentation.view.AlarmOnActivity
import com.mimi.mimialarm.android.utils.LogUtils

/**
 * Created by MihyeLee on 2017. 6. 29..
 */
class AlarmOnBroadcastReceiver : WakefulBroadcastReceiver() {
    companion object {
        val KEY_ALARM_ID = "KEY_ALARM_ID"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.printDebugLog(this@AlarmOnBroadcastReceiver.javaClass, "onReceive")

        val newIntent: Intent = Intent(context, AlarmOnActivity::class.java)
        intent?.let { newIntent.putExtra(KEY_ALARM_ID, intent.getIntExtra(KEY_ALARM_ID, -1)) }
        context?.startActivity(newIntent)
    }
}