package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.toBitmap
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssignContentTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var assignContent: AssignContent
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext()
        builder = NotificationCompat.Builder(appContext, "test_channel")
        assignContent = AssignContent
    }

    @Test
    fun applyData_asBasicData_apply() {
        val expectedData = TestDataProvider.basicData()
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyData_asBigTextData_apply() {
        val expectedData = TestDataProvider.bigTextData()
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualBigText = actualExtras.getString(NotificationCompat.EXTRA_BIG_TEXT)
        val actualSummaryText = actualExtras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.bigText, actualBigText)
        assertEquals(expectedData.summaryText, actualSummaryText)
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyData_asInboxData_apply() {
        val expectedData = TestDataProvider.inboxData()
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualTextLines = actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.lines.size, actualTextLines!!.size)
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyData_asBigPictureData_apply() {
        val expectedData = TestDataProvider.bigPictureData()
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualPicture = actualExtras.getParcelable<Bitmap>(NotificationCompat.EXTRA_PICTURE)
        val actualSummaryText = actualExtras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.summaryText, actualSummaryText)
        assertTrue(expectedData.image!!.sameAs(actualPicture))
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyData_asDuoMessageData_apply() {
        val expectedShortCutId = "contact_123"
        val expectedData = TestDataProvider.duoMessageData(
            context = ApplicationProvider.getApplicationContext(),
            shortcutId = expectedShortCutId
        )
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualMessages = actualExtras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualStyle =
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        val actualLastMsg = actualStyle!!.messages.last()

        assertEquals(expectedData.you.name, actualUserName)
        assertFalse(actualIsGroupConversation)
        assertEquals(expectedData.messages.size, actualMessages!!.size)
        assertEquals(expectedData.messages.last().mimeData!!.first, actualLastMsg.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData!!.second == actualLastMsg.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, notification.category)
        assertEquals(expectedData.contact.name, actualNamePersonAdded)
        checkCommonData(expectedData, notification)
        //Notification bubbles are available from API 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val expectedBubble = expectedData.bubble!!
            val actualBubble = notification.bubbleMetadata!!
            assertEquals(expectedBubble.desiredHeight, actualBubble.desiredHeight)
            assertEquals(expectedBubble.autoExpandBubble, actualBubble.autoExpandBubble)
            assertEquals(
                expectedBubble.isNotificationSuppressed,
                actualBubble.isNotificationSuppressed
            )
        }
        //Notification ShortCut are available from API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedShortCutId, notification.shortcutId)
        }
    }

    @Test
    fun applyData_asGroupMessageData_apply() {
        val expectedShortCutId = "contact_123"
        val expectedData = TestDataProvider.groupMessageData(
            context = ApplicationProvider.getApplicationContext(),
            shortcutId = expectedShortCutId
        )
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualMessages = actualExtras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualConversationTitle =
            actualExtras.getString(NotificationCompat.EXTRA_CONVERSATION_TITLE)
        val actualStyle =
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        val actualLastMsg = actualStyle!!.messages.last()

        assertEquals(expectedData.you.name, actualUserName)
        assertTrue(actualIsGroupConversation)
        assertEquals(expectedData.conversationTitle, actualConversationTitle)
        assertEquals(expectedData.messages.size, actualMessages!!.size)
        assertEquals(expectedData.messages.last().mimeData!!.first, actualLastMsg.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData!!.second == actualLastMsg.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, notification.category)
        checkCommonData(expectedData, notification)
        //Notification bubbles are available from API 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val expectedBubble = expectedData.bubble!!
            val actualBubble = notification.bubbleMetadata!!
            assertEquals(expectedBubble.desiredHeight, actualBubble.desiredHeight)
            assertEquals(expectedBubble.autoExpandBubble, actualBubble.autoExpandBubble)
            assertEquals(
                expectedBubble.isNotificationSuppressed,
                actualBubble.isNotificationSuppressed
            )
        }
        //Notification ShortCut are available from API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedShortCutId, notification.shortcutId)
        }
    }

    @Test
    fun applyData_asCustomDesignData_apply() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedSmallRemoteView = expectedData.smallRemoteViews
        val expectedLargeRemoteView = expectedData.largeRemoteViews
        assignContent.applyData(appContext,expectedData, builder)
        val notification = builder.build()
        val actualSmallRemoteView = notification.contentView
        val actualLargeRemoteView = notification.bigContentView

        assertEquals(expectedSmallRemoteView.invoke()!!.layoutId, actualSmallRemoteView.layoutId)
        assertEquals(expectedLargeRemoteView.invoke()!!.layoutId, actualLargeRemoteView.layoutId)
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyExtras_apply() {
        val expectedExtraData = TestDataProvider.extraData()
        assignContent.applyExtras(expectedExtraData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualOngoing = notification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        val actualOnlyAlertOnce = notification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        val actualSubText = actualExtras.getString(NotificationCompat.EXTRA_SUB_TEXT)
        val actualShowWhen = actualExtras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        val actualUsesChronometer = NotificationCompat.getUsesChronometer(notification)
        val actualBadgeNumber = notification.number
        val actualRemoteInputHistory =
            actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_REMOTE_INPUT_HISTORY)
        val actualGroupKey = notification.group

        assertEquals(expectedExtraData.category, notification.category)
        assertEquals(expectedExtraData.visibility, notification.visibility)
        assertEquals(expectedExtraData.ongoing, actualOngoing)
        assertEquals(expectedExtraData.color, notification.color)
        assertEquals(expectedExtraData.timestampWhen, notification.`when`)
        assertEquals(expectedExtraData.deleteIntent, notification.deleteIntent)
        assertEquals(expectedExtraData.fullScreenIntent?.first, notification.fullScreenIntent)
        assertEquals(expectedExtraData.onlyAlertOnce, actualOnlyAlertOnce)
        assertEquals(expectedExtraData.subText, actualSubText)
        assertEquals(expectedExtraData.showWhen, actualShowWhen)
        assertEquals(expectedExtraData.useChronometer, actualUsesChronometer)
        assertEquals(expectedExtraData.badgeNumber, actualBadgeNumber)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actualBadgeIconType = notification.badgeIconType
            val actualShortcutId = notification.shortcutId

            assertEquals(expectedExtraData.badgeIconType, actualBadgeIconType)
            assertEquals(expectedExtraData.shortCutId, actualShortcutId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val actualSystemGeneratedActions = notification.allowSystemGeneratedContextualActions
            assertEquals(
                expectedExtraData.allowSystemGeneratedContextualActions,
                actualSystemGeneratedActions
            )
        }
        assertEquals(expectedExtraData.remoteInputHistory?.size, actualRemoteInputHistory?.size)
        assertEquals(expectedExtraData.groupKey, actualGroupKey)
    }

    @Test
    fun applyAction_apply() {
        val expectedBasicAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        assignContent.applyAction(expectedBasicAction, builder)
        assignContent.applyAction(expectedReplyAction, builder)
        val notification = builder.build()
        val actualActions = notification.actions

        assertEquals(2, actualActions.size)
        assertEquals(expectedBasicAction.icon, actualActions[0].icon)
        assertEquals(expectedBasicAction.label, actualActions[0].title)
        assertEquals(expectedBasicAction.pending, actualActions[0].actionIntent)
        assertEquals(expectedReplyAction.icon, actualActions[1].icon)
        assertEquals(expectedReplyAction.label, actualActions[1].title)
        assertEquals(expectedReplyAction.replyPending, actualActions[1].actionIntent)
        assertEquals(
            expectedReplyAction.allowGeneratedReplies,
            actualActions[1].allowGeneratedReplies
        )
        assertNotNull(actualActions[1].remoteInputs[0])
    }

    @Test
    fun applyProgress_whenIsNotHide_apply() {
        val progressData = TestDataProvider.progressData(false)
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun applyProgress_whenIsHide_apply() {
        val progressData = TestDataProvider.progressData(true)
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    private fun checkCommonData(expectedData: Data, actualNotification: Notification) {
        val extras = actualNotification.extras
        val expectedLargeIcon = extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        val actualAutoCancel = actualNotification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0

        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertTrue(expectedData.largeIcon!!.sameAs(expectedLargeIcon!!.toBitmap()))
        assertEquals(expectedData.contentIntent, actualNotification.contentIntent)
        assertEquals(expectedData.autoCancel, actualAutoCancel)
        assertEquals(expectedData.priority, actualNotification.priority)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedData.timeoutAfter, actualNotification.timeoutAfter)
        }
    }

}