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
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitForNotification
import com.vanskarner.samplenotify.common.waitForEmptyListNotifications
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
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun with_usingBasicFormAndNoId_shouldBeShow() = runTest {
        val expectedNotificationId = SimpleNotify.with(context)
            .asBasic {
                title = "Any title"
                text = "Any text"
            }
            .show().first
        val statusBarNotification =
            notificationManager.waitForNotification(expectedNotificationId)
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
        val expectedNotificationId = 1
        SimpleNotify.with(context)
            .asBasic {
                id = expectedNotificationId
                title = "Any title"
                text = "Any text"
            }
            .show()
        val statusBarNotification =
            notificationManager.waitForNotification(expectedNotificationId)
        val actualNotification = statusBarNotification?.notification
        val actualTitle = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TITLE)
        val actualText = actualNotification?.extras?.getString(NotificationCompat.EXTRA_TEXT)

        assertNotNull(actualNotification)
        assertEquals(expectedNotificationId, statusBarNotification?.id)
        assertEquals("Any title", actualTitle)
        assertEquals("Any text", actualText)
    }

    @Test
    fun cancel_shouldBeCancel() = runTest {
        val expectedNotificationId = 2
        val notifyBuilder = TestDataProvider.basicNotification(context, "AnyId")
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        SimpleNotify.cancel(context, expectedNotificationId)
        assertEquals(0, notificationManager.waitForEmptyListNotifications().size)
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel() = runTest {
        val expectedNotificationId = 3
        val notifyBuilder = TestDataProvider.basicNotification(context, "AnyId")
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val statusBarNotification = notificationManager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification?.id)
        SimpleNotify.cancelAll(context)
        assertEquals(0, notificationManager.waitForEmptyListNotifications().size)
    }
}