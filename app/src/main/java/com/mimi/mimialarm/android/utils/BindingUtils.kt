package com.mimi.mimialarm.android.utils

import android.databinding.BindingAdapter
import android.view.View
import com.mimi.mimialarm.core.utils.Command

/**
 * Created by MihyeLee on 2017. 5. 31..
 */
class BindingUtils {
    companion object {
        @BindingAdapter("bind:clickEvent")
        @JvmStatic
        fun executeEvent(view: View, command: Command?) {
            view.setOnClickListener {
                command?.execute(Unit)
            }
        }
    }
}