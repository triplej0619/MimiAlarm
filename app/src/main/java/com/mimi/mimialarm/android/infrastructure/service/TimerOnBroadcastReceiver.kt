package com.mimi.mimialarm.android.infrastructure.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.mimi.mimialarm.android.presentation.view.TimerOnActivity
import com.mimi.mimialarm.android.utils.LogUtil

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class TimerOnBroadcastReceiver : WakefulBroadcastReceiver() {
    companion object {
        val KEY_TIMER_ID = "KEY_TIMER_ID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.printDebugLog(this@TimerOnBroadcastReceiver.javaClass, "onReceive")

        val newIntent: Intent = Intent(context, TimerOnActivity::class.java)
        intent?.let {
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(KEY_TIMER_ID, intent.getIntExtra(KEY_TIMER_ID, -1))
        }
        context?.startActivity(newIntent)
    }
}