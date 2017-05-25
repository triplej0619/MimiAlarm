package com.mimi.mimialarm.android.presentation.service

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.mimi.mimialarm.android.presentation.ApplicationComponent
import com.mimi.mimialarm.android.presentation.DaggerApplicationComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

class ActivityManager: Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var bus: Bus
    val component: ApplicationComponent

    private var currentActivity: Activity? = null

    constructor(application: MimiAlarmApplication) {
        component = DaggerApplicationComponent.builder().build()
        component.inject(application)

        bus.register(this)
    }

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = null
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity?) {
        currentActivity = null
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

    @Subscribe
    fun answerStartAlarmDetailActivity(event: StartAlarmDetailActivityEvent) {
        startActivityWithoutExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java)
    }
}
