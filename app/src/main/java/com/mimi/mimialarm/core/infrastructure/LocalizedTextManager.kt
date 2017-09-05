package com.mimi.mimialarm.core.infrastructure

/**
 * Created by MihyeLee on 2017. 9. 5..
 */
interface LocalizedTextManager {
    fun getText(textCode: MimiAlarmTextCode) : String
}