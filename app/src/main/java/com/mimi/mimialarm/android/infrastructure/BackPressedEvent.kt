package com.mimi.mimialarm.android.infrastructure

import com.mimi.mimialarm.core.utils.Command

/**
 * Created by MihyeLee on 2017. 6. 21..
 */
class BackPressedEvent(callback: Command) {
    var callback: Command? = callback
}