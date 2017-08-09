package com.mimi.mimialarm.android.presentation.view

import android.app.NotificationManager
import android.content.Context
import android.databinding.DataBindingUtil
import android.media.Ringtone
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.mimi.mimialarm.android.utils.LogUtil
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

    val TERMINATE_TIME = 1000 * 30

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

    val terminateHandler: Handler = Handler(Handler.Callback { msg ->
        LogUtil.printDebugLog(this@AlarmOnActivity.javaClass, "terminateHandler receive msg")
        msg?.let {
            this@AlarmOnActivity.finish()
        }
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_on)
        binding.alarmOnViewModel = viewModel

        init()
        setTerminateTimer()
    }

    fun setTerminateTimer() {
        if(dataManager.getAlarmCloseTiming() != 0) {
            LogUtil.printDebugLog(this@AlarmOnActivity.javaClass, "setTerminateTimer()")
            terminateHandler.sendEmptyMessageDelayed(0, TERMINATE_TIME.toLong())
        }
    }

    fun init() {
        viewModel.alarmId = intent.getIntExtra(AlarmOnBroadcastReceiver.KEY_ALARM_ID, -1)
        deletePreNoticeNotification(viewModel.alarmId!!)
        wakeUpScreen()

        bus.register(this)

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

    fun deletePreNoticeNotification(id: Int) {
        val nm: NotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(id)
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
        terminateHandler.removeMessages(0)
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