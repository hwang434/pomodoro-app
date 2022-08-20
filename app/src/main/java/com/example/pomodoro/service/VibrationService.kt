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
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var vibrator: Vibrator
    private lateinit var musicPlayer: MediaPlayer

    override fun onCreate() {
        Log.d(TAG,"VibrationService - onCreate() called")
        super.onCreate()
        createVibrator()
        createMediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"VibrationService - onStartCommand() called")
        startMusicMedia()
        startVibrate()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG,"VibrationService - onBind() called")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG,"VibrationService - onDestroy() called")
        super.onDestroy()
        stopVibrate()
        stopMusicMedia()
    }

    private fun stopMusicMedia() {
        Log.d(TAG,"VibrationService - stopMusicMedia() called")
        musicPlayer.stop()
    }

    private fun stopVibrate() {
        Log.d(TAG,"VibrationService - stopVibrate() called")
        vibrator.cancel()
    }

    private fun createVibrator() {
        Log.d(TAG,"VibrationService - createVibrator() called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrateManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibrateManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun createMediaPlayer() {
        Log.d(TAG,"VibrationService - createMediaPlayer() called")
        musicPlayer = MediaPlayer.create(this, R.raw.ppippi)
    }

    private fun startVibrate() {
        Log.d(TAG,"VibrationService - startVibrate() called")
        vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500), -1),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
    }

    private fun startMusicMedia() {
        musicPlayer.start()
    }
}