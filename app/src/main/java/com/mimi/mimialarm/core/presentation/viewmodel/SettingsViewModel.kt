package com.mimi.mimialarm.core.presentation.viewmodel

import com.mimi.mimialarm.core.infrastructure.UIManager
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 6. 27..
 */
class SettingsViewModel @Inject constructor(private val uiManager: UIManager, private val bus: Bus) : BaseViewModel() {

}