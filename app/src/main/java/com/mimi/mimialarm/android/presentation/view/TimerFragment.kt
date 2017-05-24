package com.mimi.mimialarm.android.presentation.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class TimerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_timer, container, false)
    }
}
