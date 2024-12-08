package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.NotifyConfig
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.simplenotify.internal.DEFAULT_PROGRESS_CHANNEL_ID
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
        val notificationId =  10
        notifyConfig.asBasic {
            id = notificationId
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.progress {
            currentValue = expectedProgress
            indeterminate = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(notificationId)
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
        val notificationId =  11
        notifyConfig.asBasic {
            id = notificationId
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.progress {
            hide = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(notificationId)
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
                title = expectedAction.title
                pending = expectedAction.pending
            }
            .addReplyAction {
                icon = expectedReplyAction.icon
                title = expectedReplyAction.title
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
    fun generateBuilder_shouldBeGenerated() = runTest {
        val expectedData = TestDataProvider.basicData()
        val actualBuilder = notifyConfig.asBasic {
            smallIcon = expectedData.smallIcon
            title = expectedData.title
            text = expectedData.text
        }.generateBuilder()
        val actualNotification = actualBuilder.build()

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