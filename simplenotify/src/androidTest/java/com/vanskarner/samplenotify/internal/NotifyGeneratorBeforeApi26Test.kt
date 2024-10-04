package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N_MR1)
class NotifyGeneratorBeforeApi26Test {
    private lateinit var notifyGenerator: NotifyGenerator
    private lateinit var notificationManager: NotificationManager
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Test
    fun show_whenIdIsNullAndProgressIsNotNull_setNotificationAndProgress() = runTest {
        val data = Data.BasicData()
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
            channelData = ChannelData.byDefault(ApplicationProvider.getApplicationContext()),
            actions = emptyArray(),
        )
        val actualNotificationId = notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }

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
    fun show_whenIdIsNullAndProgressIsNull_setNotification() = runTest {
        val data = Data.BasicData()
        data.id = null
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelData = ChannelData.byDefault(ApplicationProvider.getApplicationContext()),
            actions = emptyArray(),
        )
        val actualNotificationId = notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }

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
    fun show_whenIdIsNotNullAndProgressIsNotNull_setNotificationAndProgress() = runTest {
        val data = Data.BasicData()
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
            channelData = ChannelData.byDefault(ApplicationProvider.getApplicationContext()),
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == data.id }

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
    fun show_whenIdIsNotNullAndProgressIsNull_setNotification() = runTest {
        val data = Data.BasicData()
        data.id = 111
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelData = ChannelData.byDefault(ApplicationProvider.getApplicationContext()),
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualNotification =
            notificationManager.activeNotifications.first { it.id == data.id }

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