package com.vanskarner.samplenotify.internal

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanskarner.samplenotify.Data
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

    private fun createBasicData(): Data.BasicData {
        val intent = Intent()
        val pendingIntent = PendingIntent.getBroadcast(
            ApplicationProvider.getApplicationContext(),
            123,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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

    private fun Icon.toBitmap(): Bitmap? {
        return when (type) {
            Icon.TYPE_BITMAP -> (loadDrawable(ApplicationProvider.getApplicationContext()) as? BitmapDrawable)?.bitmap
            else -> null
        }
    }

}