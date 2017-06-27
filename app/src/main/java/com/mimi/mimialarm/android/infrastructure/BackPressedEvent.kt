package com.mimi.mimialarm.android.infrastructure

import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.Enums

/**
 * Created by MihyeLee on 2017. 6. 21..
 */
class BackPressedEvent(val tabType: Enums.MAIN_TAB, val callback: Command) {
}