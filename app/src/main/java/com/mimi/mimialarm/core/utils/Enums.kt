package com.mimi.mimialarm.core.utils

/**
 * Created by MihyeLee on 2017. 5. 31..
 */
class Enums {
    enum class MAIN_TAB(val code: Int) {
        MAIN_ALARM(0),
        MAIN_TIMER(1),
        MAIN_SETTINGS(2),
        UNKNOWN(99);

        companion object {
            fun valueOf(value: Int): MAIN_TAB {
                when (value) {
                    0 -> return MAIN_ALARM
                    1 -> return MAIN_TIMER
                    2 -> return MAIN_SETTINGS
                    else -> return UNKNOWN
                }
            }
        }
    }

    enum class SETTINGS_ALARM_CLOSE_METHOD(val method: Int) {
        IN_WINDOW(0),
        IN_APP(1),
        UNKNOWN(999);

        companion object {
            fun valueOf(value: Int): SETTINGS_ALARM_CLOSE_METHOD {
                when (value) {
                    0 -> return IN_WINDOW
                    1 -> return IN_APP
                    else -> return UNKNOWN
                }
            }
        }
    }
}