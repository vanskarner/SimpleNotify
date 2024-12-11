package com.vanskarner.samplenotify

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.vanskarner.samplenotify.databinding.MainActivityBinding
import com.vanskarner.samplenotify.types.basic.showBasicTypes
import com.vanskarner.samplenotify.types.bigpicture.showBigPictureTypes
import com.vanskarner.samplenotify.types.bigtext.showBigTextTypes
import com.vanskarner.samplenotify.types.call.showCallTypes
import com.vanskarner.samplenotify.types.customdesign.showCustomDesignTypes
import com.vanskarner.samplenotify.types.duomessaging.showDuoMessagingTypes
import com.vanskarner.samplenotify.types.groupmessaging.showGroupMessagingTypes
import com.vanskarner.samplenotify.types.inbox.showInboxTypes

class MainActivity : BaseActivity() {

    private lateinit var binding: MainActivityBinding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            binding.card.visibility = if (isGranted) View.GONE else View.VISIBLE
            if (!isGranted) showPermissionDeniedDialog()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnReset.setOnClickListener { showNotificationTypes() }
        binding.card.visibility = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> View.GONE
            hasNotificationPermission() -> View.GONE
            else -> {
                binding.btnGrant.setOnClickListener {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                View.VISIBLE
            }
        }
        binding.btnReset.performClick()
    }

    private fun showNotificationTypes() {
        val actions = mapOf(
            "Basic" to ::showBasicTypes,
            "BigText" to ::showBigTextTypes,
            "Inbox" to ::showInboxTypes,
            "BigPicture" to ::showBigPictureTypes,
            "DuoMessaging" to ::showDuoMessagingTypes,
            "GroupMessaging" to ::showGroupMessagingTypes,
            "Call" to ::showCallTypes,
            "CustomDesign" to ::showCustomDesignTypes
        )
        binding.gridView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, actions.keys.toList())
        binding.gridView.setOnItemClickListener { _, _, position, _ ->
            actions.values.elementAt(position).invoke(this, binding)
        }
    }

}
