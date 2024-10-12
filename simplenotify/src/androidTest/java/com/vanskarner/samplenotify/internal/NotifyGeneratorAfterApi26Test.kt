package com.vanskarner.samplenotify.internal

import  android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitActiveNotifications
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
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = false
        )
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = null,
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
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
        )
        val expectedNotificationId = notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
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
        val expectedChannel =
            NotificationChannel("testId", "Any Name", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(expectedChannel)
        val data = TestDataProvider.basicData()
        data.id = 111
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = false
        )
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = expectedChannel.id,
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
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
        val expectedChannel =
            NotificationChannel("testId", "Any Name", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(expectedChannel)
        val data = TestDataProvider.basicData()
        data.id = 111
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannel.id,
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
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