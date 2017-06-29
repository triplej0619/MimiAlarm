package com.mimi.mimialarm.core.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by MihyeLee on 2017. 5. 29..
 */
open class MyAlarm : RealmObject() {
    companion object {
        @Ignore
        val FIELD_ID: String = "id"
    }

    @PrimaryKey
    @Required
    var id: Int? = null

    var createdAt: Date? = null
    var completedAt: Date? = null

    var vibration: Boolean? = null
    var media: Boolean? = null
    var mediaSrc: String? = null
    var volume: Int? = null

    var snooze: Boolean? = null
    var snoozeInterval: Int? = null
    var snoozeCount: Int? = null

    var repeat: Boolean? = null
    var monDay: Boolean? = null
    var tuesDay: Boolean? = null
    var wednesDay: Boolean? = null
    var thursDay: Boolean? = null
    var friDay: Boolean? = null
    var saturDay: Boolean? = null
    var sunDay: Boolean? = null

    var enable: Boolean? = null
}