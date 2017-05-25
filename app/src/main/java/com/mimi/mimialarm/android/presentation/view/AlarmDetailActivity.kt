package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.mimi.mimialarm.R
import com.mimi.mimialarm.databinding.ActivityAlarmDetailBinding

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmDetailActivity : AppCompatActivity() {

    var binding: ActivityAlarmDetailBinding? = null
//    val binding: ActivityAlarmDetailBinding by lazy { DataBindingUtil.setContentView<ActivityAlarmDetailBinding>(this, R.layout.activity_alarm_detail) }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAlarmDetailBinding>(this, R.layout.activity_alarm_detail)
    }
}
