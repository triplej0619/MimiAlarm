package com.mimi.mimialarm.android.infrastructure

import android.content.Context
import android.util.Log
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.infrastructure.AlarmManager

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class MimiAlarmManager(val context: Context) : AlarmManager {
    override fun startAlarm(id: Int, time: Long) {
        try {
            ContextUtils.startAlarm<AlarmOnBroadcastReceiver>(context, id, time, AlarmOnBroadcastReceiver::class.java, AlarmOnBroadcastReceiver.KEY_ALARM_ID)
        } catch (ex: Exception) {
            Log.e(this::class.simpleName, ex.toString())
        }
    }

    override fun cancelAlarm(id: Int) {
        try {
            ContextUtils.cancelAlarm<AlarmOnBroadcastReceiver>(context, id, AlarmOnBroadcastReceiver::class.java, AlarmOnBroadcastReceiver.KEY_ALARM_ID)
        } catch (ex: Exception) {
            Log.e(this::class.simpleName, ex.toString())
        }
    }
}