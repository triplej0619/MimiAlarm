package com.mimi.mimialarm.android.infrastructure

import android.content.Context
import android.util.Log
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.infrastructure.service.TimerOnBroadcastReceiver
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.core.infrastructure.AlarmManager

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class MimiAlarmManager(val context: Context) : AlarmManager {
    override fun startTimer(id: Int, time: Long) {
        try {
            ContextUtil.startTimer<TimerOnBroadcastReceiver>(context, id, time, TimerOnBroadcastReceiver::class.java, TimerOnBroadcastReceiver.KEY_TIMER_ID)
        } catch (ex: Exception) {
            Log.e(this::class.java.simpleName, ex.toString())
        }
    }

    override fun cancelTimer(id: Int) {
        try {
            ContextUtil.cancelAlarm<TimerOnBroadcastReceiver>(context, id, TimerOnBroadcastReceiver::class.java, TimerOnBroadcastReceiver.KEY_TIMER_ID)
        } catch (ex: Exception) {
            Log.e(this::class.java.simpleName, ex.toString())
        }
    }

    override fun startAlarm(id: Int, time: Long) {
        try {
            ContextUtil.startAlarm<AlarmOnBroadcastReceiver>(context, id, time, AlarmOnBroadcastReceiver::class.java, AlarmOnBroadcastReceiver.KEY_ALARM_ID)
        } catch (ex: Exception) {
            Log.e(this::class.java.simpleName, ex.toString())
        }
    }

    override fun cancelAlarm(id: Int) {
        try {
            ContextUtil.cancelAlarm<AlarmOnBroadcastReceiver>(context, id, AlarmOnBroadcastReceiver::class.java, AlarmOnBroadcastReceiver.KEY_ALARM_ID)
        } catch (ex: Exception) {
            Log.e(this::class.java.simpleName, ex.toString())
        }
    }
}