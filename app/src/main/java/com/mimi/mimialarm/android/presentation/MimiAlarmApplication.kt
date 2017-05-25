package com.mimi.mimialarm.android.presentation

import android.app.Application
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.service.ActivityManager
import dagger.internal.DaggerCollections
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject


/**
 * Created by MihyeLee on 2017. 5. 24..
 */
class MimiAlarmApplication : Application() {

    lateinit var component: ApplicationComponent
    @Inject
    lateinit var activityManager: ActivityManager

    override fun onCreate() {
        super.onCreate()

        component = DaggerApplicationComponent.builder().build()  //.applicationModule(ApplicationModule(this)).build()
        component.inject(this)

        init()
    }

    fun init() {
        registerActivityLifecycleCallbacks(activityManager)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}