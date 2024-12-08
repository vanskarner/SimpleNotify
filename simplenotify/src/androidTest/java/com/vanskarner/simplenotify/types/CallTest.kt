package com.vanskarner.simplenotify.types

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.vanskarner.simplenotify.common.getCustomParcelable
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.internal.DEFAULT_CALL_CHANNEL_ID
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
class CallTest {
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
    fun showOrGenerate_shouldBeProduced() = runTest {
        val expectedData = TestDataProvider.callData()
        notifyConfig.asCall {
            smallIcon = expectedData.smallIcon
            type = expectedData.type
            caller = expectedData.caller
            answer = expectedData.answer
            declineOrHangup = expectedData.declineOrHangup
        }
        val actualNotification = showOrGenerateNotificationAccordingAPI()

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun showOrGenerate_usingProgress_shouldBeProduced() = runTest {
        val expectedData = TestDataProvider.callData()
        val expectedProgress = 50
        val notificationId = 40
        notifyConfig.asCall {
            id = notificationId
            smallIcon = expectedData.smallIcon
            type = expectedData.type
            caller = expectedData.caller
            answer = expectedData.answer
            declineOrHangup = expectedData.declineOrHangup
        }.progress {
            currentValue = expectedProgress
            indeterminate = true
        }
        val actualNotification = showOrGenerateNotificationAccordingAPI()
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertEquals(expectedProgress, actualProgress)
        assertTrue(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun showOrGenerate_whenProgressIsHide_shouldBeProduced() = runTest {
        val expectedData = TestDataProvider.callData()
        val notificationId = 41
        notifyConfig.asCall {
            id = notificationId
            smallIcon = expectedData.smallIcon
            type = expectedData.type
            caller = expectedData.caller
            answer = expectedData.answer
            declineOrHangup = expectedData.declineOrHangup
        }.progress {
            hide = true
        }
        val actualNotification = showOrGenerateNotificationAccordingAPI()
        val actualExtras = actualNotification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun showOrGenerate_usingSpecificChannel_shouldBeProduced() = runTest {
        val channelId = TestDataProvider.createChannel(notificationManager)
        val expectedData = TestDataProvider.callData()
        notifyConfig.asCall {
            smallIcon = expectedData.smallIcon
            type = expectedData.type
            caller = expectedData.caller
            answer = expectedData.answer
            declineOrHangup = expectedData.declineOrHangup
        }.useChannel(channelId)
        val actualNotification = showOrGenerateNotificationAccordingAPI()

        assertNotificationChannelId(channelId, actualNotification)
        assertCommonData(expectedData, actualNotification)
    }

    @Test
    fun showOrGenerate_usingAction_shouldBeProduced() = runTest {
        val expectedData = TestDataProvider.callData()
        val expectedAction = TestDataProvider.basicAction()
        notifyConfig.asCall {
            smallIcon = expectedData.smallIcon
            type = "incoming"
            caller = expectedData.caller
            answer = expectedData.answer
            declineOrHangup = expectedData.declineOrHangup
        }.addAction {
            icon = expectedAction.icon
            title = expectedAction.title
            pending = expectedAction.pending
        }
        val actualNotification = showOrGenerateNotificationAccordingAPI()

        assertNotificationChannelId(DEFAULT_CALL_CHANNEL_ID, actualNotification)
        //type “incoming” already includes 2 actions: “decline” and “answer”, now + 1 action are 3
        assertEquals(3, actualNotification.actions.size)
        assertCommonData(expectedData, actualNotification)
    }

    private suspend fun showOrGenerateNotificationAccordingAPI(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //From API 31, CallStyle notifications must either be for a foreground Service
            notifyConfig.generateBuilder().build()
        } else {
            val notificationPair = notifyConfig.show()
            val actualStatusBarNotification =
                notificationManager.waitForNotification(notificationPair.first)
            actualStatusBarNotification.notification
        }
    }

    private fun assertCommonData(expectedData: Data.CallData, actualNotification: Notification) {
        val expectedCallType = mapOf(
            "incoming" to NotificationCompat.CallStyle.CALL_TYPE_INCOMING,
            "ongoing" to NotificationCompat.CallStyle.CALL_TYPE_ONGOING,
            "screening" to NotificationCompat.CallStyle.CALL_TYPE_SCREENING
        ).getOrDefault(expectedData.type, NotificationCompat.CallStyle.CALL_TYPE_INCOMING)
        val actualExtras = actualNotification.extras
        val actualCallType = actualExtras.getInt(NotificationCompat.EXTRA_CALL_TYPE)
        val actualNamePersonAdded = actualExtras.getString("android.title")
        val actualAnswer = if (expectedData.type in listOf("incoming", "screening"))
            actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_ANSWER_INTENT,
                PendingIntent::class.java
            )
        else null
        val actualDeclineHangup = when (expectedData.type) {
            "incoming" -> actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_DECLINE_INTENT,
                PendingIntent::class.java
            )

            "ongoing", "screening" -> actualExtras.getCustomParcelable(
                NotificationCompat.EXTRA_HANG_UP_INTENT,
                PendingIntent::class.java
            )

            else -> null
        }

        assertNotificationPriority(NotificationCompat.PRIORITY_HIGH, actualNotification)
        assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
        assertEquals(expectedCallType, actualCallType)
        assertEquals(expectedData.caller?.name, actualNamePersonAdded)
        assertEquals(expectedData.answer, actualAnswer)
        assertEquals(expectedData.declineOrHangup, actualDeclineHangup)
    }

}