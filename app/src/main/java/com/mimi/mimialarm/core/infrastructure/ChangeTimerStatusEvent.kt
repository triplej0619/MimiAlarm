package com.mimi.mimialarm.core.infrastructure

/**
 * Created by MihyeLee on 2017. 7. 13..
 */
class ChangeTimerStatusEvent(val id: Int, val activated: Boolean, val remainSeconds: Long)