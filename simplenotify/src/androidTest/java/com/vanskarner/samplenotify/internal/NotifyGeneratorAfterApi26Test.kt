package com.vanskarner.samplenotify.internal

import  android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitForNotification
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class NotifyGeneratorAfterApi26Test {
    private lateinit var notifyGenerator: NotifyGenerator
    private lateinit var notificationManager: NotificationManager
    private lateinit var context: Context

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun show_whenHasProgressAndHasNoChannel_setDefaultProgressChannel() = runTest {
        val data = TestDataProvider.basicData()
        data.id = null
        val progressData = TestDataProvider.progressData(false)
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val expectedNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(expectedNotificationId)
        val actualNotification = actualStatusBarNotification?.notification
        val actualExtras = actualNotification?.extras
        val actualProgress = actualExtras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, actualStatusBarNotification?.id)
        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification?.channelId)
        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun show_whenHasNoProgressAndHasNoChannel_setDefaultChannel() = runTest {
        val data = TestDataProvider.basicData()
        data.id = null
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val expectedNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(expectedNotificationId)
        val actualNotification = actualStatusBarNotification?.notification
        val actualExtras = actualNotification?.extras
        val actualProgress = actualExtras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        assertEquals(DEFAULT_CHANNEL_ID, actualNotification?.channelId)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasProgressAndHasChannel_useSpecifiedChannel() = runTest {
        val expectedChannel = TestDataProvider.notificationChannel()
        notificationManager.createNotificationChannel(expectedChannel)
        val data = TestDataProvider.basicData()
        data.id = 1
        val progressData = TestDataProvider.progressData(false)
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = expectedChannel.id,
            actions = emptyArray(),
            stackableData = null
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(data.id ?: 0)
        val actualExtras = actualStatusBarNotification?.notification?.extras
        val actualProgress = actualExtras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualStatusBarNotification?.id)
        assertEquals(expectedChannel.id, actualStatusBarNotification?.notification?.channelId)
        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasNoProgressAndHasChannel_useSpecifiedChannel() = runTest {
        val expectedChannel = TestDataProvider.notificationChannel()
        notificationManager.createNotificationChannel(expectedChannel)
        val data = TestDataProvider.basicData()
        data.id = 2
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannel.id,
            actions = emptyArray(),
            stackableData = null
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(data.id ?: 0)
        val actualExtras = actualStatusBarNotification?.notification?.extras
        val actualProgress = actualExtras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualStatusBarNotification?.id)
        assertEquals(expectedChannel.id, actualStatusBarNotification?.notification?.channelId)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

}