package com.mimi.mimialarm.android.presentation.service

import android.app.Activity
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.mimi.mimialarm.android.utils.BundleKey
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 25..
 */

class MimiActivityManager @Inject constructor(private val application: MimiAlarmApplication, private val bus: Bus)
    : Application.ActivityLifecycleCallbacks, UIManager {

    private var currentActivity: Activity? = null

    init {
        bus.register(this)
    }

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = null
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    fun <T> startActivity(afterActivityClass: Class<T>) {
        currentActivity?.let {
            val intent = Intent(currentActivity, afterActivityClass)
            currentActivity?.startActivity(intent)
        }
    }

    fun <T> startActivityWithExtras(afterActivityClass: Class<T>, bundle: Bundle) {
        currentActivity?.let {
            val intent = Intent(currentActivity, afterActivityClass)
            intent.putExtras(bundle)
            currentActivity?.startActivity(intent)
        }
    }

    fun <T> startActivityWithoutExtrasForResult(afterActivityClass: Class<T>, requestCode: Int) {
        currentActivity?.let {
            val intent = Intent(currentActivity, afterActivityClass)
            currentActivity?.startActivityForResult(intent, requestCode)
        }
    }

    override fun finishForegroundActivity() {
        currentActivity?.finish()
    }

    override fun startAlarmDetailActivityForNew() {
        startActivity<AlarmDetailActivity>(AlarmDetailActivity::class.java)
    }

    override fun startAlarmDetailActivityForUpdate(alarmId: Int?) {
        if(alarmId != null) {
            val bundle: Bundle = Bundle()
            bundle.putInt(BundleKey.ALARM_ID.key, alarmId)
            startActivityWithExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java, bundle)
        }
    }

    override fun showAlertDialog(msg: String, title: String, cancelable: Boolean) {
        showAlertDialog(msg, title, cancelable, null, null)
    }

    override fun showAlertDialog(msg: String, title: String, cancelable: Boolean, okCallback: Command?, cancelCallback: Command?) {
        if(currentActivity != null) {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(currentActivity!!)
            if(title.isNotEmpty()) {
                alertDialogBuilder.setTitle(title)
            }
            alertDialogBuilder.setMessage(msg)
            alertDialogBuilder.setCancelable(cancelable)
            okCallback.let {
                alertDialogBuilder.setPositiveButton(R.string.ok) { dialog, which -> okCallback?.execute(Unit) }
            }
            cancelCallback.let {
                alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, which -> cancelCallback?.execute(Unit) }
            }
            alertDialogBuilder.create().show()
        }
    }

    override fun showToast(msg: String) {
        if(currentActivity != null) {
            Toast.makeText(currentActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
