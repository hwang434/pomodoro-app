package com.example.pomodoro.service

import android.app.*
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pomodoro.R
import com.example.pomodoro.util.LongToTime
import com.example.pomodoro.views.MainActivity

class AlarmService : Service() {
    companion object {
        private const val TAG: String = "로그"
        private const val TIME_CAST = "com.example.pomodoro.TICK"
        private const val NOTIFICATION_ID = 2
    }
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notification: Notification

    override fun onCreate() {
        Log.d(TAG,"AlarmService - onCreate() called")
        super.onCreate()
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"AlarmService - onStartCommand() called")
        Log.d(TAG,"AlarmService - intent : ${intent?.getLongExtra("time", 10000)}")
        startCountDown(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG,"AlarmService - onDestroy() called")
        super.onDestroy()
        countDownTimer.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"AlarmService - onBind() called")
        return null
    }

    private fun createNotificationBuilder() {
        notificationBuilder = NotificationCompat.Builder(this, TIME_CAST)
            .setTicker("ticker")
            .setContentTitle("제목 : 뽀모도로")
            .setContentText("내용 : 시간")
            .setSilent(true)
    }

    private fun createNotificationManager() {
        notificationManager = this.getSystemService(NotificationManager::class.java)
    }

    private fun createNotification() {
        createNotificationBuilder()
        createNotificationManager()

        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val serviceChannel = NotificationChannel(TIME_CAST, "alarm channel name", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(serviceChannel)

        notification = notificationBuilder
            .setContentTitle("타이머")
            .setContentText("00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("ticker")
            .build()
    }



    private fun startCountDown(intent: Intent?) {
        Log.d(TAG,"AlarmService - startCountDown() called")
        Log.d(TAG,"AlarmService - intent.getLongExtra : ${intent?.getLongExtra("time", 10000)}")
        startForeground(NOTIFICATION_ID, notification)

        var time = intent?.getLongExtra("time", 1000000) ?: throw Exception("시간이 null임")
        countDownTimer = object: CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"AlarmService - onTick() called")
                val broadcastIntent = Intent(TIME_CAST)
                time -= 1000
                broadcastIntent.putExtra("time", time)
                sendBroadcast(broadcastIntent)

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.setContentText(LongToTime.makeMilSecToMinSec(time)).build())
            }

            override fun onFinish() {
                Log.d(TAG,"AlarmService - onFinish() called")
            }
        }.start()
    }
}