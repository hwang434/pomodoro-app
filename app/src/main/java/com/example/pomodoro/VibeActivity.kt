package com.example.pomodoro

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.pomodoro.databinding.ActivityVibeBinding

class VibeActivity: AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityVibeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"VibeActivity - onCreate() called")
        binding = DataBindingUtil.setContentView(this,R.layout.activity_vibe)
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(2000,500)
        vibrator.vibrate(pattern,0)

        binding.finishBtn.setOnClickListener {
            this.finish()
        }
    }
}