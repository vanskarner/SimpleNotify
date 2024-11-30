package com.vanskarner.simplenotify.internal

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.waitForNotification
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyGeneratorTest {
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
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, actualStatusBarNotification.id)
        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
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
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasProgressAndHasChannel_useSpecifiedChannel() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notificationManager)
        val data = TestDataProvider.basicData()
        data.id = 1
        val progressData = TestDataProvider.progressData(false)
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = expectedChannelId,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(data.id ?: 0)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualStatusBarNotification.notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun show_whenHasIdAndHasNoProgressAndHasChannel_useSpecifiedChannel() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notificationManager)
        val data = TestDataProvider.basicData()
        data.id = 2
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannelId,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(data.id ?: 0)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualStatusBarNotification.notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertEquals(0, actualProgress)
        assertEquals(false, actualIndeterminate)
    }

    @Test
    fun show_whenIsCallData_setDefaultCallChannel() = runTest {
        val data = TestDataProvider.callData()
        data.id = 3
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationPair = createNotificationForCall(data)

        assertEquals(data.id, actualNotificationPair.first)
        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotificationPair.second)
    }

    @Test
    fun show_whenIsCallDataAndHasChannel_useSpecifiedChannel() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notificationManager)
        val data = TestDataProvider.callData()
        data.id = 4
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannelId,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationPair = createNotificationForCall(data)

        assertEquals(data.id, actualNotificationPair.first)
        assertNotificationChannelId(expectedChannelId, actualNotificationPair.second)
    }

    @Test
    fun show_whenIsCallAndHasProgress_setDefaultCallChannel() = runTest {
        val data = TestDataProvider.callData()
        val progressData = TestDataProvider.progressData(false)
        data.id = 5
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = progressData,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationPair = createNotificationForCall(data)

        assertEquals(data.id, actualNotificationPair.first)
        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotificationPair.second)
    }

    @Test
    fun show_whenIsDuoMessaging_setDefaultMessagingChannel() = runTest {
        val data = TestDataProvider.duoMessageData(context, "contact_123")
        data.id = 6
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
    }

    @Test
    fun show_whenIsDuoMessagingAndHasChannel_useSpecifiedChannel() = runTest {
        val data = TestDataProvider.duoMessageData(context, "contact_124")
        val expectedChannelId = TestDataProvider.createChannel(notificationManager)
        data.id = 7
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannelId,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(expectedChannelId, actualNotification)
    }

    @Test
    fun show_whenIsGroupMessaging_setDefaultMessagingChannel() = runTest {
        val data = TestDataProvider.groupMessageData(context, "contact_125")
        data.id = 8
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
    }

    @Test
    fun show_whenIsGroupMessagingAndHasChannel_useSpecifiedChannel() = runTest {
        val data = TestDataProvider.groupMessageData(context, "contact_126")
        val expectedChannelId = TestDataProvider.createChannel(notificationManager)
        data.id = 9
        notifyGenerator = NotifyGenerator(
            context = context,
            data = data,
            extra = ExtraData(),
            progressData = null,
            channelId = expectedChannelId,
            actions = emptyArray(),
            stackableData = null
        )
        val actualNotificationId = notifyGenerator.show().first
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(data.id, actualNotificationId)
        assertNotificationChannelId(expectedChannelId, actualNotification)
    }

    @Test
    fun showOrGenerate_withValidData_shouldBeWithinRange() {
        val expectedData = TestDataProvider.basicData()
        notifyGenerator = NotifyGenerator(
            context = context,
            data = expectedData,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )
        val notificationUsingShow = notifyGenerator.show()
        val notificationUsingGenerate = notifyGenerator.generateNotificationWithId()
        val range = RANGE_NOTIFICATION.first..RANGE_NOTIFICATION.second

        assertTrue(
            "Number ${notificationUsingShow.first} should be in range $range",
            notificationUsingShow.first in range
        )
        assertEquals(INVALID_NOTIFICATION_ID, notificationUsingShow.second)
        assertTrue(
            "Number ${notificationUsingGenerate.first} should be in range $range",
            notificationUsingGenerate.first in range
        )
        assertNotNull(notificationUsingGenerate.second)
    }

    @Test
    fun showOrGenerate_withNullData_shouldBeInvalidId() {
        notifyGenerator = NotifyGenerator(
            context = context,
            data = null,
            extra = ExtraData(),
            progressData = null,
            channelId = null,
            actions = emptyArray(),
            stackableData = null
        )

        val notificationUsingShow = notifyGenerator.show()
        val notificationUsingGenerate = notifyGenerator.generateNotificationWithId()

        assertEquals(INVALID_NOTIFICATION_ID, notificationUsingShow.first)
        assertEquals(INVALID_NOTIFICATION_ID, notificationUsingShow.second)
        assertEquals(INVALID_NOTIFICATION_ID, notificationUsingGenerate.first)
        assertNull(notificationUsingGenerate.second)
    }

    private suspend fun createNotificationForCall(data: Data.CallData): Pair<Int, Notification> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                // From API 31, CallStyle notifications must either be for a foreground Service
                val actualNotificationWithId = notifyGenerator.generateNotificationWithId()
                val actualNotification = actualNotificationWithId.second
                    ?: throw IllegalStateException("The notification generated is invalid")
                Pair(actualNotificationWithId.first, actualNotification.build())
            }

            else -> {
                notifyGenerator.show()
                val actualStatusBarNotification =
                    notificationManager.waitForNotification(data.id ?: 0)
                val actualNotification = actualStatusBarNotification.notification
                Pair(actualStatusBarNotification.id, actualNotification)
            }
        }
    }

}
