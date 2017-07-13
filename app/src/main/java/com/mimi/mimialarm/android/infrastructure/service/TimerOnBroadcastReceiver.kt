package com.mimi.mimialarm.android.infrastructure.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.mimi.mimialarm.android.presentation.view.TimerOnActivity

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class TimerOnBroadcastReceiver : WakefulBroadcastReceiver() {
    companion object {
        val KEY_TIMER_ID = "KEY_TIMER_ID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val newIntent: Intent = Intent(context, TimerOnActivity::class.java)
        intent?.let { newIntent.putExtra(KEY_TIMER_ID, intent.getIntExtra(KEY_TIMER_ID, -1)) }
        context?.startActivity(newIntent)
    }
}