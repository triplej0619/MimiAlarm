package com.mimi.mimialarm.android.presentation.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.media.Ringtone
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.infrastructure.CancelAlarmEvent
import com.mimi.mimialarm.core.infrastructure.ResetAlarmEvent
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmOnViewModel
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.databinding.ActivityAlarmOnBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 6. 29..
 */
class AlarmOnActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmOnBinding
    @Inject lateinit var viewModel: AlarmOnViewModel
    @Inject lateinit var bus: Bus

    var selectedRingtone: Ringtone? = null
    var vibrator: Vibrator? = null

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildComponent().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_on)
        binding.alarmOnViewModel = viewModel

        init()
    }

    fun init() {
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

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        selectedRingtone?.stop()
        vibrator?.cancel()
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

    @Subscribe
    fun answerCancelAlarmEvent(event: CancelAlarmEvent) {
        event.id?.let {
            ContextUtils.cancelAlarm(this, event.id!!, AlarmOnBroadcastReceiver::class.java, AlarmOnBroadcastReceiver.KEY_ALARM_ID)
//            val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val alarmIntent = Intent(this, AlarmOnBroadcastReceiver::class.java)
//            alarmIntent.putExtra(AlarmOnBroadcastReceiver.KEY_ALARM_ID, event.id!!)
//            val pendingIntent = PendingIntent.getBroadcast(this, event.id!!, alarmIntent, 0)
//            alarmManager.cancel(pendingIntent)
        }
    }

    @Subscribe
    fun answerResetAlarmEvent(event: ResetAlarmEvent) {

    }
}