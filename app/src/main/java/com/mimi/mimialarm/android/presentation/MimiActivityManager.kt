package com.mimi.mimialarm.android.presentation

import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.view.ActivatedAlarmListActivity
import com.mimi.mimialarm.android.presentation.view.AlarmDetailActivity
import com.mimi.mimialarm.android.utils.BundleKey
import com.mimi.mimialarm.core.infrastructure.UIManager
import com.mimi.mimialarm.core.utils.Command
import com.squareup.otto.Bus
import javax.inject.Inject
import android.app.PendingIntent
import android.net.Uri
import com.mimi.mimialarm.android.infrastructure.service.AlarmDeactivateService
import com.mimi.mimialarm.android.presentation.view.MainActivity
import com.mimi.mimialarm.android.utils.LogUtil
import android.support.v4.content.ContextCompat.startActivity




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
        try {
            currentActivity?.let {
                val intent = Intent(currentActivity, afterActivityClass)
                currentActivity?.startActivity(intent)
            }
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startActivity() ex : " + ex.message)
        }
    }

    fun <T> startActivityWithExtras(afterActivityClass: Class<T>, bundle: Bundle) {
        try {
            currentActivity?.let {
                val intent = Intent(currentActivity, afterActivityClass)
                intent.putExtras(bundle)
                currentActivity?.startActivity(intent)
            }
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startActivityWithExtras() ex : " + ex.message)
        }
    }

    fun <T> startActivityWithoutExtrasForResult(afterActivityClass: Class<T>, requestCode: Int) {
        try {
            currentActivity?.let {
                val intent = Intent(currentActivity, afterActivityClass)
                currentActivity?.startActivityForResult(intent, requestCode)
            }
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startActivityWithoutExtrasForResult() ex : " + ex.message)
        }
    }

    override fun finishForegroundView() {
        try {
            currentActivity?.finish()
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "finishForegroundView() ex : " + ex.message)
        }
    }

    override fun startAlarmDetailViewForNew() {
        try {
            startActivity<AlarmDetailActivity>(AlarmDetailActivity::class.java)
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startAlarmDetailViewForNew() ex : " + ex.message)
        }
    }

    override fun startAlarmDetailViewForUpdate(alarmId: Int?) {
        try {
            if(alarmId != null) {
                val bundle: Bundle = Bundle()
                bundle.putInt(BundleKey.ALARM_ID.key, alarmId)
                startActivityWithExtras<AlarmDetailActivity>(AlarmDetailActivity::class.java, bundle)
            }
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startAlarmDetailViewForUpdate() ex : " + ex.message)
        }
    }

    override fun startActivatedAlarmListView() {
        try {
            startActivity<ActivatedAlarmListActivity>(ActivatedAlarmListActivity::class.java)
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startActivatedAlarmListView() ex : " + ex.message)
        }
    }

    override fun showAlertDialog(msg: String, title: String, cancelable: Boolean) {
        showAlertDialog(msg, title, cancelable, null, null)
    }

    override fun showAlertDialog(msg: String, title: String, cancelable: Boolean, okCallback: Command?, cancelCallback: Command?) {
        try {
            currentActivity?.let {
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(currentActivity!!)
                if (title.isNotEmpty()) {
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
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "showAlertDialog() ex : " + ex.message)
        }
    }

    override fun showToast(msg: String) {
        try {
            Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "showToast() ex : " + ex.message)
        }
    }

    override fun addSnoozeNotification(msg: String, id: Int) {
        LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "addSnoozeNotification() id : $id, msg : $msg")

        val activityIntent = PendingIntent.getActivity(application, 999,
                                Intent(application, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(application)
        builder.setContentTitle(application.getString(R.string.alarm_on_reset_snooze))
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(longArrayOf(0L))
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(activityIntent)

        val dismissIntent = Intent(application, AlarmDeactivateService::class.java)
        dismissIntent.action = AlarmDeactivateService.KEY_ACTION_KILL_SNOOZE
        dismissIntent.putExtra(AlarmDeactivateService.KEY_ID, id)
        val dismissPendingIndent = PendingIntent.getService(application, id, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.addAction(R.drawable.icn_stop, application.getString(R.string.alarm_on_kill_snooze), dismissPendingIndent)

        val nm : NotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, builder.build())
    }

    override fun addPreNoticeNotification(msg: String, id: Int) {
        LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "addPreNoticeNotification() id : $id, msg : $msg")

        val activityIntent = PendingIntent.getActivity(application, 999,
                Intent(application, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(application)
        builder.setContentTitle(application.getString(R.string.alarm_on_scheduled_alarm))
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(activityIntent)

        val dismissIntent = Intent(application, AlarmDeactivateService::class.java)
        dismissIntent.action = AlarmDeactivateService.KEY_ACTION_CANCEL_ALARM
        dismissIntent.putExtra(AlarmDeactivateService.KEY_ID, id)
        val dismissPendingIndent = PendingIntent.getService(application, id, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.addAction(R.drawable.icn_stop, application.getString(R.string.alarm_on_deactivate_alarm), dismissPendingIndent)

        val nm : NotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, builder.build())
    }

    override fun startWebBrowserWithUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            currentActivity?.startActivity(intent)
        } catch (ex: Exception) {
            LogUtil.printDebugLog(this@MimiActivityManager.javaClass, "startWebBrowserWithUrl() ex : " + ex.message)
        }
    }

}
