package com.mimi.mimialarm.android.utils

import android.content.Context
import android.content.pm.PackageManager

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
    }
}