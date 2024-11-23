package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.app.PendingIntent
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyFilterTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notifyFilter: NotifyFilter
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext()
        builder = NotificationCompat.Builder(appContext, "test_channel")
        notifyFilter = NotifyFilter
    }

    @Test
    fun applyData_asBasicData_apply() {
        val expectedData = TestDataProvider.basicData()
        notifyFilter.applyData(appContext, expectedData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        checkCommonData(expectedData, notification)
    }

    @Test
    fun applyData_asBigTextData_apply() {
        val expectedData = TestDataProvider.bigTextData()
        notifyFilter.applyData(appContext, expectedData, builder)
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
        notifyFilter.applyData(appContext, expectedData, builder)
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
        notifyFilter.applyData(appContext, expectedData, builder)
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
        notifyFilter.applyData(appContext, expectedData, builder)
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
        notifyFilter.applyData(appContext, expectedData, builder)
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
    fun applyData_asCallData_apply() {
        val expectedData = TestDataProvider.callData()
        val expectedCallType = when (expectedData.type) {
            "incoming" -> NotificationCompat.CallStyle.CALL_TYPE_INCOMING
            "ongoing" -> NotificationCompat.CallStyle.CALL_TYPE_ONGOING
            "screening" -> NotificationCompat.CallStyle.CALL_TYPE_SCREENING
            else -> NotificationCompat.CallStyle.CALL_TYPE_INCOMING
        }
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualStyle = actualExtras.getString(NotificationCompat.EXTRA_COMPAT_TEMPLATE)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualCallType = actualExtras.getInt(NotificationCompat.EXTRA_CALL_TYPE)
        val actualAnswer : PendingIntent? = when (expectedData.type) {
            "incoming" -> actualExtras.getParcelable(NotificationCompat.EXTRA_ANSWER_INTENT)
            "ongoing" -> null
            "screening" -> actualExtras.getParcelable(NotificationCompat.EXTRA_ANSWER_INTENT)
            else -> null
        }
        val actualDeclineHangup: PendingIntent? = when (expectedData.type) {
            "incoming" -> actualExtras.getParcelable(NotificationCompat.EXTRA_DECLINE_INTENT)
            "ongoing" -> actualExtras.getParcelable(NotificationCompat.EXTRA_HANG_UP_INTENT)
            "screening" -> actualExtras.getParcelable(NotificationCompat.EXTRA_HANG_UP_INTENT)
            else -> null
        }

        assertEquals(expectedData.caller?.name, actualNamePersonAdded)
        assertTrue(actualStyle?.contains("CallStyle") ?: false)
        assertEquals(expectedCallType, actualCallType)
        assertEquals(expectedData.answer, actualAnswer)
        assertEquals(expectedData.declineOrHangup, actualDeclineHangup)
        checkCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asCustomDesignData_apply() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedSmallRemoteView = expectedData.smallRemoteViews
        val expectedLargeRemoteView = expectedData.largeRemoteViews
        notifyFilter.applyData(appContext, expectedData, builder)
        val notification = builder.build()
        val actualSmallRemoteView = notification.contentView
        val actualLargeRemoteView = notification.bigContentView

        assertEquals(expectedSmallRemoteView.invoke()!!.layoutId, actualSmallRemoteView.layoutId)
        assertEquals(expectedLargeRemoteView.invoke()!!.layoutId, actualLargeRemoteView.layoutId)
        checkCommonData(expectedData, notification)
    }

    private fun checkCommonData(expectedData: Data, actualNotification: Notification) {
        val extras = actualNotification.extras
        val expectedLargeIcon = extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        val actualAutoCancel = actualNotification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0

        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertTrue(expectedData.largeIcon!!.sameAs(expectedLargeIcon!!.toBitmap()))
        assertEquals(expectedData.contentIntent, actualNotification.contentIntent)
        assertEquals(expectedData.autoCancel, actualAutoCancel)
//        assertEquals(expectedData.priority, actualNotification.priority)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedData.timeoutAfter, actualNotification.timeoutAfter)
        }
    }
}