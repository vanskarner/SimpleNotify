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
import com.vanskarner.simplenotify.SimpleNotify
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertBaseData
import com.vanskarner.simplenotify.common.assertExtraData
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationMessages
import com.vanskarner.simplenotify.common.assertNotificationPriority
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
class DuoMessagingNotifyTest {
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
    fun useDuoMessaging_shouldApply() = runTest {
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotifyConfig = SimpleNotify.with(context)
            .asDuoMessaging {
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                contact = expectedData.contact
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
        assertDuoMessaging(expectedData, actualNotification)
        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotificationGenerated)
        assertDuoMessaging(expectedData, actualNotificationGenerated)
    }

    @Test
    fun useDuoMessaging_withAllAttributes_shouldApply() {
        val expectedShortCutId = "contact_123"
        val expectedData = TestDataProvider.duoMessageData(context, expectedShortCutId)
        val actualNotification = SimpleNotify.with(context)
            .asDuoMessaging {
                subText = expectedData.subText
                largeIcon = expectedData.largeIcon
                contentIntent = expectedData.contentIntent
                autoCancel = expectedData.autoCancel
                timeoutAfter = expectedData.timeoutAfter
                smallIcon = expectedData.smallIcon
                you = expectedData.you
                contact = expectedData.contact
                messages = expectedData.messages
                bubble = expectedData.bubble
                shortcut = expectedData.shortcut
                addShortcutIfNotExists = expectedData.addShortcutIfNotExists
            }.generateBuilder()?.build() ?: Notification()

        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertBaseData(expectedData, actualNotification)
        assertDuoMessaging(expectedData, actualNotification)
        //Notification bubbles are available from API 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val expectedBubble = expectedData.bubble
            val actualBubble = actualNotification.bubbleMetadata

            assertEquals(expectedBubble?.desiredHeight, actualBubble?.desiredHeight)
            assertEquals(expectedBubble?.autoExpandBubble, actualBubble?.autoExpandBubble)
            assertEquals(
                expectedBubble?.isNotificationSuppressed,
                actualBubble?.isNotificationSuppressed
            )
        }
        //Notification ShortCut are available from API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedShortCutId, actualNotification.shortcutId)
        }
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asDuoMessaging {}
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

        assertExtraData(expectedExtra, actualNotification)
    }

    @Test
    fun useProgress_shouldNotApply() = runTest {
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val expectedProgress = TestDataProvider.progressData()
        val actualNotificationIds = SimpleNotify.with(context)
            .asDuoMessaging {
                you = expectedData.you
                contact = expectedData.contact
                messages = expectedData.messages
            }
            .progress {
                currentValue = expectedProgress.currentValue
                indeterminate = expectedProgress.indeterminate
            }
            .show()
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
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotificationIds = SimpleNotify.with(context)
            .asDuoMessaging {
                you = expectedData.you
                contact = expectedData.contact
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
    fun useChannel_shouldBeShown() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val actualNotificationIds = SimpleNotify.with(context)
            .asDuoMessaging {
                you = expectedData.you
                contact = expectedData.contact
                messages = expectedData.messages
            }.useChannel(expectedChannelId)
            .show()
        val notificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertDuoMessaging(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.duoMessageData(context, "contact_015")
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationPair = SimpleNotify.with(context)
            .asDuoMessaging {
                you = expectedData.you
                contact = expectedData.contact
                messages = expectedData.messages
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
            notifyManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_MESSAGING_CHANNEL_ID, actualNotification)
        assertDuoMessaging(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    private fun assertDuoMessaging(
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

        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualNotification.category)
        assertEquals(expectedData.you.name, actualUserName)
        assertEquals(expectedData.contact.name, actualNamePersonAdded)
        assertFalse(actualIsGroupConversation)
        assertNotificationMessages(expectedData.messages, actualNotification)
        assertEquals(expectedData.messages.last().mimeData?.first, actualLastMsg?.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData?.second == actualLastMsg?.dataUri)
    }

}