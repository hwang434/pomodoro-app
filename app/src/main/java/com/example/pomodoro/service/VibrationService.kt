package com.example.pomodoro.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.example.pomodoro.R

class VibrationService: Service(){
    private val TAG: String = "로그"
    private lateinit var vibrator: Vibrator
    private lateinit var musicPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"AlarmService - onStartCommand() called")
        Toast.makeText(this, "시간이 다했어요~", Toast.LENGTH_LONG).show()

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        musicPlayer = MediaPlayer.create(this, R.raw.ppippi)
        musicPlayer.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vibrator.vibrate(1500,
                AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_ALARM).
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
                build())
        }

        return super.onStartCommand(intent, flags, startId)
    }
}