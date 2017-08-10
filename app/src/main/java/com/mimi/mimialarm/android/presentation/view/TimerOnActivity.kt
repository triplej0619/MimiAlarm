package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.mimi.data.DBManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.service.TimerOnBroadcastReceiver
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.presentation.viewmodel.TimerOnViewModel
import com.mimi.mimialarm.databinding.ActivityTimerOnBinding
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 6. 29..
 */
class TimerOnActivity : AppCompatActivity() {

    lateinit var binding: ActivityTimerOnBinding
    @Inject lateinit var viewModel: TimerOnViewModel
    @Inject lateinit var bus: Bus
    @Inject lateinit var dbManager: DBManager
    @Inject lateinit var dataManager: ApplicationDataManager

    var vibrator: Vibrator? = null
    lateinit var player: MediaPlayer

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.printDebugLog(this@TimerOnActivity.javaClass, "onCreate")
        buildComponent().inject(this)
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer_on)
        binding.timerOnViewModel = viewModel

        init()
    }

    fun init() {
        wakeUpScreen()

        viewModel.timerId = intent.getIntExtra(TimerOnBroadcastReceiver.KEY_TIMER_ID, -1)
        viewModel.startCommand.execute(Unit)

        playVibration()
        playRingtone()
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
        player.stop()
        player.release()
        vibrator?.cancel()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        viewModel.finishViewCommand.execute(Unit)
    }

    fun playVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(longArrayOf(100, 1000), 0)
    }

    fun playRingtone() {
        ContextUtil.setAlarmVolume(this, dataManager.getTimerVolume())
        player = MediaPlayer()
        player.setAudioStreamType(AudioManager.STREAM_ALARM)
        player.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        player.isLooping = true
        player.prepare()
        player.start()
    }

    override fun onBackPressed() {
    }
}