package com.vanskarner.samplenotify.types.call

import android.content.Intent
import android.widget.ArrayAdapter
import com.vanskarner.samplenotify.CallNotificationService
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.databinding.MainActivityBinding

const val EXTRA_CALL_TYPE = "CALL_TYPE"

fun showCallTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Call - Incoming" to ::incoming,
        "Call - Ongoing" to ::ongoing,
        "Call - Screening" to ::screening
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun incoming(activity: MainActivity) {
    val serviceIntent = Intent(activity, CallNotificationService::class.java)
    serviceIntent.putExtra(EXTRA_CALL_TYPE, "incoming")
    activity.startService(serviceIntent)
}

private fun ongoing(activity: MainActivity) {
    val serviceIntent = Intent(activity, CallNotificationService::class.java)
    serviceIntent.putExtra(EXTRA_CALL_TYPE, "ongoing")
    activity.startService(serviceIntent)
}

private fun screening(activity: MainActivity) {
    val serviceIntent = Intent(activity, CallNotificationService::class.java)
    serviceIntent.putExtra(EXTRA_CALL_TYPE, "screening")
    activity.startService(serviceIntent)
}