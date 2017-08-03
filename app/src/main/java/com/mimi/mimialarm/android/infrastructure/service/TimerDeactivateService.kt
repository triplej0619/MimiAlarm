package com.mimi.mimialarm.android.infrastructure.service

import android.app.IntentService
import android.content.Intent
import com.mimi.data.DBManager
import com.mimi.data.RealmDataUtils
import com.mimi.data.model.MyTimer
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.LogUtils
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 20..
 */
class TimerDeactivateService : IntentService("TimerDeactivateService") {

    @Inject lateinit var dbManager: DBManager

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.printDebugLog(this@TimerDeactivateService.javaClass, "onCreate()")
        buildComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtils.printDebugLog(this@TimerDeactivateService.javaClass, "onHandleIntent()")
        deactivateTimers()
    }

    fun deactivateTimers() {
        val realm = Realm.getDefaultInstance() // TODO
        realm.executeTransaction {
            RealmDataUtils.findObjects<MyTimer>(realm).forEach {
                it.activated = false
                LogUtils.printDebugLog(this@TimerDeactivateService.javaClass, "deactivateTimers() : " + it.id)
            }
        }
        realm.close()
    }

}