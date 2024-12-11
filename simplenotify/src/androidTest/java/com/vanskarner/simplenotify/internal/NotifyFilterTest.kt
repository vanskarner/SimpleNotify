package com.vanskarner.simplenotify.internal

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertCommonData
import com.vanskarner.simplenotify.common.assertNotificationMessages
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.assertNotificationSound
import com.vanskarner.simplenotify.common.getCustomParcelable
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
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras


        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asBigTextData_apply() {
        val expectedData = TestDataProvider.bigTextData()
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualBigText = actualExtras.getString(NotificationCompat.EXTRA_BIG_TEXT)
        val actualSubText = actualExtras.getString(NotificationCompat.EXTRA_SUB_TEXT)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.bigText, actualBigText)
        assertEquals(expectedData.subText, actualSubText)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asInboxData_apply() {
        val expectedData = TestDataProvider.inboxData()
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualTextLines = actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.lines.size, actualTextLines?.size ?: 0)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asBigPictureData_apply() {
        val expectedData = TestDataProvider.bigPictureData()
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualPicture =
            actualExtras.getCustomParcelable(NotificationCompat.EXTRA_PICTURE, Bitmap::class.java)
        val actualSummaryText = actualExtras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)

        assertEquals(expectedData.title, actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.summaryText, actualSummaryText)
        assertTrue(expectedData.image?.sameAs(actualPicture) ?: false)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asDuoMessageData_apply() {
        val expectedShortCutId = "contact_123"
        val expectedData = TestDataProvider.duoMessageData(
            context = ApplicationProvider.getApplicationContext(),
            shortcutId = expectedShortCutId
        )
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualStyle =
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
                actualNotification
            )
        val actualLastMsg = actualStyle?.messages?.last()

        assertEquals(expectedData.you.name, actualUserName)
        assertEquals(expectedData.contact.name, actualNamePersonAdded)
        assertFalse(actualIsGroupConversation)
        assertNotificationMessages(expectedData.messages, actualNotification)
        assertEquals(expectedData.messages.last().mimeData?.first, actualLastMsg?.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData?.second == actualLastMsg?.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualNotification.category)
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
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
    fun applyData_asGroupMessageData_apply() {
        val expectedShortCutId = "contact_123"
        val expectedData = TestDataProvider.groupMessageData(
            context = ApplicationProvider.getApplicationContext(),
            shortcutId = expectedShortCutId
        )
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualUserName = actualExtras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        val actualIsGroupConversation =
            actualExtras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION)
        val actualConversationTitle =
            actualExtras.getString(NotificationCompat.EXTRA_CONVERSATION_TITLE)
        val actualStyle =
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
                actualNotification
            )
        val actualLastMsg = actualStyle!!.messages.last()

        assertEquals(expectedData.you.name, actualUserName)
        assertTrue(actualIsGroupConversation)
        assertEquals(expectedData.conversationTitle, actualConversationTitle)
        assertNotificationMessages(expectedData.messages, actualNotification)
        assertEquals(expectedData.messages.last().mimeData!!.first, actualLastMsg.dataMimeType)
        assertTrue(expectedData.messages.last().mimeData!!.second == actualLastMsg.dataUri)
        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualNotification.category)
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
        //Notification bubbles are available from API 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val expectedBubble = expectedData.bubble!!
            val actualBubble = actualNotification.bubbleMetadata!!
            assertEquals(expectedBubble.desiredHeight, actualBubble.desiredHeight)
            assertEquals(expectedBubble.autoExpandBubble, actualBubble.autoExpandBubble)
            assertEquals(
                expectedBubble.isNotificationSuppressed,
                actualBubble.isNotificationSuppressed
            )
        }
        //Notification ShortCut are available from API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(expectedShortCutId, actualNotification.shortcutId)
        }
    }

    @Test
    fun applyData_asCallData_apply() {
        val expectedData = TestDataProvider.callData(appContext)
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val expectedCallType = mapOf(
            "incoming" to NotificationCompat.CallStyle.CALL_TYPE_INCOMING,
            "ongoing" to NotificationCompat.CallStyle.CALL_TYPE_ONGOING,
            "screening" to NotificationCompat.CallStyle.CALL_TYPE_SCREENING
        ).getOrDefault(expectedData.type, NotificationCompat.CallStyle.CALL_TYPE_INCOMING)
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()
        val actualExtras = actualNotification.extras
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualStyle = actualExtras.getString(NotificationCompat.EXTRA_COMPAT_TEMPLATE)
        val actualCallType = actualExtras.getInt(NotificationCompat.EXTRA_CALL_TYPE)
        val actualAnswer = if (expectedData.type in listOf("incoming", "screening"))
            actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_ANSWER_INTENT,
                PendingIntent::class.java
            )
        else null
        val actualDeclineHangup = when (expectedData.type) {
            "incoming" -> actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_DECLINE_INTENT,
                PendingIntent::class.java
            )

            "ongoing", "screening" -> actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_HANG_UP_INTENT,
                PendingIntent::class.java
            )

            else -> null
        }

        assertEquals(expectedData.caller?.name, actualNamePersonAdded)
        assertTrue(actualStyle?.contains("CallStyle") ?: false)
        assertEquals(expectedCallType, actualCallType)
        assertEquals(expectedData.answer, actualAnswer)
        assertEquals(expectedData.declineOrHangup, actualDeclineHangup)
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun applyData_asCustomDesignData_apply() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val expectedSmallRemoteView = expectedData.smallRemoteViews
        val expectedLargeRemoteView = expectedData.largeRemoteViews
        notifyFilter.applyData(appContext, expectedData, builder)
        val actualNotification = builder.build()

        @Suppress("DEPRECATION") //Deprecated in API level 24, no exchange option
        val actualSmallRemoteView = actualNotification.contentView

        @Suppress("DEPRECATION") //Deprecated in API level 24, no exchange option
        val actualLargeRemoteView = actualNotification.bigContentView

        assertEquals(expectedSmallRemoteView.invoke()!!.layoutId, actualSmallRemoteView.layoutId)
        assertEquals(expectedLargeRemoteView.invoke()!!.layoutId, actualLargeRemoteView.layoutId)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertNotificationSound(expectedSound, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

}