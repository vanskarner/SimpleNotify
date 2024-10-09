package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.simplenotify.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N_MR1)
class NotifyChannelBeforeApi26Test {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifyChannel = NotifyChannel
    }

    @Test
    fun applyDefaultChannel_shouldOnlyReturnId() {
        val channelId = notifyChannel.applyDefaultChannel(appContext)

        assertEquals(DEFAULT_CHANNEL_ID, channelId)
    }

    @Test
    fun applyProgressChannel_shouldOnlyReturnId() {
        val channelId = notifyChannel.applyProgressChannel(appContext)

        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, channelId)
    }

    @Test
    fun checkChannelNotExists_shouldOnlyReturnTrue() {
        val channel1 = notifyChannel.checkChannelNotExists(appContext, "anyId")

        assertTrue(channel1)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() {
        val channelId = notifyChannel.applyDefaultChannel(appContext)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        assertNotNull(notificationManager.activeNotifications.first { it.id == notificationId })
        notifyChannel.cancelNotification(appContext, notificationId)
        Espresso.onIdle()
        assertEquals(0, notificationManager.activeNotifications.size)
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() {
        val channelId = notifyChannel.applyDefaultChannel(appContext)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        assertNotNull(notificationManager.activeNotifications.first { it.id == notificationId })
        notifyChannel.cancelAllNotification(appContext)
        Espresso.onIdle()
        assertEquals(0, notificationManager.activeNotifications.size)
    }

}