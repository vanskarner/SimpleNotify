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
import com.vanskarner.samplenotify.common.assertNotificationMessages
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
class DuoMessagingTest {
    private lateinit var notifyConfig: NotifyConfig
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
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
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingProgress_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val expectedProgress = 50
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
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
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
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
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
        }.useChannel(channelId)
            .show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(channelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
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
        val expectedData = TestDataProvider.duoMessageData(context,"contact_015")
        val actualNotificationPair = notifyConfig.asDuoMessaging {
            smallIcon = expectedData.smallIcon
            you = expectedData.you
            contact = expectedData.contact
            messages = expectedData.messages
            useHistoricMessage = false
        }.generateNotificationPair()
        val actualNotification = actualNotificationPair.second!!.build()

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    private fun assertCommonData(
        expectedData: Data.DuoMessageData,
        actualNotification: Notification
    ) {
        val actualExtras = actualNotification.extras
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
            actualNotification
        )
        val actualLastMsg = actualStyle?.messages?.last()

        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertEquals(expectedData.you.name, actualUserName)
        assertEquals(expectedData.contact.name, actualNamePersonAdded)
        assertFalse(actualIsGroupConversation)
        assertNotificationMessages(expectedData.messages, actualNotification)
        assertEquals(expectedData.messages.last().mimeData?.first, actualLastMsg?.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData?.second == actualLastMsg?.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualNotification.category)
    }

}