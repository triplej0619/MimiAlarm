package com.mimi.data.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
open class MyTimer : RealmObject() {
    companion object {
        @Ignore
        val FIELD_ID: String = "id"
    }

    @PrimaryKey
    @Required
    var id: Int? = null

    var createdAt: Date? = null
    var completedAt: Date? = null
    var seconds: Int? = null
    var activated: Boolean = false
}