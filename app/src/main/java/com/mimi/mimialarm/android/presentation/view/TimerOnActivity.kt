package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import com.mimi.data.DBManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.service.TimerOnBroadcastReceiver
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.android.utils.LogUtils
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

    var selectedRingtone: Ringtone? = null
    var vibrator: Vibrator? = null

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.printDebugLog(this@TimerOnActivity.javaClass, "onCreate")

        super.onCreate(savedInstanceState)
        buildComponent().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer_on)
        binding.timerOnViewModel = viewModel

        init()
    }

    fun init() {
        viewModel.timerId = intent.getIntExtra(TimerOnBroadcastReceiver.KEY_TIMER_ID, -1)
        viewModel.startCommand.execute(Unit)

        // TODO disable for test
//        playVibration()
//        playRingtone()
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedRingtone?.stop()
        vibrator?.cancel()
    }

    fun playVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(longArrayOf(100, 1000), 0)
    }

    fun playRingtone() {
        selectedRingtone = ContextUtils.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), viewModel.DEFAULT_VOLUME)
        selectedRingtone?.play()
    }

    override fun onBackPressed() {
    }
}