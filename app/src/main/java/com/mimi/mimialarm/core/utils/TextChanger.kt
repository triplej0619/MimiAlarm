package com.mimi.mimialarm.core.utils

/**
 * Created by MihyeLee on 2017. 6. 23..
 */
interface TextChanger {
    fun isNeedChange(oldText: String) : Boolean
    fun getChangedText(oldText: String) : String
}