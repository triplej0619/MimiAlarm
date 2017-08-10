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
import com.mimi.mimialarm.core.infrastructure.RefreshAlarmListEvent
import com.mimi.mimialarm.core.infrastructure.ResetAlarmSnoozeCountEvent
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.squareup.otto.Bus
import io.realm.Realm
import java.util.*
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 8. 3..
 */
class AlarmDeactivateService : IntentService("AlarmDeactivateService") {

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var bus: Bus

    companion object {
        val KEY_ID = "KEY_ID"
        val KEY_ACTION_KILL_SNOOZE = "KEY_ACTION_KILL_SNOOZE"
        val KEY_ACTION_CANCEL_ALARM = "KEY_ACTION_CANCEL_ALARM"
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
            val id = intent.getIntExtra(KEY_ID, -1)
            when(intent.action) {
                KEY_ACTION_KILL_SNOOZE -> {
                    killSnooze(id)
                }
                KEY_ACTION_CANCEL_ALARM -> {
                    cancelAlarm(id)
                    setNextAlarmAfterPreCancel(id)
                }
            }
        }
    }

    fun cancelAlarm(id: Int) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "cancelAlarm() $id")
        if(id > -1) {
            val nm: NotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(id)
            alarmManager.cancelAlarm(id)
        }
        disableAlarm(id)
    }

    fun disableAlarm(id: Int) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "disableAlarm() $id")
        val realm = Realm.getDefaultInstance() // TODO
        realm.executeTransaction {
            val alarm = RealmDataUtil.findObjectWithId<MyAlarm>(realm, "id", id)
            if(!alarm.repeat) {
                alarm.enable = false
                bus.post(RefreshAlarmListEvent())
            }
        }
        realm.close()
    }

    fun setNextAlarmAfterPreCancel(id: Int) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "setNextAlarmAfterPreCancel() $id")
        val realm = Realm.getDefaultInstance() // TODO
        var copiedAlarm: MyAlarm? = null
        realm.executeTransaction {
            val alarm = RealmDataUtil.findObjectWithId<MyAlarm>(realm, "id", id)
            copiedAlarm = realm.copyFromRealm(alarm)
        }
        realm.close()

        copiedAlarm?.let {
            disableTodayOfWeekInAlarm(copiedAlarm!!)
            setNextAlarm(copiedAlarm!!, id, true)
        }
    }

    fun disableTodayOfWeekInAlarm(alarm: MyAlarm) {
        val calendar = GregorianCalendar()
        when(calendar.get(GregorianCalendar.DAY_OF_WEEK)) {
            GregorianCalendar.MONDAY -> alarm.monDay = false
            GregorianCalendar.TUESDAY -> alarm.tuesDay = false
            GregorianCalendar.WEDNESDAY -> alarm.wednesDay = false
            GregorianCalendar.THURSDAY -> alarm.thursDay = false
            GregorianCalendar.FRIDAY -> alarm.friDay = false
            GregorianCalendar.SATURDAY -> alarm.saturDay = false
            GregorianCalendar.SUNDAY -> alarm.sunDay = false
        }
    }

    fun killSnooze(id: Int) {
        LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "killSnooze() $id")
        if(id > -1) {
            cancelAlarm(id)
            resetSnoozeCount(id)
        }
    }

    fun resetSnoozeCount(id: Int) {
        val realm = Realm.getDefaultInstance() // TODO
        realm.executeTransaction {
            val alarm = RealmDataUtil.findObjectWithId<MyAlarm>(realm, "id", id)
            alarm.usedSnoozeCount = 0
            setNextAlarm(alarm, id, false)
        }
        realm.close()
        bus.post(ResetAlarmSnoozeCountEvent(id))
    }

    fun setNextAlarm(alarm: MyAlarm, id: Int, needPreNotice: Boolean) {
        if(alarm.repeat) {
            LogUtil.printDebugLog(this@AlarmDeactivateService.javaClass, "setNextAlarm() $id")
            val time = TimeCalculator.getMilliSecondsForScheduling(alarm)
            alarmManager.startAlarm(id, time)
            if(needPreNotice) {
                alarmManager.startAlarmForPreNotice(id, time)
            }
        }
    }
}