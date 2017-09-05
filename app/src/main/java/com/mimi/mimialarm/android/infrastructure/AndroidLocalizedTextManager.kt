package com.mimi.mimialarm.android.infrastructure

import android.content.Context
import com.mimi.mimialarm.R
import com.mimi.mimialarm.core.infrastructure.LocalizedTextManager
import com.mimi.mimialarm.core.infrastructure.MimiAlarmTextCode

/**
 * Created by MihyeLee on 2017. 9. 5..
 */
class AndroidLocalizedTextManager(private val context: Context) : LocalizedTextManager {
    override fun getText(textCode: MimiAlarmTextCode) : String {
        when(textCode) {
            MimiAlarmTextCode.DELETE_PART -> {
                return context.getString(R.string.msg_delete_part)
            }
            MimiAlarmTextCode.DELETE_ALL -> {
                return context.getString(R.string.msg_delete_all)
            }
            MimiAlarmTextCode.NO_SELECTED_ALARM -> {
                return context.getString(R.string.msg_no_selected_alarm)
            }
            MimiAlarmTextCode.NO_SELECTED_TIMER -> {
                return context.getString(R.string.msg_no_selected_timer)
            }
            else -> {
                return ""
            }
        }
    }
}