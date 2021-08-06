package com.example.pomodoro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmReceiver: BroadcastReceiver()  {
    private val TAG: String = "로그"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"AlarmReceiver - onReceive() called")
    }
}