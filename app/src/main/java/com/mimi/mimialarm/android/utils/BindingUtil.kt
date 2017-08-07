package com.mimi.mimialarm.android.utils

import android.databinding.BindingAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.mimi.mimialarm.R
import com.mimi.mimialarm.core.presentation.viewmodel.TimerListItemViewModel
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TextChanger
import com.mimi.mimialarm.core.utils.TimeCalculator
import kotlinx.android.synthetic.main.fragment_timer.*

/**
 * Created by MihyeLee on 2017. 5. 31..
 */
class BindingUtil {
    companion object {
        @BindingAdapter("bind:clickEvent")
        @JvmStatic
        fun executeEvent(view: View, command: Command?) {
            view.setOnClickListener {
                command?.execute(Unit)
            }
        }

        @BindingAdapter("bind:textChanger")
        @JvmStatic
        fun bindTextChanger(view: View, textChanger: TextChanger) {
            if(view is TextView) {
                view.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (textChanger.isNeedChange(s.toString())) {
                            s?.replace(0, s.length, textChanger.getChangedText(s.toString()))
                        }
                    }
                })
            }
        }

//        @BindingAdapter(value = arrayOf("timerTextHour", "timerTextMinute", "timerTextSecond"), requireAll = true)
        @BindingAdapter("bind:timerText")
        @JvmStatic
        fun bindTimerText(view: View, viewModel: TimerListItemViewModel) {
            if(view is TextView) {
                var text: String = ""
                val hour = TimeCalculator.getHourFromSeconds(viewModel.baseTime)
                val minute = TimeCalculator.getMinuteFromSeconds(viewModel.baseTime)
                val second = TimeCalculator.getSecondFromSeconds(viewModel.baseTime)
                if(hour > 0) {
                    text += hour.toString() + view.context.getString(R.string.hour)
                }
                if(minute > 0) {
                    text += " " + minute.toString() + view.context.getString(R.string.minute)
                }
                if(second > 0) {
                    text += " " + second.toString() + view.context.getString(R.string.second)
                }
                text += " " + view.context.getString(R.string.setting)
                view.text = text
            }
        }
    }
}