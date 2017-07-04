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

    override fun getNextAlarmId(): Int {
        val current = RealmDataUtils.getCurrentId<MyAlarm>(realm, FIELD_ID) ?: 0
        return current.plus(1)
    }

    override fun getNextTimerId(): Int {
        val current = RealmDataUtils.getCurrentId<MyTimer>(realm, FIELD_ID) ?: 0
        return current.plus(1)
    }

    override fun findAllAlarm(): List<MyAlarm> {
        val results = RealmDataUtils.findObjects<MyAlarm>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findAlarmWithId(id: Int?): MyAlarm? {
        if(id != null) {
            return realm.copyFromRealm(RealmDataUtils.findObjectWithId<MyAlarm>(realm, FIELD_ID, id))
        }
        return null
    }

    override fun deleteAlarmWithId(id: Int?) {
        if(id != null) {
            RealmDataUtils.deleteObject(realm, RealmDataUtils.findObjectWithId<MyAlarm>(realm, FIELD_ID, id))
        }
    }

    override fun findAllTimer(): List<MyTimer> {
        val results = RealmDataUtils.findObjects<MyTimer>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findTimerWithId(id: Int?): MyTimer? {
        if(id != null) {
            return realm.copyFromRealm(RealmDataUtils.findObjectWithId<MyTimer>(realm, FIELD_ID, id))
        }
        return null
    }

    override fun deleteTimerWithId(id: Int?) {
        if(id != null) {
            RealmDataUtils.deleteObject(realm, RealmDataUtils.findObjectWithId<MyTimer>(realm, FIELD_ID, id))
        }
    }

    override fun addAlarm(alarm: MyAlarm) {
        RealmDataUtils.insertObjectWithId(realm, alarm)
    }

    override fun updateAlarm(alarm: MyAlarm) {
        RealmDataUtils.insertOrUpdateObject(realm, alarm)
    }

    override fun addTimer(timer: MyTimer) {
        RealmDataUtils.insertObjectWithId(realm, timer)
    }

    override fun updateTimer(timer: MyTimer) {
        RealmDataUtils.insertOrUpdateObject(realm, timer)
    }

    override fun deleteAllTimer() : Boolean = RealmDataUtils.deleteClass<MyTimer>(realm)

    override fun deleteAllAlarm() : Boolean = RealmDataUtils.deleteClass<MyAlarm>(realm)
}