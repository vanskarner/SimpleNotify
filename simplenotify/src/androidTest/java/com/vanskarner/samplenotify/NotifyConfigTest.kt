package com.vanskarner.samplenotify

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import com.vanskarner.simplenotify.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotifyConfigTest {
    private lateinit var notifyConfig: NotifyConfig

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        notifyConfig = NotifyConfig(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun asBasic_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        notifyConfig.asBasic {
            id = 123
            smallIcon = R.drawable.baseline_notifications_24
            title = "Test Title"
            text = "Test Text"
            largeIcon = expectedLargeIcon
            priority = NotificationCompat.PRIORITY_HIGH
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBasicData = dataField.get(notifyConfig) as Data.BasicData

        assertEquals(123, actualBasicData.id)
        assertEquals(R.drawable.baseline_notifications_24, actualBasicData.smallIcon)
        assertEquals("Test Title", actualBasicData.title)
        assertEquals("Test Text", actualBasicData.text)
        assertTrue(actualBasicData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(NotificationCompat.PRIORITY_HIGH, actualBasicData.priority)
        assertEquals(expectedPendingIntent, actualBasicData.contentIntent)
        assertEquals(false, actualBasicData.autoCancel)
    }

    @Test
    fun asBigText_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        notifyConfig.asBigText {
            id = 123
            smallIcon = R.drawable.baseline_notifications_24
            title = "Test Title"
            bigText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            text = "Contrary to popular belief"
            summaryText = "Lorem Ipsum is not simply random text"
            largeIcon = expectedLargeIcon
            priority = NotificationCompat.PRIORITY_HIGH
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBigTextData = dataField.get(notifyConfig) as Data.BigTextData

        assertEquals(123, actualBigTextData.id)
        assertEquals(R.drawable.baseline_notifications_24, actualBigTextData.smallIcon)
        assertEquals("Test Title", actualBigTextData.title)
        assertEquals(
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
            actualBigTextData.bigText
        )
        assertEquals("Contrary to popular belief", actualBigTextData.text)
        assertEquals("Lorem Ipsum is not simply random text", actualBigTextData.summaryText)
        assertTrue(actualBigTextData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(NotificationCompat.PRIORITY_HIGH, actualBigTextData.priority)
        assertEquals(expectedPendingIntent, actualBigTextData.contentIntent)
        assertEquals(false, actualBigTextData.autoCancel)
    }

    @Test
    fun asInbox_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedLines = arrayListOf("item 1", "Item 2", "Item 3")
        notifyConfig.asInbox {
            id = 123
            smallIcon = R.drawable.baseline_notifications_24
            title = "Test Title"
            text = "Lorem Ipsum is not simply random text"
            lines = expectedLines
            largeIcon = expectedLargeIcon
            priority = NotificationCompat.PRIORITY_HIGH
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualInboxData = dataField.get(notifyConfig) as Data.InboxData

        assertEquals(123, actualInboxData.id)
        assertEquals(R.drawable.baseline_notifications_24, actualInboxData.smallIcon)
        assertEquals("Test Title", actualInboxData.title)
        assertEquals("Lorem Ipsum is not simply random text", actualInboxData.text)
        assertEquals(expectedLines, actualInboxData.lines)
        assertTrue(actualInboxData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(NotificationCompat.PRIORITY_HIGH, actualInboxData.priority)
        assertEquals(expectedPendingIntent, actualInboxData.contentIntent)
        assertEquals(false, actualInboxData.autoCancel)
    }

    @Test
    fun asBigPicture_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedImage = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        notifyConfig.asBigPicture {
            id = 123
            smallIcon = R.drawable.baseline_notifications_24
            title = "Test Title"
            text = "Contrary to popular belief"
            summaryText = "Lorem Ipsum is not simply random text"
            image = expectedImage
            largeIcon = expectedLargeIcon
            priority = NotificationCompat.PRIORITY_HIGH
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBigPictureData = dataField.get(notifyConfig) as Data.BigPictureData

        assertEquals(123, actualBigPictureData.id)
        assertEquals(R.drawable.baseline_notifications_24, actualBigPictureData.smallIcon)
        assertEquals("Test Title", actualBigPictureData.title)
        assertEquals("Contrary to popular belief", actualBigPictureData.text)
        assertEquals("Lorem Ipsum is not simply random text", actualBigPictureData.summaryText)
        assertTrue(actualBigPictureData.image?.sameAs(expectedImage)!!)
        assertTrue(actualBigPictureData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(NotificationCompat.PRIORITY_HIGH, actualBigPictureData.priority)
        assertEquals(expectedPendingIntent, actualBigPictureData.contentIntent)
        assertEquals(false, actualBigPictureData.autoCancel)
    }

    @Test
    fun asMessaging_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedUser = Person.Builder().build()
        val expectedMessage = arrayListOf(
            Message("Any Text 1", 1000, Person.Builder().build()),
            Message("Any Text 2", 2000, Person.Builder().build())
        )
        notifyConfig.asMessaging {
            id = 123
            smallIcon = R.drawable.baseline_notifications_24
            conversationTitle = "Contrary to popular belief"
            user = expectedUser
            messages = expectedMessage
            largeIcon = expectedLargeIcon
            priority = NotificationCompat.PRIORITY_HIGH
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualMessageData = dataField.get(notifyConfig) as Data.MessageData

        assertEquals(123, actualMessageData.id)
        assertEquals(R.drawable.baseline_notifications_24, actualMessageData.smallIcon)
        assertEquals("Contrary to popular belief", actualMessageData.conversationTitle)
        assertEquals(expectedUser, actualMessageData.user)
        assertEquals(expectedMessage, actualMessageData.messages)
        assertTrue(actualMessageData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(NotificationCompat.PRIORITY_HIGH, actualMessageData.priority)
        assertEquals(expectedPendingIntent, actualMessageData.contentIntent)
        assertEquals(false, actualMessageData.autoCancel)
    }

    @Test
    fun asCustomDesign_shouldSetData() {
        val context: Context = ApplicationProvider.getApplicationContext()
        notifyConfig.asCustomDesign {
            hasStyle = true
            smallRemoteViews = {
                val remoteViews = RemoteViews(
                    context.packageName,
                    com.vanskarner.simplenotify.test.R.layout.small_notification
                )
                remoteViews.setTextViewText(com.vanskarner.simplenotify.test.R.id.notification_title, "Small title")
                remoteViews
            }
            largeRemoteViews = {
                val remoteViews = RemoteViews(
                    context.packageName,
                    com.vanskarner.simplenotify.test.R.layout.large_notification
                )
                remoteViews.setTextViewText(com.vanskarner.simplenotify.test.R.id.notification_title, "Large title")
                remoteViews
            }
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualCustomDesignData = dataField.get(notifyConfig) as Data.CustomDesignData

        assertEquals(R.drawable.baseline_notifications_24, actualCustomDesignData.smallIcon)
        assertEquals(com.vanskarner.simplenotify.test.R.layout.small_notification, actualCustomDesignData.smallRemoteViews.invoke()?.layoutId)
        assertEquals(com.vanskarner.simplenotify.test.R.layout.large_notification, actualCustomDesignData.largeRemoteViews.invoke()?.layoutId)
    }

    @Test
    fun extras_shouldSetData() {
        val expectedPendingIntent = createPendingIntent()
        val expectedFullScreenIntent = Pair(createPendingIntent(), true)
        val expectedTimestampWhen = System.currentTimeMillis()
        notifyConfig.extras {
            category = NotificationCompat.CATEGORY_MESSAGE
            visibility = NotificationCompat.VISIBILITY_PRIVATE
            ongoing = true
            color = Color.GREEN
            timestampWhen = expectedTimestampWhen
            deleteIntent = expectedPendingIntent
            fullScreenIntent = expectedFullScreenIntent
            onlyAlertOnce = true
            subText = "Some SubText"
            showWhen = true
            useChronometer = true
        }
        val extraField = notifyConfig.javaClass.getDeclaredField("extras")
        extraField.isAccessible = true
        val actualExtraData = extraField.get(notifyConfig) as ExtraData

        assertEquals(NotificationCompat.CATEGORY_MESSAGE, actualExtraData.category)
        assertEquals(NotificationCompat.VISIBILITY_PRIVATE, actualExtraData.visibility)
        assertEquals(true, actualExtraData.ongoing)
        assertEquals(Color.GREEN, actualExtraData.color)
        assertEquals(expectedTimestampWhen, actualExtraData.timestampWhen)
        assertEquals(expectedPendingIntent, actualExtraData.deleteIntent)
        assertEquals(expectedFullScreenIntent, actualExtraData.fullScreenIntent)
        assertEquals(true, actualExtraData.onlyAlertOnce)
        assertEquals("Some SubText", actualExtraData.subText)
        assertEquals(true, actualExtraData.showWhen)
        assertEquals(true, actualExtraData.useChronometer)
    }

    @Test
    fun progress_shouldSetData() {
        notifyConfig.progress {
            currentValue = 50
            indeterminate = true
            hide = true
        }
        val progressField = notifyConfig.javaClass.getDeclaredField("progressData")
        progressField.isAccessible = true
        val actualProgressData = progressField.get(notifyConfig) as ProgressData

        assertEquals(50, actualProgressData.currentValue)
        assertEquals(true, actualProgressData.indeterminate)
        assertEquals(true, actualProgressData.hide)
    }

    @Test
    fun useChannel_shouldSetChannel() {
        notifyConfig.useChannel("AnyChannelId")
        val channelIdField = notifyConfig.javaClass.getDeclaredField("channelId")
        channelIdField.isAccessible = true
        val actualChannelIdField = channelIdField.get(notifyConfig) as String

        assertEquals("AnyChannelId", actualChannelIdField)
    }

    @Test
    fun addAction_whenRegisteringMultipleItems_keepOnlyThreeItems() {
        val expectedPendingIntent = createPendingIntent()
        notifyConfig.addAction {
            icon = R.drawable.baseline_notifications_24
            label = "Action 1"
            pending = expectedPendingIntent
        }
            .addAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 2"
                pending = expectedPendingIntent
            }
            .addAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 3"
                pending = expectedPendingIntent
            }
            .addAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 4"
                pending = expectedPendingIntent
            }

        assertEquals(3, notifyConfig.actions.size)
    }

    @Test
    fun addReplyAction_whenRegisteringMultipleItems_keepOnlyThreeItems() {
        val expectedPendingIntent = createPendingIntent()
        val expectedRemote = RemoteInput.Builder("anyKey").build()
        notifyConfig.addReplyAction {
            icon = R.drawable.baseline_notifications_24
            label = "Action 1"
            replyPending = expectedPendingIntent
            remote = expectedRemote
        }
            .addReplyAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 2"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }
            .addReplyAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 3"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }
            .addReplyAction {
                icon = R.drawable.baseline_notifications_24
                label = "Action 4"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }

        assertEquals(3, notifyConfig.actions.size)
    }

    @Test
    fun show_whenDataIsNull_shouldNotShow() {
        val actualNotificationId = notifyConfig.show()
        val context: Context = ApplicationProvider.getApplicationContext()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        assertEquals(-1, actualNotificationId)
        assertEquals(0, notificationManager.activeNotifications.size)
    }

    @Test
    fun show_whenDataIsNotNull_shouldBeShown() = runTest {
        val expectedNotificationId = 123
        notifyConfig
            .asBasic {
                id = expectedNotificationId
                title = "Test Title"
                text = "Test Text"
            }
            .show()
        val context: Context = ApplicationProvider.getApplicationContext()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val statusBarNotify =
            notificationManager.activeNotifications.first { it.id == expectedNotificationId }

        assertNotNull(statusBarNotify.notification)
        assertEquals(
            "Test Title",
            statusBarNotify.notification.extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals(
            "Test Text",
            statusBarNotify.notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
    }

    private fun createPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            ApplicationProvider.getApplicationContext(),
            123,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}