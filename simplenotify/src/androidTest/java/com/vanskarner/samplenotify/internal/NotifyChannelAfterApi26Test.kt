package com.vanskarner.samplenotify.internal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.waitActiveNotifications
import com.vanskarner.simplenotify.test.R
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    @After
    fun tearDown(){
        notificationManager.cancelAll()
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
        val expectedNotificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        notifyChannel.cancelNotification(appContext, expectedNotificationId)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyDefaultChannel(appContext)
        val expectedNotificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        notifyChannel.cancelAllNotification(appContext)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }

}