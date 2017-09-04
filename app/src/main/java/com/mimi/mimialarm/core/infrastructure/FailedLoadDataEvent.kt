package com.mimi.mimialarm.core.infrastructure

/**
 * Created by MihyeLee on 2017. 9. 4..
 */
class FailedLoadDataEvent(private val type: DATA_TYPE) {
    enum class DATA_TYPE {
        ALARM,
        TIMER
    }
}