package com.mimi.mimialarm.core.infrastructure

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
interface AlarmManager {
    fun startAlarm(id: Int, time: Long)
    fun startAlarmForPreNotice(id: Int, time: Long)
    fun cancelAlarm(id: Int)
    fun startTimer(id: Int, time: Long)
    fun cancelTimer(id: Int)
}