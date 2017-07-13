package com.mimi.mimialarm.android.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by MihyeLee on 2017. 6. 29..
 */
class ContextUtils {
    companion object {

        fun getVersion(context: Context): String {
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                return packageInfo.versionName
            } catch (ex: PackageManager.NameNotFoundException) {
                return ""
            }
        }

        fun getRingtone(context: Context, uri: Uri?, volume: Int) : Ringtone? {
            var ringtone: Ringtone? = null
            if(uri != null) {
                try {
                    ringtone = RingtoneManager.getRingtone(context, uri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ringtone?.audioAttributes = AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                    } else {
                        ringtone?.streamType = AudioManager.STREAM_ALARM
                    }
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val calculatedVolume: Float = volume.toFloat() / 100.0f * audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM).toFloat()
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, calculatedVolume.toInt(), 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            return ringtone
        }

        fun <T> startAlarm(context: Context, alarmId: Int, time: Long, receiverClass: Class<T>, intentIdKey: String) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, receiverClass)
            alarmIntent.putExtra(intentIdKey, alarmId)
            val pendingIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
            }
        }

        fun <T> cancelAlarm(context: Context, alarmId: Int, receiverClass: Class<T>, intentIdKey: String) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, receiverClass)
            alarmIntent.putExtra(intentIdKey, alarmId)
            val pendingIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, 0)

            alarmManager.cancel(pendingIntent)
        }

        fun <T> startTimer(context: Context, id: Int, time: Long, receiverClass: Class<T>, intentIdKey: String) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, receiverClass)
            alarmIntent.putExtra(intentIdKey, id)
            val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent)
            }
        }
    }
}