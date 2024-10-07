package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
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
    fun show_whenHasProgressAndHasNoChannel_shouldBeShown() = runTest {
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
            channelId = null,
            actions = emptyArray(),
        )
        val actualNotificationId = notifyGenerator.show()
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }
        val actualNotification = statusBarNotification.notification

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, statusBarNotification.id)
        assertEquals(
            progressData.currentValue,
            actualNotification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            actualNotification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

    @Test
    fun show_whenHasNoProgressAndHasNoChannel_shouldBeShown() = runTest {
        val data = Data.BasicData()
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
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == actualNotificationId }
        val actualNotification = statusBarNotification.notification

        assertNotNull(actualNotification)
        assertEquals(0, actualNotification.extras.getInt(NotificationCompat.EXTRA_PROGRESS))
        assertEquals(
            false,
            actualNotification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

    @Test
    fun show_whenHasIdAndHasProgressAndHasChannel_shouldBeShown() = runTest {
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
            channelId = "testId",
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == data.id }
        val actualNotification = statusBarNotification.notification

        assertNotNull(actualNotification)
        assertEquals(
            progressData.currentValue,
            actualNotification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            actualNotification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

    @Test
    fun show_whenHasIdAndHasNoProgressAndHasChannel_shouldBeShown() = runTest {
        val data = Data.BasicData()
        data.id = 111
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = "testId",
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == data.id }
        val actualNotification = statusBarNotification.notification

        assertNotNull(actualNotification)
        assertEquals(0, actualNotification.extras.getInt(NotificationCompat.EXTRA_PROGRESS))
        assertEquals(
            false,
            actualNotification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

}