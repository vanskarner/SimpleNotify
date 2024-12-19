package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
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
import com.vanskarner.simplenotify.common.getCustomParcelable
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
class BigPictureNotifyTest {
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
    fun useBigPicture_shouldApply() = runTest {
        val expectedData = TestDataProvider.bigPictureData()
        val actualNotifyConfig = SimpleNotify.with(context)
            .asBigPicture {
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
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
        assertBigPicture(expectedData, actualNotification)
        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotificationGenerated)
        assertBigPicture(expectedData, actualNotificationGenerated)
    }

    @Test
    fun useBigPicture_withAllBaseAttributes_shouldApply() {
        val expectedData = TestDataProvider.bigPictureData()
        val actualNotification = SimpleNotify.with(context)
            .asBigPicture {
                subText = expectedData.subText
                largeIcon = expectedData.largeIcon
                contentIntent = expectedData.contentIntent
                autoCancel = expectedData.autoCancel
                timeoutAfter = expectedData.timeoutAfter
                smallIcon = expectedData.smallIcon
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
            }.generateBuilder()?.build() ?: Notification()

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertBaseData(expectedData, actualNotification)
        assertBigPicture(expectedData, actualNotification)
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asBigPicture { }
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
        val expectedData = TestDataProvider.bigPictureData()
        val expectedProgress = TestDataProvider.progressData()
        val notificationId = 20
        SimpleNotify.with(context)
            .asBigPicture {
                id = notificationId
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
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
        assertBigPictureForProgress(expectedData, actualNotification)
    }

    @Test
    fun useProgress_whenIsHide_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.bigPictureData()
        val notificationId = 21
        SimpleNotify.with(context)
            .asBigPicture {
                id = notificationId
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
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
        assertBigPicture(expectedData, actualNotification)
    }

    @Test
    fun useChannel_shouldBeShown() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.bigPictureData()
        val actualNotificationIds = SimpleNotify.with(context)
            .asBigPicture {
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
            }
            .useChannel(expectedChannelId)
            .show()
        val notificationId = actualNotificationIds.first
        val actualStatusBarNotification = notifyManager.waitForNotification(notificationId)
        val actualNotification = actualStatusBarNotification.notification

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertBigPicture(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_shouldBeShown() = runTest {
        val expectedData = TestDataProvider.bigPictureData()
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualNotificationIds = SimpleNotify.with(context)
            .asBigPicture {
                title = expectedData.title
                text = expectedData.text
                summaryText = expectedData.summaryText
                image = expectedData.image
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
        assertBigPicture(expectedData, actualNotification)
        assertEquals(2, actualNotification.actions.size)
    }

    private fun assertBigPicture(
        expectedData: Data.BigPictureData,
        actualNotification: Notification
    ) {
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        assertBigPictureNotification(expectedData, expectedSound, actualNotification)
    }

    private fun assertBigPictureForProgress(
        expectedData: Data.BigPictureData,
        actualNotification: Notification
    ) {
        assertBigPictureNotification(expectedData, null, actualNotification)
    }

    private fun assertBigPictureNotification(
        expectedData: Data.BigPictureData,
        expectedSound: Uri?,
        actualNotification: Notification
    ) {
        val actualExtras = actualNotification.extras
        val actualSummaryText = actualExtras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)
        val actualPicture =
            actualExtras.getCustomParcelable(NotificationCompat.EXTRA_PICTURE, Bitmap::class.java)

        assertNotificationPriority(NotificationCompat.PRIORITY_DEFAULT, actualNotification)
        assertEquals(expectedData.title, actualExtras?.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(expectedData.text, actualExtras?.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(expectedData.summaryText, actualSummaryText)
        assertTrue(expectedData.image?.sameAs(actualPicture) ?: false)
        assertNotificationSound(expectedSound, actualNotification)
    }

}