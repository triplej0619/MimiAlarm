package com.mimi.mimialarm.core.utils

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
interface Command<T> {
    fun execute(commandArg0: T)
}