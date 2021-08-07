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


class AlarmReceiver: BroadcastReceiver()  {
    private val TAG: String = "로그"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"AlarmReceiver - onReceive() called")
//        Toast.makeText(context, "AlarmReceiver onReceive called. intent.action : ${intent?.action}", Toast.LENGTH_LONG).show()
        val dialogBuilder = AlertDialog.Builder(context!!)
        dialogBuilder.setTitle("타이틀").setMessage("메시지").setPositiveButton("끄기", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(context,"postive button is clicked", Toast.LENGTH_LONG).show()
            val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
        })
        dialogBuilder.create().show()

        val serviceIntent = Intent(context, VibrationService::class.java)
        context.startService(serviceIntent)
    }
}