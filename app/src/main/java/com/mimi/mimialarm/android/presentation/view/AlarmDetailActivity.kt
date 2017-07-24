package com.mimi.mimialarm.android.presentation.view

import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.databinding.DataBindingUtil
import android.media.AudioAttributes
import android.media.AudioManager
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
import kotlinx.android.synthetic.main.activity_alarm_detail.view.*
import kotlinx.android.synthetic.main.activity_alarm_on.view.*
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
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager


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

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtils.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_detail)
        binding.alarmDetailViewModel = viewModel

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
        }
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

        binding.timePicker?.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val calendar: GregorianCalendar = GregorianCalendar()
                calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(GregorianCalendar.MINUTE, minute)

                viewModel.endTime.set(calendar.time)
            }
        })

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
//        viewModel.repeatLive.observe(this, Observer<Boolean> { t ->
//            if(t != null) {
//                binding.mondayBtn.root.isEnabled = t
//                binding.tuesdayBtn.root.isEnabled = t
//                binding.wednesdayBtn.root.isEnabled = t
//                binding.thursdayBtn.root.isEnabled = t
//                binding.fridayBtn.root.isEnabled = t
//                binding.saturdayBtn.root.isEnabled = t
//                binding.sundayBtn.root.isEnabled = t
//            }
//        })
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
        selectedRingtone = ContextUtils.getRingtone(this, uri, volume)
        selectedRingtone?.play()
    }

    fun stopRingtone() {
        selectedRingtone?.stop()
    }
}
