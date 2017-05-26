package com.mimi.mimialarm.android.presentation.service

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.mimi.mimialarm.android.infrastructure.StartAlarmDetailActivityEvent
import com.mimi.mimialarm.android.presentation.*
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

    fun buildComponent(application: MimiAlarmApplication): ActivityComponent {
        return DaggerActivityComponent.builder().applicationComponent((application).component).viewModelModule(ViewModelModule()).build()
    }

    private var currentActivity: Activity? = null

    constructor(application: MimiAlarmApplication) {
        buildComponent(application).inject(this)
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

    @Subscribe
    fun answerStartAlarmDetailActivity(event: StartAlarmDetailActivityEvent) {
        startActivityWithoutExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java)
    }
}
