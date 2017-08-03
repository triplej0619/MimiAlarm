package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.media.Ringtone
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.ResetAlarmEvent
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmOnViewModel
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TimeCalculator
import com.mimi.mimialarm.databinding.ActivityAlarmOnBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import java.util.*
import javax.inject.Inject
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION



/**
 * Created by MihyeLee on 2017. 6. 29..
 */
class AlarmOnActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmOnBinding
    @Inject lateinit var viewModel: AlarmOnViewModel
    @Inject lateinit var bus: Bus
    @Inject lateinit var dbManager: DBManager
    @Inject lateinit var dataManager: ApplicationDataManager

    var selectedRingtone: Ringtone? = null
    var vibrator: Vibrator? = null

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtils.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_on)
        binding.alarmOnViewModel = viewModel

        init()
    }

    fun init() {
        wakeUpScreen()

        bus.register(this)

        viewModel.alarmId = intent.getIntExtra(AlarmOnBroadcastReceiver.KEY_ALARM_ID, -1)
        viewModel.startCommand.execute(object : Command {
            override fun execute(arg: Any) {
                playRingtone()
            }
        }, object : Command {
            override fun execute(arg: Any) {
                playVibration()
            }
        })
    }

    fun wakeUpScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        selectedRingtone?.stop()
        vibrator?.cancel()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if(viewModel.willExpire.get() and viewModel.finishInWindow.get()) {
            viewModel.finishViewCommand.execute(Unit)
        } else {
            viewModel.finishWithResetCommand.execute(Unit)
        }
    }

    fun playVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(longArrayOf(100, 1000), 0)
    }

    fun playRingtone() {
        selectedRingtone = ContextUtils.getRingtone(this, Uri.parse(viewModel.mediaSrc), viewModel.soundVolume)
        selectedRingtone?.play()
    }

    override fun onBackPressed() {
    }
}