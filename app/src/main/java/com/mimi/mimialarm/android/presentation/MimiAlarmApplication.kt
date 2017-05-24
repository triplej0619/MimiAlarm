package com.mimi.mimialarm.android.presentation

import android.app.Application
import com.mimi.mimialarm.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig



/**
 * Created by MihyeLee on 2017. 5. 24..
 */
class MimiAlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        init()
    }

    fun init() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}