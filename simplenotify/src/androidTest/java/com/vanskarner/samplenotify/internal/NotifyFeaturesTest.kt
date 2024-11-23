package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.common.TestDataProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyFeaturesTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var assignContent: NotifyFeatures
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext()
        builder = NotificationCompat.Builder(appContext, "test_channel")
        assignContent = NotifyFeatures
    }

    @Test
    fun applyExtras_apply() {
        val expectedExtraData = TestDataProvider.extraData()
        assignContent.applyExtras(expectedExtraData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualOngoing = notification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        val actualOnlyAlertOnce = notification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        val actualSubText = actualExtras.getString(NotificationCompat.EXTRA_SUB_TEXT)
        val actualShowWhen = actualExtras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        val actualUsesChronometer = NotificationCompat.getUsesChronometer(notification)
        val actualBadgeNumber = notification.number
        val actualRemoteInputHistory =
            actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_REMOTE_INPUT_HISTORY)
        val actualGroupKey = notification.group

        assertEquals(expectedExtraData.priority, notification.priority)
        assertEquals(expectedExtraData.category, notification.category)
        assertEquals(expectedExtraData.visibility, notification.visibility)
        assertEquals(expectedExtraData.ongoing, actualOngoing)
        assertEquals(expectedExtraData.color, notification.color)
        assertEquals(expectedExtraData.timestampWhen, notification.`when`)
        assertEquals(expectedExtraData.deleteIntent, notification.deleteIntent)
        assertEquals(expectedExtraData.fullScreenIntent?.first, notification.fullScreenIntent)
        assertEquals(expectedExtraData.onlyAlertOnce, actualOnlyAlertOnce)
        assertEquals(expectedExtraData.subText, actualSubText)
        assertEquals(expectedExtraData.showWhen, actualShowWhen)
        assertEquals(expectedExtraData.useChronometer, actualUsesChronometer)
        assertEquals(expectedExtraData.badgeNumber, actualBadgeNumber)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actualBadgeIconType = notification.badgeIconType
            val actualShortcutId = notification.shortcutId

            assertEquals(expectedExtraData.badgeIconType, actualBadgeIconType)
            assertEquals(expectedExtraData.shortCutId, actualShortcutId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val actualSystemGeneratedActions = notification.allowSystemGeneratedContextualActions
            assertEquals(
                expectedExtraData.allowSystemGeneratedContextualActions,
                actualSystemGeneratedActions
            )
        }
        assertEquals(expectedExtraData.remoteInputHistory?.size, actualRemoteInputHistory?.size)
        assertEquals(expectedExtraData.groupKey, actualGroupKey)
    }

    @Test
    fun applyAction_apply() {
        val expectedBasicAction = TestDataProvider.basicAction()
        val expectedReplyAction = TestDataProvider.replyAction()
        assignContent.applyAction(expectedBasicAction, builder)
        assignContent.applyAction(expectedReplyAction, builder)
        val notification = builder.build()
        val actualActions = notification.actions

        assertEquals(2, actualActions.size)
        assertEquals(expectedBasicAction.icon, actualActions[0].icon)
        assertEquals(expectedBasicAction.label, actualActions[0].title)
        assertEquals(expectedBasicAction.pending, actualActions[0].actionIntent)
        assertEquals(expectedReplyAction.icon, actualActions[1].icon)
        assertEquals(expectedReplyAction.label, actualActions[1].title)
        assertEquals(expectedReplyAction.replyPending, actualActions[1].actionIntent)
        assertEquals(
            expectedReplyAction.allowGeneratedReplies,
            actualActions[1].allowGeneratedReplies
        )
        assertNotNull(actualActions[1].remoteInputs[0])
    }

    @Test
    fun applyProgress_whenIsNotHide_apply() {
        val progressData = TestDataProvider.progressData(false)
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(progressData.currentValue, actualProgress)
        assertEquals(progressData.indeterminate, actualIndeterminate)
    }

    @Test
    fun applyProgress_whenIsHide_apply() {
        val progressData = TestDataProvider.progressData(true)
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

}