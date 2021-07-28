package com.example.pomodoro

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import com.example.pomodoro.databinding.ActivityMainBinding

//val intent = Intent(applicationContext, VibeActivity::class.java)
//startActivity(intent)
class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null

    companion object {
        //공부 시간.
        var studyLength: Long = 11*1000
        //쉬는 시간.
        var breakLength: Long = 6*1000

        //휴식 시간 여부.
        var isStudyTime: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //어플리케이션 실행 동안, 화면 계속 켜두게 설정.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 리스너.
        // startBtn을 눌렀을 때, start메서드 실행.
        binding.startBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - startBtn is clicked.")
            startTimer(timer)
            binding.timeView.text = ((studyLength-1000)/1000).toString()
        }
    }

    fun startTimer(timer: CountDownTimer?) {
        Log.d(TAG,"MainActivity - startTimer() called")
        var remainTime = studyLength
        var timer = object: CountDownTimer(studyLength, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"MainActivity - onTick() called")
                binding.timeView.text = ((remainTime-1000)/1000).toString()
                remainTime -= 1000
            }

            override fun onFinish() {
                Log.d(TAG,"MainActivity - onFinish() called")
                val intent: Intent = Intent(applicationContext, VibeActivity::class.java)
                startActivity(intent)

                var temp = studyLength
                studyLength = breakLength
                breakLength = temp
                binding.timeView.text = ((studyLength-1000)/1000).toString()
            }
        }
        timer.start()
    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.options_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
//    }
}