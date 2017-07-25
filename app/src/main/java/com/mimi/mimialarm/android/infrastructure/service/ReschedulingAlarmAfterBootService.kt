package com.mimi.mimialarm.android.infrastructure.service

import android.app.IntentService
import android.content.Intent
import com.mimi.data.RealmDataUtils
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.utils.TimeCalculator
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 25..
 */
class ReschedulingAlarmAfterBootService : IntentService("ReschedulingAlarmAfterBootService") {

    @Inject lateinit var alarmManager: AlarmManager

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.printDebugLog(this@ReschedulingAlarmAfterBootService.javaClass, "onCreate()")
        buildComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtils.printDebugLog(this@ReschedulingAlarmAfterBootService.javaClass, "onHandleIntent()")

        rescheduleAlarm()
    }

    fun rescheduleAlarm() {
        val realm = Realm.getDefaultInstance()
        RealmDataUtils.findObjects<MyAlarm>(realm).filter { it.enable }
                .forEach {
                    LogUtils.printDebugLog(this@ReschedulingAlarmAfterBootService.javaClass, "rescheduleAlarm() id : " + it.id)
                    val time = TimeCalculator.getMilliSecondsForScheduling(it)
                    alarmManager.startAlarm(it.id!!, time)
                }
        realm.close()
    }

}