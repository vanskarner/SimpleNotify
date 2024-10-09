package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.simplenotify.R
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
        val basicData = createBasicData()
        assignContent.applyData(basicData, builder)
        val notification = builder.build()

        assertEquals(basicData.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(basicData.text, notification.extras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(basicData.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(basicData.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(basicData.priority, notification.priority)
        assertEquals(basicData.pending, notification.contentIntent)
        assertEquals(
            basicData.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asBigTextData_apply() {
        val data = createBigTextData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(data.bigText, notification.extras.getString(NotificationCompat.EXTRA_BIG_TEXT))
        assertEquals(
            data.summaryText,
            notification.extras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)
        )
        assertEquals(data.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(
            data.collapsedText,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.pending, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asInboxData_apply() {
        val data = createInboxData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(data.summaryText, notification.extras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(
            data.lines.size,
            notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.size
        )
        assertEquals(data.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(data.smallIcon, notification.smallIcon.resId)
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(data.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        assertEquals(data.priority, notification.priority)
        assertEquals(data.pending, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asBigPictureData_apply() {
        val data = createBigPictureData()
        assignContent.applyData(data, builder)
        val notification = builder.build()

        assertEquals(
            data.collapsedText,
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
        assertEquals(data.pending, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asMessageData_apply() {
        val data = createMessageData()
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
        assertEquals(data.pending, notification.contentIntent)
        assertEquals(
            data.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyData_asCustomDesignData_apply() {
        val expectedData = createCustomDesignData(ApplicationProvider.getApplicationContext())
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
        assertEquals(expectedData.pending, notification.contentIntent)
        assertEquals(
            expectedData.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
        assertEquals(expectedData.priority, notification.priority)
    }

    @Test
    fun applyExtras_apply() {
        val extraData = createExtraData()
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
            icon = R.drawable.baseline_notifications_24,
            label = "Any Label",
            pending = createPendingIntent()
        )
        val replyAction = ActionData.ReplyAction(
            icon = R.drawable.baseline_notifications_24,
            label = "Any Label",
            replyPending = createPendingIntent(),
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

    private fun createExtraData() = ExtraData(
        category = NotificationCompat.CATEGORY_MESSAGE,
        visibility = NotificationCompat.VISIBILITY_PRIVATE,
//        vibrationPattern = longArrayOf(0, 500, 1000, 500),
//        lights = Triple(Color.GREEN, 1000, 1000),
        ongoing = true,
        color = Color.GRAY,
//        timeoutAfter = 1000,
//        badgeIconType = 12,
        timestampWhen = 1500,
        deleteIntent = createPendingIntent(),
        fullScreenIntent = Pair(createPendingIntent()!!, true),
        onlyAlertOnce = true,
        subText = "Any SubText",
        showWhen = true,
        useChronometer = true
    )

    private fun createBasicData(): Data.BasicData {
        val pendingIntent = createPendingIntent()
        val basicData = Data.BasicData().apply {
            title = "Any title"
            text = "Any text"
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_DEFAULT
            pending = pendingIntent
            autoCancel = true
        }
        return basicData
    }

    private fun createBigTextData(): Data.BigTextData {
        val pendingIntent = createPendingIntent()
        val data = Data.BigTextData().apply {
            title = "Any title"
            bigText = "Any text"
            collapsedText = "Any collapsedText"
            summaryText = "Any summary"
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_DEFAULT
            pending = pendingIntent
            autoCancel = true
        }
        return data
    }

    private fun createInboxData(): Data.InboxData {
        val pendingIntent = createPendingIntent()
        val data = Data.InboxData().apply {
            title = "Any title"
            summaryText = "Any text"
            lines = arrayListOf("Any line 1", "Any line 2", "Any line 3")
            summaryText = "Any summary"
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_HIGH
            pending = pendingIntent
            autoCancel = true
        }
        return data
    }

    private fun createBigPictureData(): Data.BigPictureData {
        val pendingIntent = createPendingIntent()
        val data = Data.BigPictureData().apply {
            title = "Any title"
            collapsedText = "Any collapsedText"
            summaryText = "Any text"
            image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_HIGH
            pending = pendingIntent
            autoCancel = true
        }
        return data
    }

    private fun createMessageData(): Data.MessageData {
        val pendingIntent = createPendingIntent()
        val data = Data.MessageData().apply {
            conversationTitle = "Any conversationTitle"
            user = Person.Builder().setName("Albert").build()
            messages = arrayListOf(
                NotificationCompat.MessagingStyle.Message(
                    "Any Message 1",
                    System.currentTimeMillis() - (5 * 60 * 1000),
                    Person.Builder().setName("Chris").build()
                ),
                NotificationCompat.MessagingStyle.Message(
                    "Any Message 2",
                    System.currentTimeMillis() - (10 * 60 * 1000),
                    Person.Builder().setName("Max").build()
                )
            )
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_HIGH
            pending = pendingIntent
            autoCancel = true
        }
        return data
    }

    private fun createCustomDesignData(context: Context): Data.CustomDesignData {
        val data = Data.CustomDesignData().apply {
            hasStyle = false
            smallRemoteViews = {
                val remoteViews = RemoteViews(
                    context.packageName,
                    com.vanskarner.simplenotify.test.R.layout.small_notification
                )
                remoteViews.setTextViewText(
                    com.vanskarner.simplenotify.test.R.id.notification_title,
                    "Small title"
                )
                remoteViews
            }
            largeRemoteViews = {
                val remoteViews = RemoteViews(
                    context.packageName,
                    com.vanskarner.simplenotify.test.R.layout.large_notification
                )
                remoteViews.setTextViewText(
                    com.vanskarner.simplenotify.test.R.id.notification_title,
                    "Large title"
                )
                remoteViews
            }
            smallIcon = R.drawable.baseline_notifications_24
            pending = createPendingIntent()
            autoCancel = true
            priority = NotificationCompat.PRIORITY_HIGH
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
        return data
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent()
        val pendingIntent = PendingIntent.getBroadcast(
            ApplicationProvider.getApplicationContext(),
            123,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    private fun Icon.toBitmap(): Bitmap? {
        return when (type) {
            Icon.TYPE_BITMAP -> (loadDrawable(ApplicationProvider.getApplicationContext()) as? BitmapDrawable)?.bitmap
            else -> null
        }
    }

}