package com.mimi.data

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
interface DBManager {
    fun findAllAlarm() : List<MyAlarm>
    fun findAlarmWithId(id: Int) : MyAlarm
    fun deleteAlarmWithId(id: Int) : Boolean

    fun findAllTimer() : List<MyTimer>
    fun findTimerWithId(id: Int) : MyTimer
    fun deleteTimerWithId(id: Int) : Boolean
}