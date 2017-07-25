package com.mimi.data.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by MihyeLee on 2017. 7. 3..
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

    var vibration: Boolean = false
    var media: Boolean = false
    var mediaSrc: String? = null
    var volume: Int? = null

    var snooze: Boolean = false
    var snoozeInterval: Int? = null
    var snoozeCount: Int? = null
    var usedSnoozeCount: Int? = null

    var repeat: Boolean = false
    var monDay: Boolean = false
    var tuesDay: Boolean = false
    var wednesDay: Boolean = false
    var thursDay: Boolean = false
    var friDay: Boolean = false
    var saturDay: Boolean = false
    var sunDay: Boolean = false

    var enable: Boolean = false
}