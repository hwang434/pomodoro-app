package com.example.pomodoro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import com.example.pomodoro.R
import com.example.pomodoro.service.VibrationService


class VibratorReceiver: BroadcastReceiver()  {
    private val TAG: String = "로그"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"AlarmReceiver - onReceive() called")

        val serviceIntent = Intent(context, VibrationService::class.java)
        context?.startService(serviceIntent)
    }
}