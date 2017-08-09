package com.mimi.mimialarm.core.infrastructure

/**
 * Created by MihyeLee on 2017. 7. 19..
 */
interface ApplicationDataManager {
    fun getCurrentTheme() : Int
    fun setCurrentTheme(theme: Int)

    fun getAlarmCloseMethod() : Int
    fun setAlarmCloseMethod(method: Int)

    fun getTimerVolume() : Int
    fun setTimerVolume(volume: Int)
}