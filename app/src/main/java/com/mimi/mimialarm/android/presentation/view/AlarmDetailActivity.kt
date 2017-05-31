package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmDetailViewModel
import com.mimi.mimialarm.databinding.ActivityAlarmDetailBinding
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmDetailActivity : AppCompatActivity() {

    var binding: ActivityAlarmDetailBinding? = null
    @Inject lateinit var viewModel: AlarmDetailViewModel

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder().applicationComponent((application as MimiAlarmApplication).component).viewModelModule(ViewModelModule()).build()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        buildComponent().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_detail)
        binding?.alarmDetailViewModel = viewModel
    }
}
