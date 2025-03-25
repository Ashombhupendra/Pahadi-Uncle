package com.pahadi.uncle.presentation.notifications

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.MainActivity
import me.leolin.shortcutbadger.ShortcutBadger


class NotificationService : FirebaseMessagingService() {
    val TAG = "Service"
    var badgeCount = 1
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
        get() {
            return sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }
}

override fun onNewToken(newToken: String) {
    super.onNewToken(newToken)
    SharedPrefHelper.stoken = newToken
    //showNotification("PahadiUncle", "Message from Admin ")

}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val notificationId: Long = System.currentTimeMillis()
        Log.d("Notification 11", "From: " + remoteMessage.data.toString())
        Log.d("Notification 11", "Notification Message Body: " + remoteMessage.notification.toString())

        // Second case when notification payload is
        // received.
        if (SharedPrefHelper.isLoggedIn) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



            badgeCount++
            showNotification("PahadiUncle", remoteMessage.getData().get("message").toString())
            Log.d("notification11",remoteMessage.data.toString())

            //2021-08-04 10:38:53.333 9682-26024/com.pahadi.uncle D/notification11: {type=Chat, message=Someone Message You}
        //{type=other, message=hello}
            if (remoteMessage.getData().get("type").equals("other")){
                SharedPrefHelper.setNotificationAlert("ALERT")

            }

        }

    }




    // Method to display the notifications
    // Method to display the notifications
    fun showNotification(
        title: String?,
        message: String?
    ) {

        val intent = Intent(this, MainActivity::class.java)
         intent.putExtra("notificationsss","1")

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


     //   val contentView = RemoteViews("com.pahadi.uncle", com.pahadi.uncle.R.layout.fragment_notification_view)

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)

            notificationChannel.apply {
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(notificationChannel)



            builder = Notification.Builder(this, channelId)

                .setSmallIcon(com.pahadi.uncle.R.mipmap.pahadi_logo)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, com.pahadi.uncle.R.mipmap.pahadi_logo))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)


                .setContentIntent(pendingIntent)
        } else {
                builder = Notification.Builder(this)
               // .setContentIntent(pendingIntent)
                .setSmallIcon(com.pahadi.uncle.R.mipmap.pahadi_logo)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, com.pahadi.uncle.R.mipmap.pahadi_logo))
                .setContentTitle(title)
                .setContentText(message)
                    .setAutoCancel(true)

        }

        val notificationId: Long = System.currentTimeMillis()
        notificationManager.notify(notificationId.toInt(), builder.build())
    }


}