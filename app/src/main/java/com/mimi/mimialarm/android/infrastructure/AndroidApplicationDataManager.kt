package com.mimi.mimialarm.android.infrastructure

import android.content.Context
import android.content.SharedPreferences
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager

/**
 * Created by MihyeLee on 2017. 7. 19..
 */
class AndroidApplicationDataManager(context: Context) : ApplicationDataManager {

    val DEFAULT_VOLUME = 70

    val KEY_CURRENT_THEME = "KEY_CURRENT_THEME"
    val KEY_ALARM_CLOSE_METHOD = "KEY_ALARM_CLOSE_METHOD"
    val KEY_TIMER_VOLUME = "KEY_TIMER_VOLUME"

    val PREF_NAME = "MIMI_ALARM_PREF"

    var pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun getCurrentTheme(): Int = pref.getInt(KEY_CURRENT_THEME, 0)

    override fun setCurrentTheme(theme: Int) = pref.edit().putInt(KEY_CURRENT_THEME, theme).apply()

    override fun getAlarmCloseMethod(): Int = pref.getInt(KEY_ALARM_CLOSE_METHOD, 0)

    override fun setAlarmCloseMethod(method: Int) = pref.edit().putInt(KEY_ALARM_CLOSE_METHOD, method).apply()

    override fun getTimerVolume(): Int = pref.getInt(KEY_TIMER_VOLUME, DEFAULT_VOLUME)

    override fun setTimerVolume(volume: Int) = pref.edit().putInt(KEY_TIMER_VOLUME, volume).apply()
}