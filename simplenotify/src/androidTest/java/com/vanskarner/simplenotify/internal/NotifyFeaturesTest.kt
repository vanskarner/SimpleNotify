package com.vanskarner.simplenotify.internal

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationPriority
import com.vanskarner.simplenotify.common.waitForAllNotificationsPresents
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyFeaturesTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notifyFeatures: NotifyFeatures
    private lateinit var appContext: Context

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext()
        builder = NotificationCompat.Builder(appContext, "test_channel")
        notifyFeatures = NotifyFeatures
    }

    @Test
    fun applyExtras_apply() {
        val expectedExtraData = TestDataProvider.extraData()
        notifyFeatures.applyExtras(expectedExtraData, builder)
        val actualNotification = builder.build()
        val expectedPriority = expectedExtraData.priority ?: -666
        val actualExtras = actualNotification.extras
        val actualOngoing = actualNotification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        val actualOnlyAlertOnce =
            actualNotification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        val actualSubText = actualExtras.getString(NotificationCompat.EXTRA_SUB_TEXT)
        val actualShowWhen = actualExtras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        val actualUsesChronometer = NotificationCompat.getUsesChronometer(actualNotification)
        val actualBadgeNumber = actualNotification.number
        val actualRemoteInputHistory =
            actualExtras.getCharSequenceArray(NotificationCompat.EXTRA_REMOTE_INPUT_HISTORY)
        val actualGroupKey = actualNotification.group

        assertNotificationPriority(expectedPriority, actualNotification)
        assertEquals(expectedExtraData.category, actualNotification.category)
        assertEquals(expectedExtraData.visibility, actualNotification.visibility)
        assertEquals(expectedExtraData.ongoing, actualOngoing)
        assertEquals(expectedExtraData.color, actualNotification.color)
        assertEquals(expectedExtraData.timestampWhen, actualNotification.`when`)
        assertEquals(expectedExtraData.deleteIntent, actualNotification.deleteIntent)
        assertEquals(expectedExtraData.fullScreenIntent?.first, actualNotification.fullScreenIntent)
        assertEquals(expectedExtraData.onlyAlertOnce, actualOnlyAlertOnce)
        assertEquals(expectedExtraData.subText, actualSubText)
        assertEquals(expectedExtraData.showWhen, actualShowWhen)
        assertEquals(expectedExtraData.useChronometer, actualUsesChronometer)
        assertEquals(expectedExtraData.badgeNumber, actualBadgeNumber)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actualBadgeIconType = actualNotification.badgeIconType
            val actualShortcutId = actualNotification.shortcutId

            assertEquals(expectedExtraData.badgeIconType, actualBadgeIconType)
            assertEquals(expectedExtraData.shortCutId, actualShortcutId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val actualSystemGeneratedActions =
                actualNotification.allowSystemGeneratedContextualActions
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
        notifyFeatures.applyAction(expectedBasicAction, builder)
        notifyFeatures.applyAction(expectedReplyAction, builder)
        val actualNotification = builder.build()
        val actualActions = actualNotification.actions

        assertEquals(2, actualActions.size)
        assertEquals(expectedBasicAction.icon, actualActions[0].getIcon().resId)
        assertEquals(expectedBasicAction.title, actualActions[0].title)
        assertEquals(expectedBasicAction.pending, actualActions[0].actionIntent)
        assertEquals(expectedReplyAction.icon, actualActions[1].getIcon().resId)
        assertEquals(expectedReplyAction.title, actualActions[1].title)
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
        notifyFeatures.applyProgress(progressData, builder)
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
        notifyFeatures.applyProgress(progressData, builder)
        val notification = builder.build()
        val actualExtras = notification.extras
        val actualProgress = actualExtras.getInt(NotificationCompat.EXTRA_PROGRESS)
        val actualIndeterminate =
            actualExtras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)

        assertEquals(0, actualProgress)
        assertFalse(actualIndeterminate)
    }

    @Test
    fun getGroupStackable_whenNoGroupNotificationsAreActive_shouldBeAnEmptyList() {
        val stackableData = TestDataProvider.stackableData()
        val extraData = TestDataProvider.extraData()
        val notifyChannel = NotifyChannel
        val groupStackable = notifyFeatures
            .getGroupStackable(appContext, stackableData, extraData, notifyChannel)

        assertTrue(groupStackable.isEmpty())
    }

    @Test
    fun getGroupStackable_whenGroupNotificationsExist_shouldContainItems() = runTest {
        val stackableData = TestDataProvider.stackableData()
        val extraData = TestDataProvider.extraData()
        val notifyChannel = NotifyChannel
        stackableData.initialAmount = 3
        extraData.groupKey = "Test_Group_Key"
        waitForActiveNotificationsGroup(extraData.groupKey!!, stackableData.initialAmount)
        val groupStackable = notifyFeatures
            .getGroupStackable(appContext, stackableData, extraData, notifyChannel)
        val groupNotification = groupStackable.last().second
        val actualGroupTitle = groupNotification.extras.getString(NotificationCompat.EXTRA_TITLE)
        val actualSummaryText =
            groupNotification.extras.getString(NotificationCompat.EXTRA_SUMMARY_TEXT)

        assertFalse(groupStackable.isEmpty())
        //Normal notifications + group notifications
        assertEquals(stackableData.initialAmount + 1, groupStackable.size)
        assertEquals(stackableData.title, actualGroupTitle)
        assertEquals(stackableData.summaryText, actualSummaryText)
    }

    private suspend fun waitForActiveNotificationsGroup(
        group: String,
        numberNotifications: Int = 4
    ) {
        val manager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val testChannelId = TestDataProvider.createChannel(manager)
        val notificationIds = mutableSetOf<Int>()
        repeat(numberNotifications) { count ->
            val notificationCompat = TestDataProvider.basicNotification(appContext, testChannelId)
                .setGroup(group)
                .build()
            val notificationId = count + 200
            notificationIds.add(notificationId)
            manager.notify(notificationId, notificationCompat)
        }
        manager.waitForAllNotificationsPresents(notificationIds)
    }

}