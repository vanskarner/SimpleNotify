package com.vanskarner.samplenotify.internal

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitForNotification
import com.vanskarner.samplenotify.common.waitForEmptyListNotifications
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
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
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifyChannel = NotifyChannel
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun applyDefaultChannel_shouldBeCreated() {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val notificationChannel = notificationManager.getNotificationChannel(channelId)

        assertNotNull(notificationChannel)
        assertEquals(DEFAULT_CHANNEL_ID, notificationChannel.id)
    }

    @Test
    fun applyProgressChannel_shouldBeCreated() {
        val channelId = notifyChannel.applyProgressChannel(context)
        val notificationChannel = notificationManager.getNotificationChannel(channelId)

        assertNotNull(notificationChannel)
        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, notificationChannel.id)
    }

    @Test
    fun checkChannelNotExists_shouldBeVerified() {
        val channel1 = notifyChannel.checkChannelNotExists(context, "NotExist")

        assertTrue(channel1)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val expectedNotificationId = 1
        val notifyBuilder = TestDataProvider.basicNotification(context, channelId)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        notifyChannel.cancelNotification(context, expectedNotificationId)
        assertEquals(0, notificationManager.waitForEmptyListNotifications().size)
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val expectedNotificationId = 2
        val notifyBuilder = TestDataProvider.basicNotification(context, channelId)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        notifyChannel.cancelAllNotification(context)
        assertEquals(0, notificationManager.waitForEmptyListNotifications().size)
    }

}