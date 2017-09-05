package com.mimi.mimialarm.android.presentation.view

import android.arch.lifecycle.LifecycleActivity
import android.databinding.DataBindingUtil
import android.widget.TimePicker
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.BundleKey
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.databinding.ActivityAlarmDetailBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import javax.inject.Inject
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AlertDialog
import kotlin.collections.HashMap
import android.media.Ringtone
import android.os.Build
import android.support.v7.widget.AppCompatSeekBar
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.utils.Command


/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmDetailActivity : LifecycleActivity(), View.OnTouchListener {

    lateinit var binding: ActivityAlarmDetailBinding
    @Inject lateinit var viewModel: AlarmDetailViewModel
    @Inject lateinit var dataManager: ApplicationDataManager

    val ringtones: MutableMap<String, Uri> = HashMap<String, Uri>()
    var selectedRingtone: Ringtone? = null
    var ringtoneIndex: Int = 0
    var ringtoneIndexPrev: Int = 0
    var init = false


    val addOrUpdateAlarmCommand: Command = object : Command {
        override fun execute(arg: Any) {
            if(init) {
                LogUtil.printDebugLog(this@AlarmDetailActivity.javaClass, "addOrUpdateAlarmCommand()")
                val calendar = GregorianCalendar()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    calendar.set(GregorianCalendar.HOUR_OF_DAY, binding.timePicker.hour)
                    calendar.set(GregorianCalendar.MINUTE, binding.timePicker.minute)
                } else {
                    calendar.set(GregorianCalendar.HOUR_OF_DAY, binding.timePicker.currentHour)
                    calendar.set(GregorianCalendar.MINUTE, binding.timePicker.currentMinute)
                }
                viewModel.endTime.set(calendar.time)
                viewModel.addOrUpdateAlarmCommand.execute(Unit)
            }
        }
    }

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_detail)
        binding.alarmDetailViewModel = viewModel
        binding.alarmDetailActivity = this

        getBundleData()
        init()
        observeData()
    }

    fun getBundleData() {
        if(intent.hasExtra(BundleKey.ALARM_ID.key)) {
            viewModel.id = intent.getIntExtra(BundleKey.ALARM_ID.key, 0)
            viewModel.loadAlarmData()

            val calendar: GregorianCalendar = GregorianCalendar()
            calendar.time = viewModel.endTime.get()
            binding.timePicker.currentHour = calendar.get(GregorianCalendar.HOUR_OF_DAY)
            binding.timePicker.currentMinute = calendar.get(GregorianCalendar.MINUTE)

            val snoozeCountArray = resources.getStringArray(R.array.snoozeCountValue)
            val snoozeIntervalArray = resources.getStringArray(R.array.snoozeTimeIntervalValue)
            binding.snoozeCount.setSelection(getIndexInStringArray(viewModel.snoozeCount.get().toString(), snoozeCountArray))
            binding.snoozeInterval.setSelection(getIndexInStringArray(viewModel.snoozeInterval.get().toString(), snoozeIntervalArray))
        }
    }

    fun getIndexInStringArray(value: String, array: Array<String>) : Int {
        return (0..array.size-1).firstOrNull { array[it] == value }
                ?: 0
    }

    fun init() {
        RxAdapterView.itemSelections(binding.snoozeInterval)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ position -> run {
                        val valueArray: Array<String> = resources.getStringArray(R.array.snoozeTimeIntervalValue)
                        if(position < valueArray.size) {
                            viewModel.snoozeInterval.set(valueArray[position].toInt())
                        }
                    }
                })
        RxAdapterView.itemSelections(binding.snoozeCount)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ position -> run {
                        val valueArray: Array<String> = resources.getStringArray(R.array.snoozeCountValue)
                        if(position < valueArray.size) {
                            viewModel.snoozeCount.set(valueArray[position].toInt())
                        }
                    }
                })

        binding.timePicker?.setOnTimeChangedListener { view, hourOfDay, minute ->
            val calendar = GregorianCalendar()
            calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(GregorianCalendar.MINUTE, minute)

            viewModel.endTime.set(calendar.time)
        }

        loadRingtones()
        binding.selectedSound.setOnClickListener {
            createRingtoneListDlg()
        }

        binding.soundVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                stopRingtone()
                viewModel.soundVolume.set(binding.soundVolume.progress)
                playRingtone(ringtones[ringtones.keys.toList()[ringtoneIndex]], binding.soundVolume.progress)
            }

        })

        setStopSoundListener()
        init = true
    }

    fun setStopSoundListener() {
        binding.root.setOnTouchListener(this)
        setStopSoundListenerToChild(binding.root)
    }

    fun setStopSoundListenerToChild(view: View) {
        if(view is ViewGroup) {
            val viewGroup: ViewGroup = view
            (0..viewGroup.childCount)
                    .filter { viewGroup.getChildAt(it) !is AppCompatSeekBar }
                    .forEach {
                        if(viewGroup.getChildAt(it) != null) {
                            viewGroup.getChildAt(it).setOnTouchListener(this)
                            setStopSoundListenerToChild(viewGroup.getChildAt(it))
                        }
                    }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(v != null && v != binding.soundVolume) {
            stopRingtone()
        }
        return false;
    }

    fun observeData() {
        viewModel.snoozeLive.observe(this, android.arch.lifecycle.Observer<Boolean> { t ->
            if(t != null) {
                setEnableView(binding.snoozeInterval, t)
                setEnableView(binding.snoozeCount, t)
            }
        })
        viewModel.soundLive.observe(this, android.arch.lifecycle.Observer<Boolean> { t ->
            if(t != null) {
                setEnableView(binding.selectedSound, t)
                setEnableView(binding.soundVolume, t)
            }
        })
        viewModel.repeatLive.observe(this, android.arch.lifecycle.Observer<Boolean> { t ->
            if(t != null) {
                setEnableView(binding.mondayBtn.root, t)
                setEnableView(binding.tuesdayBtn.root, t)
                setEnableView(binding.wednesdayBtn.root, t)
                setEnableView(binding.thursdayBtn.root, t)
                setEnableView(binding.fridayBtn.root, t)
                setEnableView(binding.saturdayBtn.root, t)
                setEnableView(binding.sundayBtn.root, t)
            }
        })
    }

    fun setEnableView(view: View, enable: Boolean) {
        var alpha = 1.0f
        if(!enable)
            alpha = 0.3f
        view.isEnabled = enable
        view.alpha = alpha
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        viewModel.release()
    }

    fun loadRingtones() {
        val manager = RingtoneManager(this)
        manager.setType(RingtoneManager.TYPE_ALARM)
        val cursor = manager.cursor
        var selectedRingtoneTitle: String = ""
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val ringtoneURI = manager.getRingtoneUri(cursor.position)
            ringtones.put(title, ringtoneURI)
            if(ringtoneURI.toString() == viewModel.mediaSrc) {
                selectedRingtoneTitle = title
            }
        }

        if(selectedRingtoneTitle.isNotEmpty()) {
            ringtoneIndex = ringtones.keys.toList().indexOf(selectedRingtoneTitle)
        }
        setSoundInfo(ringtoneIndex)
    }

    fun setSoundInfo(index: Int) {
        viewModel.mediaSrc = ringtones[ringtones.keys.toList()[index]].toString()
        binding.selectedSound.text = ringtones.keys.toList()[index]
    }

    fun createRingtoneListDlg() {
        ringtoneIndexPrev = ringtoneIndex

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.title_select_sound))
        builder.setSingleChoiceItems(ringtones.keys.toTypedArray(), ringtoneIndex, { dialog, which ->
            ringtoneIndex = which
            stopRingtone()
            playRingtone(ringtones[ringtones.keys.toList()[which]], viewModel.soundVolume.get())
        })
        builder.setPositiveButton(R.string.ok, { dialog, which ->
            ringtoneIndexPrev = ringtoneIndex
            stopRingtone()
            setSoundInfo(ringtoneIndex)
        })
        builder.setNegativeButton(R.string.cancel, { dialog, which ->
            ringtoneIndex = ringtoneIndexPrev
            stopRingtone()
        })

        if (!this.isFinishing) {
            builder.create().show()
        }
    }

    fun playRingtone(uri: Uri?, volume: Int) {
        selectedRingtone = ContextUtil.getRingtone(this, uri, volume)
        selectedRingtone?.play()
    }

    fun stopRingtone() {
        selectedRingtone?.stop()
    }
}
