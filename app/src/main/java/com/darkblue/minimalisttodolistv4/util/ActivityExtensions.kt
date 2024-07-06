package com.darkblue.minimalisttodolistv4.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Change App Icon
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun lightIcon(context: Context) {
    val activity = context.getActivity()
    activity?.let {
        it.enableLightIcon()
        Log.d("TAG","dark")
    }
}
fun darkIcon(context: Context) {
    val activity = context.getActivity()
    activity?.let {
        it.enableDarkIcon()
        Log.d("TAG","dark")
    }
}

fun Activity.enableLightIcon() {
    Log.d("TAG","inside")
    changeEnabledComponent(
        enabled = "$packageName.MainActivityAlias",
        disabled = "$packageName.MainActivity"
    )
}

fun Activity.enableDarkIcon() {
    Log.d("TAG","inside")
    changeEnabledComponent(
        enabled = "$packageName.MainActivity",
        disabled = "$packageName.MainActivityAlias"
    )
}

// Keep the original changeEnabledComponent function
fun Activity.changeEnabledComponent(
    enabled: String,
    disabled: String,
) {
    packageManager.setComponentEnabledSetting(
        ComponentName(this, enabled),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )

    packageManager.setComponentEnabledSetting(
        ComponentName(this, disabled),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}

