package com.mimi.mimialarm.android.presentation.service

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.mimi.mimialarm.android.infrastructure.FinishForegroundActivityEvent
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

class MimiActivityManager @Inject constructor(private val application: MimiAlarmApplication, private val bus: Bus)
    : Application.ActivityLifecycleCallbacks, UIManager {

    private var currentActivity: Activity? = null

    init {
        bus.register(this)
    }

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = null
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    fun <T> startActivityWithoutExtras(afterActivityClass: Class<T>) {
        currentActivity?.let {
            val intent = Intent(currentActivity, afterActivityClass)
            currentActivity?.startActivity(intent)
        }
    }

//    @Subscribe
//    fun answerStartAlarmDetailActivity(event: StartAlarmDetailActivityEvent) {
//        startActivityWithoutExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java)
//    }
//
//    @Subscribe
//    fun answerFinishForegroundActivity(event: FinishForegroundActivityEvent) {
//        currentActivity?.finish()
//    }

    override fun finishForegroundActivity() {
        currentActivity?.finish()
    }

    override fun startAlarmDetailActivity() {
        startActivityWithoutExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java)
    }
}
