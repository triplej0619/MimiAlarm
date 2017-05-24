package com.mimi.mimialarm.android.presentation.view

import com.mimi.mimialarm.R

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class SettingsFragment : android.support.v4.app.Fragment() {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        return inflater?.inflate(R.layout.fragment_settings, container, false)
    }
}