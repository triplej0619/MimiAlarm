package com.mimi.mimialarm.core.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
open class MyAlarm : RealmObject() {
    companion object {
        val FIELD_ID: String = "id"
    }

    @PrimaryKey
    @Required
    var id: Int? = null

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

    var enable: Boolean? = null
}