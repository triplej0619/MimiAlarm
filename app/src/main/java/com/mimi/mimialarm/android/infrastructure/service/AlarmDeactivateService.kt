package com.mimi.mimialarm.android.infrastructure.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.mimi.data.RealmDataUtil
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.utils.TimeCalculator
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 8. 3..
 */
class AlarmDeactivateService : IntentService("AlarmDeactivateService") {

    @Inject lateinit var alarmManager: AlarmManager

    companion object {
        val KEY_ID = "KEY_ID"
        val KEY_ACTION_KILL_SNOOZE = "KEY_ACTION_KILL_SNOOZE"
    }

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "onCreate()")
        buildComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "onHandleIntent() " + intent?.action)

        intent?.let {
            when(intent.action) {
                KEY_ACTION_KILL_SNOOZE -> {
                    killSnooze(intent.getIntExtra(KEY_ID, -1))
                }
            }
        }
    }

    fun killSnooze(id: Int) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "killSnooze() $id")
        if(id > -1) {
            val nm: NotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(id)
            alarmManager.cancelAlarm(id)
            resetSnoozeCount(id)
        }
    }

    fun resetSnoozeCount(id: Int) {
        val realm = Realm.getDefaultInstance() // TODO
        realm.executeTransaction {
            val alarm = RealmDataUtil.findObjectWithId<MyAlarm>(realm, "id", id)
            alarm?.let {
                alarm.usedSnoozeCount = 0
                setNextDayAlarm(alarm, id)
            }
        }
        realm.close()
    }

    fun setNextDayAlarm(alarm: MyAlarm, id: Int) {
        if(alarm.repeat) {
            alarmManager.startAlarm(id, TimeCalculator.getMilliSecondsForScheduling(alarm))
        }
    }
}