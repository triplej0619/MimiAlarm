package com.mimi.mimialarm.android.infrastructure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mimi.mimialarm.android.infrastructure.service.TimerDeactivateService

/**
 * Created by MihyeLee on 2017. 6. 27..
 */
class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            context?.let {
                // TODO rescheduling alarm

                context.startService(Intent(context, TimerDeactivateService::class.java))
            }
        }
    }

}