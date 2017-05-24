package com.mimi.mimialarm.core.presentation.viewmodel

import android.databinding.Bindable
import com.mimi.mimialarm.BR

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class AlarmListItemViewModel : BaseViewModel() {
    @get:Bindable
    var monday: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.monday)
        }

    @get:Bindable
    var tuesDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.tuesDay)
        }

    @get:Bindable
    var wednesDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.wednesDay)
        }

    @get:Bindable
    var thurseDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.thurseDay)
        }

    @get:Bindable
    var friDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.friDay)
        }

    @get:Bindable
    var saturDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.saturDay)
        }

    @get:Bindable
    var sunDay: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.sunDay)
        }

}