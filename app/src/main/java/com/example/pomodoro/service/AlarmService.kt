package com.example.pomodoro.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AlarmService: Service(){
    private val TAG: String = "로그"
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"AlarmService - onStartCommand() called")
        return super.onStartCommand(intent, flags, startId)
    }
}