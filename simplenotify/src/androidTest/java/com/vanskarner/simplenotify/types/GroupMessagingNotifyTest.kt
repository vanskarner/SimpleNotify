package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.SimpleNotify
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationMessages
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.assertNotificationSound
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_MESSAGING_CHANNEL_ID
import com.vanskarner.simplenotify.internal.INVALID_NOTIFICATION_ID
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
class GroupMessagingNotifyTest {
    private lateinit var context: Context
    private lateinit var notifyManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notifyManager.cancelAll()
    }

    @Test
    fun useBasic_shouldApply() = runTest {
        val expectedData = TestDataProvider.groupMessageData(context, "contact_015")
        val actualNotifyConfig = SimpleNotify.with(context)
            .asGroupMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                conversationTitle = expectedData.conversationTitle
                messages = expectedData.messages
                useHistoricMessage = false
            }
        val actualNotificationIds = actualNotifyConfig.show()
        val actualNotificationGenerated =
            actualNotifyConfig.generateBuilder()?.build() ?: Notification()
        val notificationId = actualNotificationIds.first
        val actualGroupNotificationId = actualNotificationIds.second
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(INVALID_NOTIFICATION_ID, actualGroupNotificationId)
        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotificationGenerated)
        assertCommonData(expectedData, actualNotificationGenerated)
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asGroupMessaging { }
            .extras {
                priority = expectedExtra.priority
                sounds = expectedExtra.sounds
                category = expectedExtra.category
                visibility = expectedExtra.visibility
                ongoing = expectedExtra.ongoing
                color = expectedExtra.color
                timestampWhen = expectedExtra.timestampWhen
                deleteIntent = expectedExtra.deleteIntent
                fullScreenIntent = expectedExtra.fullScreenIntent
                onlyAlertOnce = expectedExtra.onlyAlertOnce
                showWhen = expectedExtra.showWhen
                useChronometer = expectedExtra.useChronometer
                badgeNumber = expectedExtra.badgeNumber
                badgeIconType = expectedExtra.badgeIconType
                shortCutId = expectedExtra.shortCutId
                allowSystemGeneratedContextualActions =
                    expectedExtra.allowSystemGeneratedContextualActions
                remoteInputHistory = expectedExtra.remoteInputHistory
                groupKey = expectedExtra.groupKey
            }
            .generateBuilder()?.build() ?: Notification()
        val expectedPriority = expectedExtra.priority ?: -666
        val actualExtras = actualNotification.extras
        val actualOngoing = actualNotification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        val actualOnlyAlertOnce =
            actualNotification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        val actualShowWhen = actualExtras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        val actualUsesChronometer = NotificationCompat.getUsesChronometer(actualNotification)
        val actualBadgeNumber = actualNotification.number
        val actualRemoteInputHistory =
            actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_REMOTE_INPUT_HISTORY)
        val actualGroupKey = actualNotification.group
        val expectedSound = expectedExtra.sounds ?: Uri.EMPTY

        assertNotificationPriority(expectedPriority, actualNotification)
        assertEquals(expectedExtra.category, actualNotification.category)
        assertEquals(expectedExtra.visibility, actualNotification.visibility)
        assertEquals(expectedExtra.ongoing, actualOngoing)
        assertEquals(expectedExtra.color, actualNotification.color)
        assertEquals(expectedExtra.timestampWhen, actualNotification.`when`)
        assertEquals(expectedExtra.deleteIntent, actualNotification.deleteIntent)
        assertEquals(expectedExtra.fullScreenIntent?.first, actualNotification.fullScreenIntent)
        assertEquals(expectedExtra.onlyAlertOnce, actualOnlyAlertOnce)
        assertEquals(expectedExtra.showWhen, actualShowWhen)
        assertEquals(expectedExtra.useChronometer, actualUsesChronometer)
        assertEquals(expectedExtra.badgeNumber, actualBadgeNumber)
        assertNotificationSound(expectedSound, actualNotification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actualBadgeIconType = actualNotification.badgeIconType
            val actualShortcutId = actualNotification.shortcutId

            assertEquals(expectedExtra.badgeIconType, actualBadgeIconType)
            assertEquals(expectedExtra.shortCutId, actualShortcutId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val actualSystemGeneratedActions =
                actualNotification.allowSystemGeneratedContextualActions
            assertEquals(
                expectedExtra.allowSystemGeneratedContextualActions,
                actualSystemGeneratedActions
            )
        }
        assertEquals(expectedExtra.remoteInputHistory?.size, actualRemoteInputHistory?.size)
        assertEquals(expectedExtra.groupKey, actualGroupKey)
    }

    @Test
    fun useProgress_shouldNotApply() = runTest {
        val expectedData = TestDataProvider.groupMessageData(context, "contact_015")
        val actualNotificationIds = SimpleNotify.with(context)
            .asGroupMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                conversationTitle = expectedData.conversationTitle
                messages = expectedData.messages
                useHistoricMessage = false
            }.progress {
                currentValue = 50
                indeterminate = true
            }.show()
        val actualNotificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    @Test
    fun useProgress_whenIsHide_shouldNotApply() = runTest {
        val expectedData = TestDataProvider.groupMessageData(context, "contact_015")
        val actualNotificationIds = SimpleNotify.with(context)
            .asGroupMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                conversationTitle = expectedData.conversationTitle
                messages = expectedData.messages
                useHistoricMessage = false
            }.progress {
                hide = true
            }.show()
        val actualNotificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    @Test
    fun useChannel_shouldApply() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.groupMessageData(context, "contact_015")
        val actualNotificationIds = SimpleNotify.with(context)
            .asGroupMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                conversationTitle = expectedData.conversationTitle
                messages = expectedData.messages
                useHistoricMessage = false
            }
            .useChannel(expectedChannelId)
            .show()
        val actualNotificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.groupMessageData(context, "contact_015")
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationIds = SimpleNotify.with(context)
            .asGroupMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                conversationTitle = expectedData.conversationTitle
                messages = expectedData.messages
                useHistoricMessage = false
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
        val actualNotificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(actualNotificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    private fun assertCommonData(
        expectedData: Data.GroupMessageData,
        actualNotification: Notification
    ) {
        val actualExtras = actualNotification.extras
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualConversationTitle =
            actualExtras.getString(NotificationCompat.EXTRA_CONVERSATION_TITLE)
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
            actualNotification
        )
        val actualLastMsg = actualStyle?.messages?.last()

        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertEquals(expectedData.you.name, actualUserName)
        assertEquals(expectedData.conversationTitle, actualConversationTitle)
        assertTrue(actualIsGroupConversation)
        assertNotificationMessages(expectedData.messages, actualNotification)
        assertEquals(expectedData.messages.last().mimeData?.first, actualLastMsg?.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData?.second == actualLastMsg?.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualNotification.category)
    }

}