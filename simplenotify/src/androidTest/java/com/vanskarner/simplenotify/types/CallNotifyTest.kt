package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.NotifyConfig
import com.vanskarner.simplenotify.SimpleNotify
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertBaseData
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.assertNotificationSound
import com.vanskarner.simplenotify.common.getCustomParcelable
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_CALL_CHANNEL_ID
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallNotifyTest {
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
    fun useCall_withAllBaseAttributes_shouldApply() {
        val expectedData = TestDataProvider.callData(context)
        val actualNotification = SimpleNotify.with(context)
            .asCall {
                subText = expectedData.subText
                largeIcon = expectedData.largeIcon
                contentIntent = expectedData.contentIntent
                autoCancel = expectedData.autoCancel
                timeoutAfter = expectedData.timeoutAfter
                smallIcon = expectedData.smallIcon
                type = expectedData.type
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }.generateBuilder()?.build() ?: Notification()

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertBaseData(expectedData, actualNotification)
        assertCallNotification(expectedData, actualNotification)
    }

    @Test
    fun useCall_whenIsIncoming_shouldApply() = runTest {
        val expectedData = TestDataProvider.callData(context)
        expectedData.type = "incoming"
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertCallNotification(expectedData, actualNotification)
    }

    @Test
    fun useCall_whenIsOngoing_shouldApply() = runTest {
        val expectedData = TestDataProvider.callData(context)
        expectedData.type = "ongoing"
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                declineOrHangup = expectedData.declineOrHangup
            }
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertCallNotification(expectedData, actualNotification)
    }

    @Test
    fun useCall_whenIsScreening_shouldApply() = runTest {
        val expectedData = TestDataProvider.callData(context)
        expectedData.type = "screening"
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertCallNotification(expectedData, actualNotification)
    }

    @Test
    fun useExtras_shouldApply() {
        val expectedData = TestDataProvider.callData(context)
        val expectedExtra = TestDataProvider.extraData()
        val actualNotification = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }
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
        //Category can not be changed until api 30
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            assertEquals(NotificationCompat.CATEGORY_CALL, actualNotification.category)
        } else {
            assertEquals(expectedExtra.category, actualNotification.category)
        }
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
    fun useProgress_shouldNotApply() = runTest {
        val expectedData = TestDataProvider.callData(context)
        val expectedProgress = TestDataProvider.progressData()
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }.progress {
                currentValue = expectedProgress.currentValue
                indeterminate = expectedProgress.indeterminate
            }
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    @Test
    fun useProgress_whenIsHide_shouldNotApply() = runTest {
        val expectedData = TestDataProvider.callData(context)
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }.progress {
                hide = true
            }
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    @Test
    fun useChannel_shouldApply() = runTest {
        val expectedChannelId = TestDataProvider.createChannel(notifyManager)
        val expectedData = TestDataProvider.callData(context)
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = expectedData.type
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
            }.useChannel(expectedChannelId)
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)

        assertNotificationChannelId(expectedChannelId, actualNotification)
        assertCallNotification(expectedData, actualNotification)
    }

    @Test
    fun useActionAndReplyAction_whenIsIncoming_shouldApplyOnlyOne() = runTest {
        val expectedData = TestDataProvider.callData(context)
        val expectedAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        val actualConfig = SimpleNotify.with(context)
            .asCall {
                type = "incoming"
                verificationText = expectedData.verificationText
                verificationIcon = expectedData.verificationIcon
                caller = expectedData.caller
                answer = expectedData.answer
                declineOrHangup = expectedData.declineOrHangup
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
        val actualNotification = showOrGenerateNotificationAccordingAPI(actualConfig)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        //type “incoming” already includes 2 actions: “decline” and “answer”, and maximum 3 are allowed
        assertEquals(3, actualNotification.actions.size)
        assertCallNotification(expectedData, actualNotification)
    }

    private suspend fun showOrGenerateNotificationAccordingAPI(config: NotifyConfig): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //From API 31, CallStyle notifications must either be for a foreground Service
            config.generateBuilder()?.build() ?: Notification()
        } else {
            val notificationPair = config.show()
            val actualStatusBarNotification =
                notifyManager.waitForNotification(notificationPair.first)
            actualStatusBarNotification.notification
        }
    }

    private fun assertCallNotification(expectedData: Data.CallData, actualNotification: Notification) {
        val expectedCallType = mapOf(
            "incoming" to NotificationCompat.CallStyle.CALL_TYPE_INCOMING,
            "ongoing" to NotificationCompat.CallStyle.CALL_TYPE_ONGOING,
            "screening" to NotificationCompat.CallStyle.CALL_TYPE_SCREENING
        )[expectedData.type]
        val actualExtras = actualNotification.extras
        val actualCallType = actualExtras.getInt(NotificationCompat.EXTRA_CALL_TYPE)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualVerificationText =
            actualExtras.getCharSequence(NotificationCompat.EXTRA_VERIFICATION_TEXT)
        val actualVerificationIcon = actualExtras.getCustomParcelable(
            NotificationCompat.EXTRA_VERIFICATION_ICON,
            Icon::class.java
        )
        val expectedSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val actualOngoing = actualNotification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0

        assertEquals(expectedCallType, actualCallType)
        when (expectedCallType) {
            NotificationCompat.CallStyle.CALL_TYPE_INCOMING -> {
                val actualAnswer = actualExtras.getCustomParcelable(
                    NotificationCompat.EXTRA_ANSWER_INTENT,
                    PendingIntent::class.java
                )
                val actualDecline = actualExtras.getCustomParcelable(
                    NotificationCompat.EXTRA_DECLINE_INTENT,
                    PendingIntent::class.java
                )

                assertEquals(expectedData.answer, actualAnswer)
                assertEquals(expectedData.declineOrHangup, actualDecline)
            }

            NotificationCompat.CallStyle.CALL_TYPE_ONGOING -> {
                val actualHangUp = actualExtras.getCustomParcelable(
                    NotificationCompat.EXTRA_HANG_UP_INTENT,
                    PendingIntent::class.java
                )

                assertEquals(expectedData.declineOrHangup, actualHangUp)
            }

            NotificationCompat.CallStyle.CALL_TYPE_SCREENING -> {
                val actualAnswer = actualExtras.getCustomParcelable(
                    NotificationCompat.EXTRA_ANSWER_INTENT,
                    PendingIntent::class.java
                )
                val actualHangUp = actualExtras.getCustomParcelable(
                    NotificationCompat.EXTRA_HANG_UP_INTENT,
                    PendingIntent::class.java
                )

                assertEquals(expectedData.answer, actualAnswer)
                assertEquals(expectedData.declineOrHangup, actualHangUp)
            }

            else -> throw IllegalArgumentException("Unsupported call style: $expectedCallType")
        }
        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertEquals(NotificationCompat.CATEGORY_CALL, actualNotification.category)
        assertEquals(expectedData.caller?.name, actualNamePersonAdded)
        assertEquals(expectedData.verificationText, actualVerificationText)
        assertNotNull(actualVerificationIcon)
        assertNotificationSound(expectedSound, actualNotification)
        assertTrue(actualOngoing)
    }

}