package com.mimi.mimialarm.core.model

import io.realm.RealmObject
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
open class MyAlarm : RealmObject() {
    var createdAt: Date? = null
    var completedAt: Date? = null

    var mediaSrc: String? = null

    var snoozeInterval: Int? = null
    var snoozeCount: Int? = null

    var monday: Boolean? = null
    var tuesDay: Boolean? = null
    var wednesDay: Boolean? = null
    var thurseDay: Boolean? = null
    var friDay: Boolean? = null
    var saturDay: Boolean? = null
    var sunDay: Boolean? = null
}