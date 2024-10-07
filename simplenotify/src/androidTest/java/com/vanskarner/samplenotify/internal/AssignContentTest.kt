package com.vanskarner.samplenotify.internal

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data
import com.vanskarner.samplenotify.ExtraData
import com.vanskarner.samplenotify.ProgressData
import com.vanskarner.simplenotify.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssignContentTest {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var assignContent: AssignContent

    @Before
    fun setUp() {
        builder = NotificationCompat
            .Builder(ApplicationProvider.getApplicationContext(), "test_channel")
        assignContent = AssignContent
    }

    @Test
    fun applyData_apply() {
        val basicData = createBasicData()
        assignContent.applyData(basicData, builder)
        val notification = builder.build()

        assertEquals(basicData.title, notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals(basicData.text, notification.extras.getString(NotificationCompat.EXTRA_TEXT))
        assertEquals(basicData.smallIcon, notification.smallIcon.resId)
        @Suppress("DEPRECATION")
        val largeIcon = notification.extras.getParcelable<Icon>(NotificationCompat.EXTRA_LARGE_ICON)
        assertTrue(basicData.largeIcon!!.sameAs(largeIcon?.toBitmap()))
        @Suppress("DEPRECATION")
        assertEquals(basicData.priority, notification.priority)
        assertEquals(basicData.pending, notification.contentIntent)
        assertEquals(
            basicData.autoCancel,
            notification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0
        )
    }

    @Test
    fun applyExtras_apply() {
        val extraData = createExtraData()
        assignContent.applyExtras(extraData, builder)
        val notification = builder.build()

        assertEquals(extraData.category, notification.category)
        assertEquals(extraData.visibility, notification.visibility)
        assertEquals(extraData.color, notification.color)
        assertEquals(
            extraData.ongoing,
            notification.flags and NotificationCompat.FLAG_ONGOING_EVENT != 0
        )
        assertEquals(extraData.timestampWhen, notification.`when`)
        assertEquals(extraData.deleteIntent, notification.deleteIntent)
        assertEquals(extraData.fullScreenIntent?.first, notification.fullScreenIntent)
        assertEquals(
            extraData.onlyAlertOnce,
            notification.flags and Notification.FLAG_ONLY_ALERT_ONCE != 0
        )
        assertEquals(
            extraData.subText,
            notification.extras.getString(NotificationCompat.EXTRA_SUB_TEXT)
        )
        assertEquals(
            extraData.showWhen,
            notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN)
        )
        assertEquals(extraData.useChronometer, NotificationCompat.getUsesChronometer(notification))
    }

    @Test
    @Suppress("DEPRECATION")
    fun applyAction_apply() {
        val basicActionData = ActionData.BasicAction(
            icon = R.drawable.baseline_notifications_24,
            label = "Any Label",
            pending = createPendingIntent()
        )
        val replyAction = ActionData.ReplyAction(
            icon = R.drawable.baseline_notifications_24,
            label = "Any Label",
            replyPending = createPendingIntent(),
            remote = RemoteInput.Builder("any_key").build()
        )
        assignContent.applyAction(basicActionData, builder)
        assignContent.applyAction(replyAction, builder)
        val notification = builder.build()

        assertEquals(2, notification.actions.size)
        assertEquals(basicActionData.icon, notification.actions[0].icon)
        assertEquals(basicActionData.label, notification.actions[0].title)
        assertEquals(basicActionData.pending, notification.actions[0].actionIntent)
        assertEquals(replyAction.icon, notification.actions[1].icon)
        assertEquals(replyAction.label, notification.actions[1].title)
        assertEquals(replyAction.replyPending, notification.actions[1].actionIntent)
        assertNotNull(notification.actions[1].remoteInputs[0])
    }

    @Test
    fun applyProgress_whenIsNotHide_apply() {
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = false
        )
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()

        assertEquals(
            progressData.currentValue,
            notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS)
        )
        assertEquals(
            progressData.indeterminate,
            notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE)
        )
    }

    @Test
    fun applyProgress_whenIsHide_apply() {
        val progressData = ProgressData(
            currentValue = 50,
            indeterminate = true,
            hide = true
        )
        assignContent.applyProgress(progressData, builder)
        val notification = builder.build()

        assertEquals(0, notification.extras.getInt(NotificationCompat.EXTRA_PROGRESS))
        assertFalse(notification.extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE))
    }

    private fun createExtraData() = ExtraData(
        category = NotificationCompat.CATEGORY_MESSAGE,
        visibility = NotificationCompat.VISIBILITY_PRIVATE,
//        vibrationPattern = longArrayOf(0, 500, 1000, 500),
//        lights = Triple(Color.GREEN, 1000, 1000),
        ongoing = true,
        color = Color.GRAY,
//        timeoutAfter = 1000,
//        badgeIconType = 12,
        timestampWhen = 1500,
        deleteIntent = createPendingIntent(),
        fullScreenIntent = Pair(createPendingIntent()!!, true),
        onlyAlertOnce = true,
        subText = "Any SubText",
        showWhen = true,
        useChronometer = true
    )

    private fun createBasicData(): Data.BasicData {
        val pendingIntent = createPendingIntent()
        val basicData = Data.BasicData().apply {
            title = "Any title"
            text = "Any text"
            smallIcon = R.drawable.baseline_notifications_24
            largeIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            priority = NotificationCompat.PRIORITY_DEFAULT
            pending = pendingIntent
            autoCancel = true
        }
        return basicData
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent()
        val pendingIntent = PendingIntent.getBroadcast(
            ApplicationProvider.getApplicationContext(),
            123,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    private fun Icon.toBitmap(): Bitmap? {
        return when (type) {
            Icon.TYPE_BITMAP -> (loadDrawable(ApplicationProvider.getApplicationContext()) as? BitmapDrawable)?.bitmap
            else -> null
        }
    }

}