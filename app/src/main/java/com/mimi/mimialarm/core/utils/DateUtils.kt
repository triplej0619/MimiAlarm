package com.mimi.mimialarm.core.utils

import java.util.*

/**
 * Created by MihyeLee on 2017. 7. 20..
 */
class DateUtils {
    companion object {
        fun getAfterDate(milliseconds: Int) : Date{
            val afterDate: Date = Date()
            afterDate.time = afterDate.time + milliseconds
            return afterDate
        }
    }
}