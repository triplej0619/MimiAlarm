package com.mimi.mimialarm.android.utils

import android.util.Log
import kotlin.reflect.KClass

/**
 * Created by MihyeLee on 2017. 7. 20..
 */
class LogUtils {
    companion object {
        fun printDebugLog(tag: String,  msg: String) {
            Log.d(tag, msg)
        }

        fun <T> printDebugLog(clazz: Class<T>, msg: String) {
            Log.d(clazz.simpleName, msg)
        }

        fun <T> printDebugLog(clazz: Class<T>, exception: Throwable) {
            Log.d(clazz.simpleName, exception.toString())
        }
    }
}