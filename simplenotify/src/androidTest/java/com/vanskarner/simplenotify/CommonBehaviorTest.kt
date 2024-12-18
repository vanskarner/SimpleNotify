package com.vanskarner.simplenotify

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.waitForAllNotificationsPresents
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.common.waitNotificationDisappear
import com.vanskarner.simplenotify.internal.DEFAULT_PROGRESS_NOTIFICATION_ID
import com.vanskarner.simplenotify.internal.INVALID_NOTIFICATION_ID
import com.vanskarner.simplenotify.internal.MAXIMUM_ACTIONS
import com.vanskarner.simplenotify.internal.RANGE_GROUP_NOTIFICATION
import com.vanskarner.simplenotify.internal.RANGE_NOTIFICATION
import com.vanskarner.simplenotify.test.R
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommonBehaviorTest {
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
    fun show_withUnspecifiedType_shouldBeInvalid() {
        val actualNotifyConfig = SimpleNotify.with(context)
        val actualNotificationIds = actualNotifyConfig.show()
        val actualNotificationGenerated = actualNotifyConfig.generateBuilder()

        assertEquals(INVALID_NOTIFICATION_ID, actualNotificationIds.first)
        assertEquals(INVALID_NOTIFICATION_ID, actualNotificationIds.second)
        assertNull(actualNotificationGenerated)
    }

    @Test
    fun show_withUnspecifiedId_shouldIdBeWithinRange() {
        val actualNotificationIds = SimpleNotify.with(context)
            .asBasic { }
            .show()
        val actualNotificationId = actualNotificationIds.first
        val notificationRange = RANGE_NOTIFICATION.first..RANGE_NOTIFICATION.second

        assertTrue(
            "Number $actualNotificationId should be in range $notificationRange",
            actualNotificationId in notificationRange
        )
        assertEquals(INVALID_NOTIFICATION_ID, actualNotificationIds.second)
    }

    @Test
    fun useStackable_withDifferentStyles_shouldShowGroup() = runTest {
        val myGroupKey = "ANY_GROUP_KEY2"
        val notification1 = SimpleNotify.with(context)
            .asBasic {
                title = "Test Title 1"
                text = "Test Text 1"
            }.extras {
                groupKey = myGroupKey
            }.show()
        val notification2 = SimpleNotify.with(context)
            .asBigPicture {
                title = "Test Title 2"
                text = "Test Text 2"
                summaryText = "Any summary 2"
            }.extras {
                groupKey = myGroupKey
            }.show()
        //wait for all notifications to be displayed to activate the grouping function
        val notificationIds = setOf(notification1.first, notification2.first)
        notifyManager.waitForAllNotificationsPresents(notificationIds)
        val notification3 = SimpleNotify.with(context)
            .asBigText {
                title = "Test Title 3"
                text = "Test Text 3"
                bigText = "Any Big Text 3"
            }.extras {
                groupKey = myGroupKey
            }.stackable {
                title = "Any Group Title"
                summaryText = "Any Group Summary"
                initialAmount = 3
            }
            .show()
        //Here are the identification of the individual and group notifications
        val latestIdNotifications = setOf(notification3.first, notification3.second)
        val actualGroupNotificationId = notification3.second
        notifyManager.waitForAllNotificationsPresents(latestIdNotifications)
        val activeNotifications = notifyManager.activeNotifications
        val actualGroupedNotificationsSize = activeNotifications
            .filter { it.groupKey.contains(myGroupKey) }
            .size
        val groupNotificationRange = RANGE_GROUP_NOTIFICATION.first..RANGE_GROUP_NOTIFICATION.second

        //group notification is also considered in verification
        assertEquals(4, actualGroupedNotificationsSize)
        assertTrue(activeNotifications.any { it.id == notification1.first })
        assertTrue(activeNotifications.any { it.id == notification2.first })
        assertEquals(-1, notification1.second)
        assertEquals(-1, notification2.second)
        assertTrue(activeNotifications.any { it.id == notification3.first })
        assertTrue(activeNotifications.any { it.id == actualGroupNotificationId })
        assertTrue(
            "Number $actualGroupNotificationId should be in range $groupNotificationRange",
            actualGroupNotificationId in groupNotificationRange
        )
    }

    @Test
    fun addActions_whenIsMoreThanThree_keepOnlyThreeItems() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedRemote = RemoteInput.Builder("anyKey").build()
        val actualNotifyConfig = SimpleNotify.with(context)
            .addAction {
                icon = R.drawable.test_ic_message_24
                title = "Action 1"
                pending = expectedPendingIntent
            }
            .addReplyAction {
                icon = R.drawable.test_ic_mail_24
                title = "Action 2"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }
            .addAction {
                icon = R.drawable.test_ic_archive_24
                title = "Action 3"
                pending = expectedPendingIntent
            }
            .addReplyAction {
                icon = R.drawable.test_ic_notification_24
                title = "Action 4"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }

        assertEquals(MAXIMUM_ACTIONS, actualNotifyConfig.actions.size)
    }

    @Test
    fun useProgress_withUnspecifiedId_shouldUseDefaultIdForProgress() {
        val actualNotificationIds = SimpleNotify.with(context)
            .asBasic { }
            .progress {
                currentValue = 50
            }.show()

        assertEquals(DEFAULT_PROGRESS_NOTIFICATION_ID, actualNotificationIds.first)
        assertEquals(INVALID_NOTIFICATION_ID, actualNotificationIds.second)
    }

    @Test
    fun cancel_shouldBeCancel() = runTest {
        val testChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedNotificationId = 2
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannelId)
        notifyManager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = notifyManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        SimpleNotify.cancel(context, expectedNotificationId)
        assertTrue(notifyManager.waitNotificationDisappear(expectedNotificationId))
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel() = runTest {
        val testChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedNotificationId = 3
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannelId)
        notifyManager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = notifyManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        SimpleNotify.cancelAll(context)
        assertTrue(notifyManager.waitNotificationDisappear(expectedNotificationId))
    }

}