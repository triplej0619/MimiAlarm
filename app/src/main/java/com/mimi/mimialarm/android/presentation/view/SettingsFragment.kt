package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.presentation.viewmodel.SettingsViewModel
import com.mimi.mimialarm.databinding.FragmentSettingsBinding
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class SettingsFragment : android.support.v4.app.Fragment() {

    var binding: FragmentSettingsBinding? = null
    @Inject
    lateinit var viewModel: SettingsViewModel
    @Inject
    lateinit var bus: Bus

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((activity.application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        buildComponent().inject(this)
        binding?.settingsViewModel = viewModel

        bus.register(this)

        init()

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
    }

    fun init() {
        viewModel.version.set(ContextUtils.getVersion(activity))
    }
}