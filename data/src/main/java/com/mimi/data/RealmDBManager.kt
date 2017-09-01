package com.mimi.data

import com.mimi.data.model.MyAlarm
import com.mimi.data.model.MyTimer
import io.realm.Realm

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class RealmDBManager : DBManager {
    val FIELD_ID = "id"
    val FIELD_COMPLETED_AT = "completedAt"
    val FIELD_SECONDS = "seconds"

    lateinit var realm: Realm

    fun setRealm(realm: Realm) : RealmDBManager {
        this.realm = realm
        return this
    }

    override fun getNextAlarmId(): Int {
        val current = RealmDataUtil.getCurrentId<MyAlarm>(realm, FIELD_ID) ?: 0
        return current.plus(1)
    }

    override fun getNextTimerId(): Int {
        val current = RealmDataUtil.getCurrentId<MyTimer>(realm, FIELD_ID) ?: 0
        return current.plus(1)
    }

    override fun findAllAlarm(): List<MyAlarm> {
        val results = RealmDataUtil.findObjects<MyAlarm>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findAllAlarmSorted(comparable: Comparable<MyAlarm>): List<MyAlarm> {
        val results = RealmDataUtil.findObjects<MyAlarm>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findAlarmWithId(id: Int?): MyAlarm? {
        if(id != null) {
            return realm.copyFromRealm(RealmDataUtil.findObjectWithId<MyAlarm>(realm, FIELD_ID, id))
        }
        return null
    }

    override fun findAlarmWithIdAsync(id: Int?): MyAlarm? {
        return null // TODO
    }

    override fun deleteAlarmWithId(id: Int?) {
        if(id != null) {
            RealmDataUtil.deleteObject(realm, RealmDataUtil.findObjectWithId<MyAlarm>(realm, FIELD_ID, id))
        }
    }

    override fun findAllTimer(): List<MyTimer> {
        val results = RealmDataUtil.findObjects<MyTimer>(realm)
        return realm.copyFromRealm(results.toList())
    }

    override fun findTimerWithId(id: Int?): MyTimer? {
        if(id != null) {
            return realm.copyFromRealm(RealmDataUtil.findObjectWithId<MyTimer>(realm, FIELD_ID, id))
        }
        return null
    }

    override fun deleteTimerWithId(id: Int?) {
        if(id != null) {
            RealmDataUtil.deleteObject(realm, RealmDataUtil.findObjectWithId<MyTimer>(realm, FIELD_ID, id))
        }
    }

    override fun addAlarm(alarm: MyAlarm) {
        RealmDataUtil.insertObjectWithId(realm, alarm)
    }

    override fun updateAlarm(alarm: MyAlarm) {
        RealmDataUtil.insertOrUpdateObject(realm, alarm)
    }

    override fun addTimer(timer: MyTimer) {
        RealmDataUtil.insertObjectWithIdAsync(timer)
//        RealmDataUtil.insertObjectWithId(realm, timer)
    }

    override fun updateTimer(timer: MyTimer) {
        RealmDataUtil.insertOrUpdateObjectAsync(timer)
//        RealmDataUtil.insertOrUpdateObject(realm, timer)
    }

    override fun deleteAllTimer() : Boolean = RealmDataUtil.deleteClass<MyTimer>(realm)

    override fun deleteAllAlarm() : Boolean = RealmDataUtil.deleteClass<MyAlarm>(realm)

    override fun isSameStatusAllAlarm(status: Boolean): Boolean {
        val list = RealmDataUtil.findObjects<MyAlarm>(realm)
        return list.filter { it.enable != status }.isEmpty()
    }

}