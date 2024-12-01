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
import com.vanskarner.simplenotify.internal.RANGE_GROUP_NOTIFICATION
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
    fun addReplyAction_whenRegisteringMultipleItems_keepOnlyThreeItems() {
        val expectedPendingIntent = TestDataProvider.pendingIntent()
        val expectedRemote = RemoteInput.Builder("anyKey").build()
        notifyConfig.addAction {
            icon = R.drawable.test_ic_message_24
            title = "Action 1"
            pending = expectedPendingIntent
        }
            .addReplyAction {
                icon = R.drawable.test_ic_message_24
                title = "Action 1"
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
                title = "Action 3"
                replyPending = expectedPendingIntent
                remote = expectedRemote
            }

        assertEquals(3, notifyConfig.actions.size)
    }

    @Test
    fun show_whenDataIsNull_shouldNotShow() = runTest {
        val actualNotification = notifyConfig.show()
        val actualNotificationId = actualNotification.first
        val actualGroupNotificationId = actualNotification.second

        assertEquals(-1, actualNotificationId)
        assertEquals(-1, actualGroupNotificationId)
    }

    @Test
    fun show_usingStackableWithDifferentStyles_shouldShowGroup() = runTest {
        val myGroupKey = "ANY_GROUP_KEY2"
        val notification1 = notifyConfig.asBasic {
            title = "Test Title 1"
            text = "Test Text 1"
        }.extras {
            groupKey = myGroupKey
        }.show()
        val notification2 = notifyConfig.asBigPicture {
            title = "Test Title 2"
            text = "Test Text 2"
            summaryText = "Any summary 2"
        }.extras {
            groupKey = myGroupKey
        }.show()
        //wait for all notifications to be displayed to activate the grouping function
        val notificationIds = setOf(notification1.first, notification2.first)
        notificationManager.waitForAllNotificationsPresents(notificationIds)
        val notification3 = notifyConfig.asBigText {
            title = "Test Title 3"
            text = "Test Text 3"
            bigText = "Any Big Text 3"
        }.extras {
            groupKey = myGroupKey
        }.stackable {
            title = "Any Group Title"
            summaryText = "Any Group Summary"
            initialAmount = 3
        }.show()
        //Here are the identification of the individual and group notifications
        val latestIdNotifications = setOf(notification3.first, notification3.second)
        val actualGroupNotificationId = notification3.second
        notificationManager.waitForAllNotificationsPresents(latestIdNotifications)
        val activeNotifications = notificationManager.activeNotifications
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

}