package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.test.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssignContentTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var assignContent: AssignContent

    @Before
    fun setUp() {
        builder = NotificationCompat
            .Builder(ApplicationProvider.getApplicationContext(), "test_channel")
        assignContent = AssignContent
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asBasicData_apply() {
        val basicData = TestDataProvider.basicData()
        assignContent.applyData(basicData, builder)
        val notification = builder.build()

        assertEquals(basicData.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(basicData.text, notification.extras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(basicData.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(basicData.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(basicData.priority, notification.priority)
        assertEquals(basicData.contentIntent, notification.contentIntent)
        assertEquals(
            basicData.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asBigTextData_apply() {
        val data = TestDataProvider.bigTextData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(data.bigText, notification.extras.getString(NotificationCompat.EXTRA_BIG_TEXT))
        assertEquals(
            data.summaryText,
            notification.extras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)
        )
        assertEquals(data.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(
            data.text,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.contentIntent, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asInboxData_apply() {
        val data = TestDataProvider.inboxData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(data.text, notification.extras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(
            data.lines.size,
            notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.size
        )
        assertEquals(data.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.contentIntent, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asBigPictureData_apply() {
        val data = TestDataProvider.bigPictureData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(
            data.text,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(
            data.summaryText,
            notification.extras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)
        )
        val picture = notification.extras.getParcelable<Bitmap>(NotificationCompat.EXTRA_PICTURE)
        assertTrue(data.image!!.sameAs(picture))
        assertEquals(data.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.contentIntent, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asMessageData_apply() {
        val data = TestDataProvider.messageData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(
            data.conversationTitle,
            notification.extras.getString(NotificationCompat.EXTRA_CONVERSATION_TITLE)
        )
        assertEquals(
            data.user.name,
            notification.extras.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME)
        )
        val messages = notification.extras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)
        assertEquals(data.messages.size, messages?.size)
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.contentIntent, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asCustomDesignData_apply() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val expectedData = TestDataProvider.customDesignData(context)
        assignContent.applyData(expectedData, builder)
        val notification = builder.build()

        assertNotNull(notification.contentView)
        assertEquals(
            expectedData.smallRemoteViews.invoke()?.layoutId,
            notification.contentView.layoutId
        )
        assertNotNull(notification.bigContentView)
        assertEquals(
            expectedData.largeRemoteViews.invoke()?.layoutId,
            notification.bigContentView.layoutId
        )
        assertEquals(expectedData.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(expectedData.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(expectedData.contentIntent, notification.contentIntent)
        assertEquals(
            expectedData.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
        assertEquals(expectedData.priority, notification.priority)
    }

    @Test
    fun applyExtras_apply() {
        val extraData = TestDataProvider.extraData()
        assignContent.applyExtras(extraData, builder)
        val notification = builder.build()

        assertEquals(extraData.category, notification.category)
        assertEquals(extraData.visibility, notification.visibility)
        assertEquals(extraData.color, notification.color)
        assertEquals(
            extraData.ongoing,
            notification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        )
        assertEquals(extraData.timestampWhen, notification.`when`)
        assertEquals(extraData.deleteIntent, notification.deleteIntent)
        assertEquals(extraData.fullScreenIntent?.first, notification.fullScreenIntent)
        assertEquals(
            extraData.onlyAlertOnce,
            notification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        )
        assertEquals(
            extraData.subText,
            notification.extras.getString(NotificationCompat.EXTRA_SUB_TEXT)
        )
        assertEquals(
            extraData.showWhen,
            notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        )
        assertEquals(extraData.useChronometer, NotificationCompat.getUsesChronometer(notification))
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyAction_apply() {
        val basicActionData = ActionData.BasicAction(
            icon = R.drawable.test_ic_mail_24,
            label = "Any Label",
            pending = TestDataProvider.pendingIntent()
        )
        val replyAction = ActionData.ReplyAction(
            icon = R.drawable.test_ic_archive_24,
            label = "Any Label",
            replyPending = TestDataProvider.pendingIntent(),
            remote = RemoteInput.Builder("any_key").build()
        )
        assignContent.applyAction(basicActionData, builder)
        assignContent.applyAction(replyAction, builder)
        val notification = builder.build()

        assertEquals(2, notification.actions.size)
        assertEquals(basicActionData.icon, notification.actions[0].icon)
        assertEquals(basicActionData.label, notification.actions[0].title)
        assertEquals(basicActionData.pending, notification.actions[0].actionIntent)
        assertEquals(replyAction.icon, notification.actions[1].icon)
        assertEquals(replyAction.label, notification.actions[1].title)
        assertEquals(replyAction.replyPending, notification.actions[1].actionIntent)
        assertNotNull(notification.actions[1].remoteInputs[0])
    }

    @Test
    fun applyProgress_whenIsNotHide_apply() {
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = false
        )
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()

        assertEquals(
            progressData.currentValue,
            notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

    @Test
    fun applyProgress_whenIsHide_apply() {
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = true
        )
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()

        assertEquals(0, notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS))
        assertFalse(notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE))
    }

    private fun Icon.toBitmap(): Bitmap? {
        return when (type) {
            Icon.TYPE_BITMAP -> (loadDrawable(ApplicationProvider.getApplicationContext()) as? BitmapDrawable)?.bitmap
            else -> null
        }
    }

}