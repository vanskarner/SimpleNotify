package com.vanskarner.samplenotify.internal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.ConditionalPermissionRule
import com.vanskarner.simplenotify.test.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class NotifyChannelAfterApi26Test {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifyChannel = NotifyChannel
    }

    @Test
    fun applyDefaultChannel_shouldBeCreated() {
        val channelId = notifyChannel.applyDefaultChannel(appContext)
        val notificationChannel = notificationManager.getNotificationChannel(channelId)

        assertNotNull(notificationChannel)
        assertEquals(DEFAULT_CHANNEL_ID, notificationChannel.id)
    }

    @Test
    fun applyProgressChannel_shouldBeCreated() {
        val channelId = notifyChannel.applyProgressChannel(appContext)
        val notificationChannel = notificationManager.getNotificationChannel(channelId)

        assertNotNull(notificationChannel)
        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, notificationChannel.id)
    }

    @Test
    fun checkChannelNotExists_shouldBeVerified() {
        val channel1 = notifyChannel.checkChannelNotExists(appContext, "NotExist")
        val testNotificationChannel = NotificationChannel(
            "testId",
            "Test Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(testNotificationChannel)
        val channel2 = notifyChannel.checkChannelNotExists(appContext, testNotificationChannel.id)

        assertTrue(channel1)
        assertFalse(channel2)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() = runTest {
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
    fun cancelAllNotification_shouldBeCanceled() = runTest {
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