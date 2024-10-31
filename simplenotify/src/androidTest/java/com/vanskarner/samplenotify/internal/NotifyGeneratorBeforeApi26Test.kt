package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitActiveNotifications
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun show_whenHasProgressAndHasNoChannel_shouldBeShown() = runTest {
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
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
        val actualNotification = actualStatusBarNotification?.notification
        val actualProgress = actualNotification?.extras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualNotification?.extras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, actualStatusBarNotification?.id)
        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun show_whenHasNoProgressAndHasNoChannel_shouldBeShown() = runTest {
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
        val actualProgress = actualNotification?.extras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualNotification?.extras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasProgressAndHasChannel_shouldBeShown() = runTest {
        val data = TestDataProvider.basicData()
        data.id = 111
        val progressData = TestDataProvider.progressData(false)
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = "testId",
            actions = emptyArray(),
        )
        notifyGenerator.show()
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
        val actualNotification = actualStatusBarNotification?.notification
        val actualProgress = actualNotification?.extras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualNotification?.extras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualStatusBarNotification?.id)
        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasNoProgressAndHasChannel_shouldBeShown() = runTest {
        val data = TestDataProvider.basicData()
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
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
        val actualNotification = actualStatusBarNotification?.notification
        val actualProgress = actualNotification?.extras?.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualNotification?.extras?.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualStatusBarNotification?.id)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

}