package com.vanskarner.samplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.NotifyConfig
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.assertNotificationChannelId
import com.vanskarner.samplenotify.common.assertNotificationPriority
import com.vanskarner.samplenotify.common.waitForNotification
import com.vanskarner.samplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.samplenotify.internal.DEFAULT_PROGRESS_CHANNEL_ID
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicTest {
    private lateinit var notifyConfig: NotifyConfig
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        notifyConfig = NotifyConfig(context)
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun show_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingProgress_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val expectedProgress = 50
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.progress {
            currentValue = expectedProgress
            indeterminate = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
        assertEquals(expectedProgress, actualProgress)
        assertTrue(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_whenProgressIsHide_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.progress {
            hide = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingSpecificChannel_shouldBeShown() = runTest {
        val channelId = TestDataProvider.createChannel(notificationManager)
        val expectedData = TestDataProvider.basicData()
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }
            .useChannel(channelId).show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(channelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }
            .addAction {
                icon = expectedAction.icon
                label = expectedAction.label
                pending = expectedAction.pending
            }
            .addReplyAction {
                icon = expectedReplyAction.icon
                label = expectedReplyAction.label
                replyPending = expectedReplyAction.replyPending
                remote = expectedReplyAction.remote
            }
            .show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    @Test
    fun generateNotificationPair_shouldBeGenerated() = runTest {
        val expectedData = TestDataProvider.basicData()
        val actualNotificationPair = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.generateNotificationPair()
        val actualNotification = actualNotificationPair.second!!.build()

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    private fun assertCommonData(expectedData: Data.BasicData, actualNotification: Notification) {
        val actualExtras = actualNotification.extras
        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertEquals(expectedData.title, actualExtras?.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras?.getString(NotificationCompat.EXTRA_TEXT))
    }

}