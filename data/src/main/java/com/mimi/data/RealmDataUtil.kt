package com.mimi.data

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class RealmDataUtil {
    companion object {
        inline fun <reified T : RealmModel?> findObjects(realm: Realm) : RealmResults<T> {
            return realm.where(T::class.java).findAll()
        }

        inline fun <reified T : RealmModel?> findObjectWithId(realm: Realm, idFieldName: String, id: Int) : T {
            return realm.where(T::class.java).equalTo(idFieldName, id).findFirst()
        }

        inline fun <reified T : RealmModel?> getCurrentId(realm: Realm, idFieldName: String) : Int? {
            return realm.where(T::class.java).max(idFieldName)?.toInt()
        }

        inline fun <reified T : RealmModel?> deleteClass(realm: Realm) : Boolean {
            realm.beginTransaction()
            val ret = realm.where(T::class.java).findAll().deleteAllFromRealm()
            realm.commitTransaction()
            return ret
        }

        fun deleteAll(realm: Realm) {
            realm.executeTransaction {
                realm.deleteAll()
            }
        }

        fun <T : RealmObject?> deleteObject(realm: Realm, obj: T) {
            if(obj != null) {
                realm.executeTransaction {
                    obj.deleteFromRealm()
                }
            }
        }

        fun <T : RealmModel?> insertOrUpdateObject(realm: Realm, obj: T) {
            realm.executeTransaction {
                realm.insertOrUpdate(obj)
            }
        }

        fun <T : RealmModel?> insertOrUpdateObjectAsync(obj: T) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.insertOrUpdate(obj)
            }
            realm.close()
        }

        fun <T : RealmModel?> insertObjectWithId(realm: Realm, obj: T) {
            realm.executeTransaction {
                realm.insert(obj)
            }
        }

        fun <T : RealmModel?> insertObjectWithIdAsync(obj: T) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync {
                it.insert(obj)
            }
            realm.close()
        }
    }
}
