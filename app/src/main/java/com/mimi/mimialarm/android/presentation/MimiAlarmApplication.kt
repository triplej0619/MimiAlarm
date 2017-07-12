package com.mimi.mimialarm.android.presentation

import android.support.multidex.MultiDexApplication
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.service.MimiActivityManager
import com.mimi.mimialarm.core.infrastructure.UIManager
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject


/**
 * Created by MihyeLee on 2017. 5. 24..
 */
class MimiAlarmApplication : MultiDexApplication() {

    lateinit var component: ApplicationComponent
    @Inject
    lateinit var uiManager: UIManager

    fun buildComponent(): ApplicationComponent {
        component = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this)).build()
        return component
    }

    override fun onCreate() {
        super.onCreate()

        buildComponent().inject(this)

        init()
    }

    fun init() {
//        Stetho.initializeWithDefaults(this)

        Realm.init(this)

        registerActivityLifecycleCallbacks(uiManager as MimiActivityManager)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}