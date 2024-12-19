package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.SimpleNotify
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertBaseData
import com.vanskarner.simplenotify.common.assertExtraData
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicNotifyTest {
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
    fun useBasic_shouldApply() = runTest {
        val expectedData = TestDataProvider.basicData()
        val actualNotifyConfig = SimpleNotify.with(context)
            .asBasic {
                title = expectedData.title
                text = expectedData.text
            }
        val actualNotificationIds = actualNotifyConfig.show()
        val actualNotificationGenerated =
            actualNotifyConfig.generateBuilder()?.build() ?: Notification()
        val notificationId = actualNotificationIds.first
        val actualGroupNotificationId = actualNotificationIds.second
        val actualNotification = notifyManager.waitForNotification(notificationId).notification

        assertEquals(INVALID_NOTIFICATION_ID, actualGroupNotificationId)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertBasic(expectedData, actualNotification)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotificationGenerated)
        assertBasic(expectedData, actualNotificationGenerated)
    }

    @Test
    fun useBasic_withAllBaseAttributes_shouldApply() {
        val expectedData = TestDataProvider.basicData()
        val actualNotification = SimpleNotify.with(context)
            .asBasic {
                subText = expectedData.subText
                largeIcon = expectedData.largeIcon
                contentIntent = expectedData.contentIntent
                autoCancel = expectedData.autoCancel
                timeoutAfter = expectedData.timeoutAfter
                smallIcon = expectedData.smallIcon
                title = expectedData.title
                text = expectedData.text
            }.generateBuilder()?.build() ?: Notification()

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertBaseData(expectedData, actualNotification)
        assertBasic(expectedData, actualNotification)
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asBasic {}
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

        assertExtraData(expectedExtra, actualNotification)
    }

    @Test
    fun useProgress_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val expectedProgress = TestDataProvider.progressData()
        val notificationId = 10
        SimpleNotify.with(context)
            .asBasic {
                id = notificationId
                title = expectedData.title
                text = expectedData.text
            }.progress {
                currentValue = expectedProgress.currentValue
                indeterminate = expectedProgress.indeterminate
            }.show()
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_PROGRESS_CHANNEL_ID, actualNotification)
        assertEquals(expectedProgress.currentValue, actualProgress)
        assertEquals(expectedProgress.indeterminate, actualIndeterminate)
        assertBasicForProgress(expectedData, actualNotification)
    }

    @Test
    fun useProgress_whenIsHide_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val notificationId = 11
        SimpleNotify.with(context)
            .asBasic {
                id = notificationId
                title = expectedData.title
                text = expectedData.text
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
        assertBasic(expectedData, actualNotification)
    }

    @Test
    fun useChannel_shouldBeShown() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.basicData()
        val actualNotificationIds = SimpleNotify.with(context)
            .asBasic {
                title = expectedData.title
                text = expectedData.text
            }
            .useChannel(expectedChannelId)
            .show()
        val notificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertBasic(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.basicData()
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationIds = SimpleNotify.with(context)
            .asBasic {
                title = expectedData.title
                text = expectedData.text
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
        assertBasic(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    private fun assertBasic(expectedData: Data.BasicData, actualNotification: Notification) {
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        assertBasicNotification(expectedData, expectedSound, actualNotification)
    }

    private fun assertBasicForProgress(
        expectedData: Data.BasicData,
        actualNotification: Notification
    ) {
        assertBasicNotification(expectedData, null, actualNotification)
    }

    private fun assertBasicNotification(
        expectedData: Data.BasicData,
        expectedSound: Uri?,
        actualNotification: Notification
    ) {
        val actualExtras = actualNotification.extras
        val actualTitle = actualExtras?.getString(NotificationCompat.EXTRA_TITLE)
        val actualText = actualExtras?.getString(NotificationCompat.EXTRA_TEXT)

        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertEquals(expectedData.title, actualTitle)
        assertEquals(expectedData.text, actualText)
        assertNotificationSound(expectedSound, actualNotification)
    }

}