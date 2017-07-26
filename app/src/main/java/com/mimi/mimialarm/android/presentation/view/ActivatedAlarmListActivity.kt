package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.mimi.data.DBManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.presentation.viewmodel.ActivatedAlarmListViewModel
import com.mimi.mimialarm.databinding.ActivityActivatedAlarmListBinding
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 7. 26..
 */
class ActivatedAlarmListActivity : AppCompatActivity() {

    lateinit var binding: ActivityActivatedAlarmListBinding
    @Inject lateinit var dbManager: DBManager
    @Inject lateinit var dataManager: ApplicationDataManager
    @Inject lateinit var bus: Bus
    @Inject lateinit var viewModel: ActivatedAlarmListViewModel

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtils.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activated_alarm_list)
        binding.activatedAlarmListViewModel = viewModel

        init()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        val ab = supportActionBar
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(true)
            ab.setDisplayShowHomeEnabled(true)
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewResourceId = item.itemId
        if (viewResourceId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun init() {
        binding.list.layoutManager = LinearLayoutManager(this)
    }
}