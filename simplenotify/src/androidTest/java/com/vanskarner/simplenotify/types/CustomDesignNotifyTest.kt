package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.SimpleNotify
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.assertNotificationSound
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.simplenotify.internal.DEFAULT_PROGRESS_CHANNEL_ID
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
class CustomDesignNotifyTest {
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
    fun useCustomDesign_shouldApply() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotifyConfig = SimpleNotify.with(context)
            .asCustomDesign {
                smallIcon = expectedData.smallIcon
                smallRemoteViews = expectedData.smallRemoteViews
                largeRemoteViews = expectedData.largeRemoteViews
            }
        val actualNotificationIds = actualNotifyConfig.show()
        val actualNotificationGenerated =
            actualNotifyConfig.generateBuilder()?.build() ?: Notification()
        val notificationId = actualNotificationIds.first
        val actualGroupNotificationId = actualNotificationIds.second
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertEquals(INVALID_NOTIFICATION_ID, actualGroupNotificationId)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotificationGenerated)
        assertCommonData(expectedData, actualNotificationGenerated)
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asCustomDesign { }
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
        val expectedPriority = expectedExtra.priority ?: -666
        val actualExtras = actualNotification.extras
        val actualOngoing = actualNotification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        val actualOnlyAlertOnce =
            actualNotification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        val actualShowWhen = actualExtras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        val actualUsesChronometer = NotificationCompat.getUsesChronometer(actualNotification)
        val actualBadgeNumber = actualNotification.number
        val actualRemoteInputHistory =
            actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_REMOTE_INPUT_HISTORY)
        val actualGroupKey = actualNotification.group
        val expectedSound = expectedExtra.sounds ?: Uri.EMPTY

        assertNotificationPriority(expectedPriority, actualNotification)
        assertEquals(expectedExtra.category, actualNotification.category)
        assertEquals(expectedExtra.visibility, actualNotification.visibility)
        assertEquals(expectedExtra.ongoing, actualOngoing)
        assertEquals(expectedExtra.color, actualNotification.color)
        assertEquals(expectedExtra.timestampWhen, actualNotification.`when`)
        assertEquals(expectedExtra.deleteIntent, actualNotification.deleteIntent)
        assertEquals(expectedExtra.fullScreenIntent?.first, actualNotification.fullScreenIntent)
        assertEquals(expectedExtra.onlyAlertOnce, actualOnlyAlertOnce)
        assertEquals(expectedExtra.showWhen, actualShowWhen)
        assertEquals(expectedExtra.useChronometer, actualUsesChronometer)
        assertEquals(expectedExtra.badgeNumber, actualBadgeNumber)
        assertNotificationSound(expectedSound, actualNotification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actualBadgeIconType = actualNotification.badgeIconType
            val actualShortcutId = actualNotification.shortcutId

            assertEquals(expectedExtra.badgeIconType, actualBadgeIconType)
            assertEquals(expectedExtra.shortCutId, actualShortcutId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val actualSystemGeneratedActions =
                actualNotification.allowSystemGeneratedContextualActions
            assertEquals(
                expectedExtra.allowSystemGeneratedContextualActions,
                actualSystemGeneratedActions
            )
        }
        assertEquals(expectedExtra.remoteInputHistory?.size, actualRemoteInputHistory?.size)
        assertEquals(expectedExtra.groupKey, actualGroupKey)
    }

    @Test
    fun useProgress_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedProgress = 50
        val notificationId = 50
        SimpleNotify.with(context)
            .asCustomDesign {
                id = notificationId
                smallIcon = expectedData.smallIcon
                smallRemoteViews = expectedData.smallRemoteViews
                largeRemoteViews = expectedData.largeRemoteViews
            }.progress {
                currentValue = expectedProgress
                indeterminate = true
            }.show()
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
        assertEquals(expectedProgress, actualProgress)
        assertTrue(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun useProgress_whenIsHide_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val notificationId = 51
        SimpleNotify.with(context)
            .asCustomDesign {
                id = notificationId
                smallIcon = expectedData.smallIcon
                smallRemoteViews = expectedData.smallRemoteViews
                largeRemoteViews = expectedData.largeRemoteViews
            }.progress {
                hide = true
            }.show()
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun useChannel_shouldBeShown() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotificationIds = SimpleNotify.with(context)
            .asCustomDesign {
                smallIcon = expectedData.smallIcon
                smallRemoteViews = expectedData.smallRemoteViews
                largeRemoteViews = expectedData.largeRemoteViews
            }
            .useChannel(expectedChannelId)
            .show()
        val notificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationIds = SimpleNotify.with(context)
            .asCustomDesign {
                smallIcon = expectedData.smallIcon
                smallRemoteViews = expectedData.smallRemoteViews
                largeRemoteViews = expectedData.largeRemoteViews
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
        val notificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    private fun assertCommonData(
        expectedData: Data.CustomDesignData,
        actualNotification: Notification
    ) {
        @Suppress("DEPRECATION") //Deprecated in API level 24, no exchange option
        val actualSmallRemoteView = actualNotification.contentView.layoutId

        @Suppress("DEPRECATION") //Deprecated in API level 24, no exchange option
        val actualLargeRemoteView = actualNotification.bigContentView.layoutId

        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertEquals(expectedData.smallRemoteViews.invoke()?.layoutId, actualSmallRemoteView)
        assertEquals(expectedData.largeRemoteViews.invoke()?.layoutId, actualLargeRemoteView)
    }

}