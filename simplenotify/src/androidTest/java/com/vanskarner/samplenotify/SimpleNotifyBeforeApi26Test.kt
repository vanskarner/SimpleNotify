package com.vanskarner.samplenotify

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.waitActiveNotifications
import com.vanskarner.simplenotify.test.R
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N_MR1)
class SimpleNotifyBeforeApi26Test {
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun with_usingBasicFormAndNoId_shouldBeShow() = runTest {
        val expectedNotificationId = SimpleNotify.with(appContext)
            .asBasic {
                title = "Any title"
                text = "Any text"
            }
            .show()
        val statusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
        val actualNotification = statusBarNotification?.notification
        val actualTitle = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TITLE)
        val actualText = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TEXT)

        assertNotNull(actualNotification)
        assertEquals(expectedNotificationId, statusBarNotification?.id)
        assertEquals("Any title", actualTitle)
        assertEquals("Any text", actualText)
    }

    @Test
    fun with_usingBasicFormAndWithId_shouldBeShow() = runTest {
        SimpleNotify.with(appContext)
            .asBasic {
                id = 123
                title = "Any title"
                text = "Any text"
            }
            .show()
        val statusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()
        val actualNotification = statusBarNotification?.notification
        val actualTitle = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TITLE)
        val actualText = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TEXT)

        assertNotNull(actualNotification)
        assertEquals(123, statusBarNotification?.id)
        assertEquals("Any title", actualTitle)
        assertEquals("Any text", actualText)
    }

    @Test
    fun cancel_shouldBeCancel() = runTest {
        val expectedNotificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, "anyId")
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification =
            notificationManager.waitActiveNotifications(1).first()

        assertEquals(expectedNotificationId, statusBarNotification.id)
        SimpleNotify.cancel(appContext, expectedNotificationId)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel()= runTest {
        val expectedNotificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, "anyId")
            .setSmallIcon(R.drawable.test_ic_notification_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification =
            notificationManager.waitActiveNotifications(1).first()

        assertEquals(expectedNotificationId, statusBarNotification.id)
        SimpleNotify.cancelAll(appContext)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }
}