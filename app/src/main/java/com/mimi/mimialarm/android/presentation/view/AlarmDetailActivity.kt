package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.TimePicker
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.databinding.ActivityAlarmDetailBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmDetailBinding
    @Inject lateinit var viewModel: AlarmDetailViewModel

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        buildComponent().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_detail)
        binding.alarmDetailViewModel = viewModel

        init();
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

//        binding.soundSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                viewModel.mediaIndex.set(position)
//            }
//        }

        binding.timePicker?.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val calendar: GregorianCalendar = GregorianCalendar()
                calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(GregorianCalendar.MINUTE, minute)

                if(viewModel.endTime.get().time != calendar.time.time) {
                    viewModel.endTime.set(calendar.time)
                }
            }
        })
    }
}
