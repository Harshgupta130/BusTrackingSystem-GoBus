package com.example.bustrackingsystem

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class ForegroundService:Service(){
    private val CHANNEL_ID="ForegroundService kotlin"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle: Bundle? = intent?.extras

        val locationName:String=bundle?.get("InputExtra").toString()
        val riding=bundle?.get("riding").toString()
        Log.d("1",riding)
        Log.d("extra",locationName.toString())
        createNotificationChannel()
        val notificationIntent=Intent(this,ForegroundService::class.java)

        val resultIntent = Intent(this, DriverMainPage::class.java)
// Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification: Notification = Notification.Builder(this,CHANNEL_ID)
            .setContentTitle("Go Bus")
            .setContentText(riding+"in "+locationName)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(resultPendingIntent)
            .build()




        startForeground(1,notification)

        return START_STICKY
    }

private val d=DriverMainPage()
    override fun onDestroy() {
//        stopSelf()

        super.onDestroy()



    }
    override fun onBind(p0: Intent?): IBinder? {


        return null
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(notificationChannel)
        }
    }
}