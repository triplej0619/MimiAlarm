package com.mimi.mimialarm.android.utils

import android.util.Log

/**
 * Created by MihyeLee on 2017. 7. 20..
 */
class LogUtil {
    companion object {
        val APP_NAME: String = "MimiAlarmLog"

        fun printDebugLog(tag: String,  msg: String) {
            Log.d("$APP_NAME : $tag", msg)
        }

        fun <T> printDebugLog(clazz: Class<T>, msg: String) {
            Log.d("$APP_NAME : " + clazz.simpleName, msg)
        }

        fun <T> printDebugLog(clazz: Class<T>, exception: Throwable) {
            Log.d("$APP_NAME : " + clazz.simpleName, exception.toString())
        }
    }
}