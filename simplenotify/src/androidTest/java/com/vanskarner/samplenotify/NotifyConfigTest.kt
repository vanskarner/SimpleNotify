package com.vanskarner.samplenotify

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitForNotification
import com.vanskarner.simplenotify.test.R
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyConfigTest {
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
    fun asBasic_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        notifyConfig.asBasic {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            title = "Test Title"
            text = "Test Text"
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBasicData = dataField.get(notifyConfig) as Data.BasicData

        assertEquals(123, actualBasicData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualBasicData.smallIcon)
        assertEquals("Test Title", actualBasicData.title)
        assertEquals("Test Text", actualBasicData.text)
        assertTrue(actualBasicData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualBasicData.contentIntent)
        assertEquals(false, actualBasicData.autoCancel)
    }

    @Test
    fun asBigText_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        notifyConfig.asBigText {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            title = "Test Title"
            bigText = "Lorem Ipsum is simply dummy text of ..."
            text = "Contrary to popular belief"
            summaryText = "Lorem Ipsum is not simply random text"
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBigTextData = dataField.get(notifyConfig) as Data.BigTextData

        assertEquals(123, actualBigTextData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualBigTextData.smallIcon)
        assertEquals("Test Title", actualBigTextData.title)
        assertEquals("Lorem Ipsum is simply dummy text of ...", actualBigTextData.bigText)
        assertEquals("Contrary to popular belief", actualBigTextData.text)
        assertEquals("Lorem Ipsum is not simply random text", actualBigTextData.summaryText)
        assertTrue(actualBigTextData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualBigTextData.contentIntent)
        assertEquals(false, actualBigTextData.autoCancel)
    }

    @Test
    fun asInbox_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedLines = arrayListOf("item 1", "Item 2", "Item 3")
        notifyConfig.asInbox {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            title = "Test Title"
            text = "Lorem Ipsum is not simply random text"
            lines = expectedLines
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualInboxData = dataField.get(notifyConfig) as Data.InboxData

        assertEquals(123, actualInboxData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualInboxData.smallIcon)
        assertEquals("Test Title", actualInboxData.title)
        assertEquals("Lorem Ipsum is not simply random text", actualInboxData.text)
        assertEquals(expectedLines, actualInboxData.lines)
        assertTrue(actualInboxData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualInboxData.contentIntent)
        assertEquals(false, actualInboxData.autoCancel)
    }

    @Test
    fun asBigPicture_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedImage = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        notifyConfig.asBigPicture {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            title = "Test Title"
            text = "Contrary to popular belief"
            summaryText = "Lorem Ipsum is not simply..."
            image = expectedImage
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualBigPictureData = dataField.get(notifyConfig) as Data.BigPictureData

        assertEquals(123, actualBigPictureData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualBigPictureData.smallIcon)
        assertEquals("Test Title", actualBigPictureData.title)
        assertEquals("Contrary to popular belief", actualBigPictureData.text)
        assertEquals("Lorem Ipsum is not simply...", actualBigPictureData.summaryText)
        assertTrue(actualBigPictureData.image?.sameAs(expectedImage)!!)
        assertTrue(actualBigPictureData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualBigPictureData.contentIntent)
        assertEquals(false, actualBigPictureData.autoCancel)
    }

    @Test
    fun asDuoMessaging_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedUser = Person.Builder().build()
        val expectedContact = Person.Builder().build()
        val expectedMessage = arrayListOf(
            NotifyMessaging.ContactMsg("Any Text 1", 1000),
            NotifyMessaging.YourMsg("Any Text 2", 2000)
        )
        notifyConfig.asDuoMessaging {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            you = expectedUser
            contact = expectedContact
            messages = expectedMessage
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualDuoMessageData = dataField.get(notifyConfig) as Data.DuoMessageData

        assertEquals(123, actualDuoMessageData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualDuoMessageData.smallIcon)
        assertEquals(expectedUser, actualDuoMessageData.you)
        assertEquals(expectedContact, actualDuoMessageData.contact)
        assertEquals(expectedMessage, actualDuoMessageData.messages)
        assertTrue(actualDuoMessageData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualDuoMessageData.contentIntent)
        assertEquals(false, actualDuoMessageData.autoCancel)
    }

    @Test
    fun asGroupMessaging_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedLargeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expectedUser = Person.Builder().setName("You").build()
        val expectedMessage = arrayListOf(
            NotifyMessaging.ContactMsg(
                "Any Text 1",
                System.currentTimeMillis(),
                Person.Builder().setName("Max").build()
            ),
            NotifyMessaging.ContactMsg(
                "Any Text 2",
                1000,
                Person.Builder().setName("Albert").build()
            ),
            NotifyMessaging.YourMsg(
                "Any Text 3",
                1000
            )
        )
        notifyConfig.asGroupMessaging {
            id = 123
            smallIcon = R.drawable.test_ic_notification_24
            conversationTitle = "Contrary to popular belief"
            you = expectedUser
            messages = expectedMessage
            largeIcon = expectedLargeIcon
            contentIntent = expectedPendingIntent
            autoCancel = false
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualDuoMessageData = dataField.get(notifyConfig) as Data.GroupMessageData

        assertEquals(123, actualDuoMessageData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualDuoMessageData.smallIcon)
        assertEquals("Contrary to popular belief", actualDuoMessageData.conversationTitle)
        assertEquals(expectedUser, actualDuoMessageData.you)
        assertEquals(expectedMessage.size, actualDuoMessageData.messages.size)
        assertTrue(actualDuoMessageData.largeIcon?.sameAs(expectedLargeIcon)!!)
        assertEquals(expectedPendingIntent, actualDuoMessageData.contentIntent)
        assertEquals(false, actualDuoMessageData.autoCancel)
    }

    @Test
    fun asCustomDesign_shouldSetData() {
        val context: Context = ApplicationProvider.getApplicationContext()
        notifyConfig.asCustomDesign {
            smallIcon = R.drawable.test_ic_notification_24
            hasStyle = true
            smallRemoteViews = {
                val remoteViews = RemoteViews(context.packageName, R.layout.test_small_notification)
                remoteViews.setTextViewText(R.id.notification_title, "Small title")
                remoteViews
            }
            largeRemoteViews = {
                val remoteViews = RemoteViews(context.packageName, R.layout.test_large_notification)
                remoteViews.setTextViewText(R.id.notification_title, "Large title")
                remoteViews
            }
        }
        val dataField = notifyConfig.javaClass.getDeclaredField("data")
        dataField.isAccessible = true
        val actualCustomDesignData = dataField.get(notifyConfig) as Data.CustomDesignData
        val actualSmallRemoteView = actualCustomDesignData.smallRemoteViews.invoke()
        val actualLargeRemoteView = actualCustomDesignData.largeRemoteViews.invoke()

        assertEquals(R.drawable.test_ic_notification_24, actualCustomDesignData.smallIcon)
        assertEquals(R.layout.test_small_notification, actualSmallRemoteView?.layoutId)
        assertEquals(R.layout.test_large_notification, actualLargeRemoteView?.layoutId)
    }

    @Test
    fun extras_shouldSetData() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedFullScreenIntent = Pair(TestDataProvider.pendingIntent(), true)
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
    fun stackable_shouldSetData() {
        notifyConfig.stackable {
            id = 152
            smallIcon = R.drawable.test_ic_notification_24
            title = "My Group Summary"
            summaryText = "Any description"
            initialAmount = 5
        }
        val stackableField = notifyConfig.javaClass.getDeclaredField("stackableData")
        stackableField.isAccessible = true
        val actualExtraData = stackableField.get(notifyConfig) as StackableData

        assertEquals(152, actualExtraData.id)
        assertEquals(R.drawable.test_ic_notification_24, actualExtraData.smallIcon)
        assertEquals("My Group Summary", actualExtraData.title)
        assertEquals("Any description", actualExtraData.summaryText)
        assertEquals(5, actualExtraData.initialAmount)
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
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        notifyConfig.addAction {
            icon = R.drawable.test_ic_message_24
            label = "Action 1"
            pending = expectedPendingIntent
        }
            .addAction {
                icon = R.drawable.test_ic_mail_24
                label = "Action 2"
                pending = expectedPendingIntent
            }
            .addAction {
                icon = R.drawable.test_ic_archive_24
                label = "Action 3"
                pending = expectedPendingIntent
            }
            .addAction {
                icon = R.drawable.test_ic_message_24
                label = "Action 4"
                pending = expectedPendingIntent
            }

        assertEquals(3, notifyConfig.actions.size)
    }

    @Test
    fun addReplyAction_whenRegisteringMultipleItems_keepOnlyThreeItems() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedRemote = RemoteInput.Builder("anyKey").build()
        notifyConfig.addReplyAction {
            icon = R.drawable.test_ic_message_24
            label = "Action 1"
            replyPending = expectedPendingIntent
            remote = expectedRemote
        }
            .addReplyAction {
                icon = R.drawable.test_ic_mail_24
                label = "Action 2"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }
            .addReplyAction {
                icon = R.drawable.test_ic_archive_24
                label = "Action 3"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }
            .addReplyAction {
                icon = R.drawable.test_ic_message_24
                label = "Action 4"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }

        assertEquals(3, notifyConfig.actions.size)
    }

    @Test
    fun show_usingStackable_shouldShowGroup() = runTest {
        val myGroupKey = "ANY_GROUP_KEY1"
        val expectedNumberNotifications = 4
        val expectedGroupNotificationId = 50
        repeat(expectedNumberNotifications) { count ->
            val actualNotificationId = notifyConfig.asBasic {
                title = "Test Title $count"
                text = "Test Text $count"
            }.extras {
                groupKey = myGroupKey
            }.stackable {
                id = expectedGroupNotificationId
                title = "Any Group Title"
                summaryText = "Any Group Summary"
                initialAmount = expectedNumberNotifications
            }.show()
            //wait for the penultimate notification to be released
            if (count == expectedNumberNotifications - 2)
                notificationManager.waitForNotification(actualNotificationId.first)
        }
        val statusBarNotification =
            notificationManager.waitForNotification(expectedGroupNotificationId)

        assertEquals(expectedGroupNotificationId, statusBarNotification?.id)
        val groupNotifications = notificationManager.activeNotifications
            .filter { it.groupKey.contains(myGroupKey) }
        assertEquals(expectedNumberNotifications + 1, groupNotifications.size)
    }

    @Test
    fun show_usingStackableWithDifferentStyles_shouldShowGroup() = runTest {
        val myGroupKey = "ANY_GROUP_KEY2"
        notifyConfig.asBasic {
            title = "Test Title 1"
            text = "Test Text 1"
        }.extras {
            groupKey = myGroupKey
        }.show()
        notifyConfig.asBigPicture {
            title = "Test Title 2"
            text = "Test Text 2"
            summaryText = "Any summary 2"
        }.extras {
            groupKey = myGroupKey
        }.show()
        val penultimateNotificationId = notifyConfig.asInbox {
            title = "Test Title 2"
            text = "Test Text 2"
            lines = arrayListOf("item 1", "item 2")
        }.extras {
            groupKey = myGroupKey
        }.show().first
        notificationManager.waitForNotification(penultimateNotificationId)
        val expectedGroupNotificationId = notifyConfig.asBigText {
            title = "Test Title 2"
            text = "Test Text 2"
            bigText = "Any Big Text"
        }.extras {
            groupKey = myGroupKey
        }.stackable {
            title = "Any Group Title"
            summaryText = "Any Group Summary"
            initialAmount = 4
        }.show().second
        val statusBarNotification =
            notificationManager.waitForNotification(expectedGroupNotificationId)

        assertEquals(expectedGroupNotificationId, statusBarNotification?.id)
        val groupNotifications = notificationManager.activeNotifications
            .filter { it.groupKey.contains(myGroupKey) }
        assertEquals(5, groupNotifications.size)
    }

    @Test
    fun show_whenDataIsNull_shouldNotShow() = runTest {
        val actualNotificationId = notifyConfig.show().first

        assertEquals(-1, actualNotificationId)
        assertNull(notificationManager.waitForNotification(actualNotificationId, timeout = 2000))
    }

    @Test
    fun show_whenDataIsNotNull_shouldBeShown() = runTest {
        val expectedNotificationId = 1
        notifyConfig
            .asBasic {
                id = expectedNotificationId
                title = "Test Title"
                text = "Test Text"
            }
            .show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(expectedNotificationId)
        val actualExtras = actualStatusBarNotification?.notification?.extras
        val actualTitle = actualExtras?.getString(NotificationCompat.EXTRA_TITLE)
        val actualText = actualExtras?.getString(NotificationCompat.EXTRA_TEXT)

        assertNotNull(actualStatusBarNotification?.notification)
        assertEquals("Test Title", actualTitle)
        assertEquals("Test Text", actualText)
    }

}