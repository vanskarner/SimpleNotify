package com.vanskarner.samplenotify.internal

import android.Manifest
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
import kotlinx.coroutines.test.runTest
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
        val actualNotificationId = notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, actualNotification.id)
        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification.notification.channelId)
        assertEquals(
            progressData.currentValue,
            actualNotification.notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            actualNotification.notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
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
        val actualNotificationId = notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }

        assertNotNull(actualNotification.notification)
        assertEquals(DEFAULT_CHANNEL_ID, actualNotification.notification.channelId)
        assertEquals(
            0,
            actualNotification.notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            false,
            actualNotification.notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
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
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == data.id }

        assertNotNull(actualNotification.notification)
        assertEquals(expectedChannel.id, actualNotification.notification.channelId)
        assertEquals(
            progressData.currentValue,
            actualNotification.notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            actualNotification.notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
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
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == data.id }

        assertNotNull(actualNotification.notification)
        assertEquals(expectedChannel.id, actualNotification.notification.channelId)
        assertEquals(
            0,
            actualNotification.notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            false,
            actualNotification.notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

}