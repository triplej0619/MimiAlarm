package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TimePicker


/**
 * Created by MihyeLee on 2017. 7. 31..
 */
class CustomTimerPicker : TimePicker {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null && ev.actionMasked == MotionEvent.ACTION_DOWN) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return false
    }
}