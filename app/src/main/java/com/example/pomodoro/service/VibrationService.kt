package com.example.pomodoro.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import com.example.pomodoro.R

class VibrationService: Service(){
    private val TAG: String = "로그"
    private lateinit var vibrator: Vibrator
    private lateinit var musicPlayer: MediaPlayer

    override fun onCreate() {
        Log.d(TAG,"VibrationService - onCreate() called")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrateManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibrateManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        musicPlayer = MediaPlayer.create(this, R.raw.ppippi)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"VibrationService - onBind() called")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"VibrationService - onStartCommand() called")
        musicPlayer.start()

        vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500), -1),
            AudioAttributes.Builder().
            setUsage(AudioAttributes.USAGE_ALARM).
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
            build()
        )

        return super.onStartCommand(intent, flags, startId)
    }
}