package com.mimi.mimialarm.android.infrastructure

import android.content.Context
import android.util.Log
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.infrastructure.service.NoticeAlarmBroadcastReceiver
import com.mimi.mimialarm.android.infrastructure.service.TimerOnBroadcastReceiver
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.core.infrastructure.AlarmManager

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class MimiAlarmManager(val context: Context) : AlarmManager {
    val HOUR_IN_MILLI = 1000 * 60 * 60

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

    override fun startAlarmForPreNotice(id: Int, time: Long) {
        try {
            var noticeTime = 0L
            if(time > HOUR_IN_MILLI) {
                noticeTime = time - HOUR_IN_MILLI
            }
            ContextUtil.startAlarm<NoticeAlarmBroadcastReceiver>(context, id, noticeTime, NoticeAlarmBroadcastReceiver::class.java, NoticeAlarmBroadcastReceiver.KEY_ALARM_ID)
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
            ContextUtil.cancelAlarm<NoticeAlarmBroadcastReceiver>(context, id, NoticeAlarmBroadcastReceiver::class.java, NoticeAlarmBroadcastReceiver.KEY_ALARM_ID)
        } catch (ex: Exception) {
            Log.e(this::class.java.simpleName, ex.toString())
        }
    }
}