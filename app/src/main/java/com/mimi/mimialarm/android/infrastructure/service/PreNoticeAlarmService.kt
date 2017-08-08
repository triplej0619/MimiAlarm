package com.mimi.mimialarm.android.infrastructure.service

import android.app.IntentService
import android.content.Intent
import com.mimi.data.RealmDataUtil
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.utils.DateUtil
import io.realm.Realm
import java.util.*
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 8. 7..
 */
class PreNoticeAlarmService : IntentService("PreNoticeAlarmService") {

    @Inject lateinit var uiManager: UIManager

    companion object {
        val KEY_ALARM_ID = "KEY_ALARM_ID"
    }

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        buildComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtil.printDebugLog(this@PreNoticeAlarmService.javaClass, "onHandleIntent")
        intent?.let {
            val id = intent.getIntExtra(KEY_ALARM_ID, -1)
            val completedAt = getCompletedAt(id)
            completedAt?.let {
                val timeString = DateUtil.dateToFormattedString(completedAt, "HH:mm")
                uiManager.addPreNoticeNotification(timeString, id)
            }
        }
    }

    fun getCompletedAt(id: Int) : Date? {
        val realm = Realm.getDefaultInstance() // TODO
        var date : Date? = null
        realm.executeTransaction {
            val alarm = RealmDataUtil.findObjectWithId<MyAlarm>(realm, "id", id)
            alarm?.let {
                date = alarm.completedAt
            }
        }
        realm.close()
        return date
    }

}