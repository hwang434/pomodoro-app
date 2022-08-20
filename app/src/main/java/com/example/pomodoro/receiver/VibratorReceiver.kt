package com.example.pomodoro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pomodoro.service.VibrationService


class VibratorReceiver: BroadcastReceiver()  {
    companion object {
        private const val TAG: String = "로그"
        const val VIBRATOR_CAST = "com.example.pomodoro.ALARM_START"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"VibratorReceiver - onReceive() called")
        val serviceIntent = Intent(context, VibrationService::class.java)
        context?.startService(serviceIntent)
    }
}