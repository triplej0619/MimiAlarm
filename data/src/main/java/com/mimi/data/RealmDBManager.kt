package com.mimi.data

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer
import io.realm.Realm

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class RealmDBManager : DBManager {
    val FIELD_ID = "id"
    lateinit var realm: Realm

    fun setRealm(realm: Realm) : RealmDBManager {
        this.realm = realm
        return this
    }

    override fun findAllAlarm(): List<MyAlarm> {
        val results = RealmDataUtils.findObjects<MyAlarm>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findAlarmWithId(id: Int): MyAlarm {
        return realm.copyFromRealm(RealmDataUtils.findObjectWithId<MyAlarm>(realm, FIELD_ID, id))
    }

    override fun deleteAlarmWithId(id: Int) : Boolean = RealmDataUtils.deleteClass<MyAlarm>(realm)

    override fun findAllTimer(): List<MyTimer> {
        val results = RealmDataUtils.findObjects<MyTimer>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findTimerWithId(id: Int): MyTimer {
        return realm.copyFromRealm(RealmDataUtils.findObjectWithId<MyTimer>(realm, FIELD_ID, id))
    }

    override fun deleteTimerWithId(id: Int) : Boolean = RealmDataUtils.deleteClass<MyTimer>(realm)
}