package com.mimi.data

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
interface DBManager {
    fun getNextAlarmId() : Int
    fun findAllAlarm() : List<MyAlarm>
    fun findAllAlarmSorted(comparable: Comparable<MyAlarm>) : List<MyAlarm>
    fun findAlarmWithId(id: Int?) : MyAlarm?
    fun findAlarmWithIdAsync(id: Int?) : MyAlarm?
    fun deleteAlarmWithId(id: Int?)
    fun addAlarm(alarm: MyAlarm)
    fun updateAlarm(alarm: MyAlarm)
    fun deleteAllAlarm() : Boolean
    fun isSameStatusAllAlarm(status: Boolean) : Boolean

    fun getNextTimerId() : Int
    fun findAllTimer() : List<MyTimer>
    fun findTimerWithId(id: Int?) : MyTimer?
    fun deleteTimerWithId(id: Int?)
    fun addTimer(timer: MyTimer)
    fun updateTimer(timer: MyTimer)
    fun deleteAllTimer() : Boolean
}