package com.mimi.mimialarm.android.utils

import android.databinding.BindingAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.TextChanger

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
                        view.text = textChanger.getChangedText(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }
        }
    }
}