package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.simplenotify.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyGeneratorChannelTest {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var notificationManager: NotificationManager
    private lateinit var expectedChannel: ChannelData

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        expectedChannel = ChannelData.byDefault(appContext)

        notifyChannel = NotifyChannel(appContext)
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun applyChannelAndGetChannel_inAPIVersionsFrom8_shouldHaveTheSameData() {
        notifyChannel.applyChannel(expectedChannel)
        val actualChannel = notifyChannel.getChannel(expectedChannel.id)

        assertNotNull(actualChannel)
        assertEquals(expectedChannel.id, actualChannel?.id)
        assertEquals(expectedChannel.name, actualChannel?.name)
        assertEquals(expectedChannel.summary, actualChannel?.description)
        assertEquals(expectedChannel.importance, actualChannel?.importance)
    }

    @Test
    @SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N)
    fun applyChannelAndGetChannel_inApiVersionsOlderThan26_shouldBeNull() {
        notifyChannel.applyChannel(expectedChannel)
        val actualChannel = notifyChannel.getChannel(expectedChannel.id)

        assertNull(actualChannel)
    }

    @Test
    fun deleteChannel_inAPIVersionsFrom8_shouldBeNull() {
        notifyChannel.applyChannel(expectedChannel)
        notifyChannel.deleteChannel(expectedChannel.id)
        val actualChannel = notifyChannel.getChannel(expectedChannel.id)

        assertNull(actualChannel)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() {
        val channelId = notifyChannel.applyChannel(expectedChannel)
        val notificationId = 123
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val notifyBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        assertNotNull(notificationManager.activeNotifications.find { it.id == notificationId })
        notifyChannel.cancelNotification(notificationId)
        Espresso.onIdle()
        assertNull(notificationManager.activeNotifications.find { it.id == notificationId })
    }

}