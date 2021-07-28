package com.example.pomodoro

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import com.example.pomodoro.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null
    private var isStudyTime: Boolean = true

    companion object {
        //공부 시간.
        var studyLength: Long = 25*60*1000+1000
        //쉬는 시간.
        var breakLength: Long = 5*60*1000+1000
        //현재 타이머의 남은 시간
        var remainTime: Long = studyLength

        //휴식 시간 여부.
        var isStudyTime: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //어플리케이션 실행 동안, 화면 계속 켜Timer두게 설정.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //시간 형식
        val dateFormat = SimpleDateFormat("mm:ss")

        // 리스너.
        // startBtn을 눌렀을 때, start메서드 실행.
        binding.startBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - startBtn is clicked.")
            binding.startBtn.visibility = View.GONE
            binding.pauseBtn.visibility = View.VISIBLE

            timer = startTimer()
            binding.timeView.text = makeMilSecToMinSec(remainTime-1000)
        }
        // 정지 버튼 눌렀을 때, 처음 시간으로 초기화.
        binding.stopBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - stopBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE

            timer?.apply {
                stopTimer(this)
            }

            if (isStudyTime) {
                binding.timeView.text = makeMilSecToMinSec(studyLength-1000)
            } else {
                binding.timeView.text = makeMilSecToMinSec(breakLength-1000)
            }
        }

        //일시 정지 버튼 눌렀을 때, 타이머 중지하고 재개하면 시간 이어서쭉
        binding.pauseBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - pauseBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE

            timer?.apply {
                this.cancel()
            }
        }
    }

    private fun stopTimer(timer: CountDownTimer?) {
        Log.d(TAG,"MainActivity - stopTimer() called")
        timer!!.cancel()

        if (isStudyTime) {
            remainTime = studyLength
        } else {
            remainTime = breakLength
        }
    }

    fun startTimer(): CountDownTimer {
        Log.d(TAG,"MainActivity - startTimer() called")
        if (isStudyTime) binding.pomodoroStatus.text = "공부중"
        else binding.pomodoroStatus.text = "휴식중"

        var timer = object: CountDownTimer(remainTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"MainActivity - onTick() called")
                binding.timeView.text = makeMilSecToMinSec(remainTime-1000)
                remainTime -= 1000
            }

            override fun onFinish() {
                Log.d(TAG,"MainActivity - onFinish() called")
                val intent: Intent = Intent(applicationContext, VibeActivity::class.java)
                startActivity(intent)

                //공부 시간이 끝났으니, 휴식 시간이 남은 시간이 됨.
                if (isStudyTime) {
                    remainTime = breakLength
                    binding.pomodoroStatus.text = "휴식중"
                } else {
                    remainTime = studyLength
                    binding.pomodoroStatus.text = "공부중"
                }
                isStudyTime = !isStudyTime

                binding.timeView.text = makeMilSecToMinSec(remainTime-1000)
                binding.pauseBtn.visibility = View.GONE
                binding.startBtn.visibility = View.VISIBLE
            }
        }
        timer.start()
        return timer
    }
    fun makeMilSecToMinSec(time: Long): String {
        val timeFormated = StringBuilder()
        var min = time/1000/60
        var sec = (time % (1000*60)) / 1000

        if (min < 10) {
            timeFormated.append(0)
        }
        timeFormated.append(min)

        timeFormated.append(":")

        if (sec < 10) {
            timeFormated.append(0)
        }
        timeFormated.append(sec)

        return timeFormated.toString()
    }
}