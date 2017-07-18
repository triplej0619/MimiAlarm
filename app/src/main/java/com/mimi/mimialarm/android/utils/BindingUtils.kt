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
import kotlinx.android.synthetic.main.fragment_timer.*

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
                if(viewModel.hour.get() > 0) {
                    text += viewModel.hour.get().toString() + view.context.getString(R.string.hour)
                }
                if(viewModel.minute.get() > 0) {
                    text += " " + viewModel.minute.get().toString() + view.context.getString(R.string.minute)
                }
                if(viewModel.second.get() > 0) {
                    text += " " + viewModel.second.get().toString() + view.context.getString(R.string.second)
                }
                text += " " + view.context.getString(R.string.setting)
                view.text = text
            }
        }
    }
}