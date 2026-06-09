package io.iskopasi.kmpvpntest.managers

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import io.iskopasi.kmpvpntest.MainActivity
import io.iskopasi.kmpvpntest.R

const val ChannelId = "vpn_service"
const val NotificationId = 1

class NManager(
    application: Application
) {
    private val notifManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ChannelId,
                "VPN Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = Color.Red.value.toInt()
            }

            notifManager.createNotificationChannel(channel)
        }
    }

    private fun getServiceType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        else
            0
    }

    private fun createNotification(
        context: Context,
        contentText: String,
        channelId: String
    ): Notification {
        val notificationIntent =
            Intent(context.applicationContext, MainActivity::class.java) // Activity to open on tap
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            context.applicationContext, 0, notificationIntent, pendingIntentFlags
        )

        val appName = context.applicationContext.resources.getString(R.string.app_name)

        return NotificationCompat.Builder(context.applicationContext, channelId)
            .setContentTitle(appName)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Makes the notification non-dismissable by swipe
            .setSilent(true) // To avoid sound on initial display/updates if not desired
            .build()
    }

    fun startForeground(service: Service) {
        val notification = createNotification(
            context = service.applicationContext,
            contentText = "VPN is running...",
            channelId = ChannelId
        )
        val serviceType = getServiceType()

        try {
            ServiceCompat.startForeground(
                service,
                NotificationId,
                notification,
                serviceType
            )
        } catch (ex: Exception) {
            service.stopSelf()
        }
    }

    fun cancel() {
        notifManager.cancel(NotificationId)
    }
}