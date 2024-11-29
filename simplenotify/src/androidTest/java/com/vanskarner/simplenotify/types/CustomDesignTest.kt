package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.NotifyConfig
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.simplenotify.internal.DEFAULT_PROGRESS_CHANNEL_ID
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
class CustomDesignTest {
    private lateinit var notifyConfig: NotifyConfig
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notifyConfig = NotifyConfig(context)
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun show_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingProgress_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedProgress = 50
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }.progress {
            currentValue = expectedProgress
            indeterminate = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
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
    fun show_whenProgressIsHide_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }.progress {
            hide = true
        }.show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
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
    fun show_usingSpecificChannel_shouldBeShown() = runTest {
        val channelId = TestDataProvider.createChannel(notificationManager)
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }
            .useChannel(channelId).show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(channelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun show_usingActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }
            .addAction {
                icon = expectedAction.icon
                label = expectedAction.label
                pending = expectedAction.pending
            }
            .addReplyAction {
                icon = expectedReplyAction.icon
                label = expectedReplyAction.label
                replyPending = expectedReplyAction.replyPending
                remote = expectedReplyAction.remote
            }
            .show()
        val actualStatusBarNotification =
            notificationManager.waitForNotification(actualNotificationPair.first)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    @Test
    fun generateNotificationPair_shouldBeGenerated() = runTest {
        val expectedData = TestDataProvider.customDesignData(context)
        val actualNotificationPair = notifyConfig.asCustomDesign {
            smallIcon = expectedData.smallIcon
            smallRemoteViews = expectedData.smallRemoteViews
            largeRemoteViews = expectedData.largeRemoteViews
        }.generateNotificationPair()
        val actualNotification = actualNotificationPair.second!!.build()

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
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