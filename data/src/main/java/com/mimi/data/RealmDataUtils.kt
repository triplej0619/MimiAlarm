package com.mimi.data

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * Created by MihyeLee on 2017. 7. 3..
 */
class RealmDataUtils {
    companion object {
        inline fun <reified T : RealmModel?> findObjects(realm: Realm) : RealmResults<T> {
            return realm.where(T::class.java).findAll()
        }
        inline fun <reified T : RealmModel?> findObjectWithId(realm: Realm, idFieldName: String, id: Int) : T {
            return realm.where(T::class.java).equalTo(idFieldName, id).findFirst()
        }

        inline fun <reified T : RealmModel?> getCurrentId(realm: Realm, idFieldName: String) : Int {
            return realm.where(T::class.java).max(idFieldName).toInt()
        }

        inline fun <reified T : RealmModel?> deleteClass(realm: Realm) : Boolean = realm.where(T::class.java).findAll().deleteAllFromRealm()

        fun deleteAll(realm: Realm) {
            realm.deleteAll()
        }
    }
}
