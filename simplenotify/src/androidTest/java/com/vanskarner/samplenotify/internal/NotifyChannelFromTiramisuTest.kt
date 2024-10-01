package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.simplenotify.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.TIRAMISU)
class NotifyChannelFromTiramisuTest {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var expectedChannel: ChannelData

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        expectedChannel = ChannelData.byDefault(appContext)

        notifyChannel = NotifyChannel
    }

    @Test
    fun applyChannelAndGetChannel_shouldHaveTheSameData() {
        notifyChannel.applyChannel(appContext, expectedChannel)
        val actualChannel = notifyChannel.getChannel(appContext, expectedChannel.id)

        assertNotNull(actualChannel)
        assertEquals(expectedChannel.id, actualChannel?.id)
        assertEquals(expectedChannel.name, actualChannel?.name)
        assertEquals(expectedChannel.summary, actualChannel?.description)
        assertEquals(expectedChannel.importance, actualChannel?.importance)
        assertEquals(expectedChannel.sound, actualChannel?.sound)
        assertEquals(expectedChannel.audioAttributes, actualChannel?.audioAttributes)
    }

    @Test
    fun deleteChannel_shouldBeNull() {
        notifyChannel.applyChannel(appContext, expectedChannel)
        notifyChannel.deleteChannel(appContext, expectedChannel.id)
        val actualChannel = notifyChannel.getChannel(appContext, expectedChannel.id)

        assertNull(actualChannel)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyChannel(appContext, expectedChannel)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        assertNotNull(notificationManager.activeNotifications.find { it.id == notificationId })
        notifyChannel.cancelNotification(appContext, notificationId)
        Espresso.onIdle()
        assertNull(notificationManager.activeNotifications.find { it.id == notificationId })
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyChannel(appContext, expectedChannel)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        assertNotNull(notificationManager.activeNotifications.find { it.id == notificationId })
        notifyChannel.cancelAllNotification(appContext)
        Espresso.onIdle()
        assertEquals(0, notificationManager.activeNotifications.size)
    }

}