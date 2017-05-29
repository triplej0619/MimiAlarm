package com.mimi.mimialarm.core.model

import io.realm.RealmObject
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
open class MyAlarm : RealmObject() {
    var createdAt: Date? = null
    var completedAt: Date? = null
    var days: Set<String>? = null
    var bellSrc: String? = null
}