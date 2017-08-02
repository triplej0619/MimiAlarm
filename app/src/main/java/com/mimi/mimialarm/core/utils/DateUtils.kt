package com.mimi.mimialarm.core.utils

import java.util.*

/**
 * Created by MihyeLee on 2017. 7. 20..
 */
class DateUtils {
    companion object {
        fun getAfterDate(milliseconds: Int) : Date{
            return getAfterDate(Date(), milliseconds)
        }

        fun getAfterDate(baseDate: Date, milliseconds: Int) : Date{
            val afterDate = Date()
            afterDate.time = baseDate.time + milliseconds
            return afterDate
        }
    }
}