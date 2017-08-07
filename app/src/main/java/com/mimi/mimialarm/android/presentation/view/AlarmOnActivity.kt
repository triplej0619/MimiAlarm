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
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmOnViewModel
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.databinding.ActivityAlarmOnBinding
import com.squareup.otto.Bus
import javax.inject.Inject





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
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))

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

    fun getSystemUIFlag() : Int = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = getSystemUIFlag()
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        selectedRingtone?.stop()
        vibrator?.cancel()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if(!viewModel.willExpire.get() and viewModel.snooze.get()) {
            viewModel.finishWithResetCommand.execute(Unit)
        } else {
            viewModel.finishViewCommand.execute(Unit)
        }
    }

    fun playVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(longArrayOf(100, 1000), 0)
    }

    fun playRingtone() {
        selectedRingtone = ContextUtil.getRingtone(this, Uri.parse(viewModel.mediaSrc), viewModel.soundVolume)
        selectedRingtone?.play()
    }

    override fun onBackPressed() {
    }
}