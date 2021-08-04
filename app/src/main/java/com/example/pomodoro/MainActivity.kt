package com.example.pomodoro

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.viewmodels.TimerViewModel
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TimerViewModel
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //어플리케이션 실행 동안, 화면 계속 켜Timer두게 설정.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //뷰모델 초기화ㅣ
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        //onDestroy하고 다시 onCreate하면 바로 화면 시간 나옴.
        binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)

        makeTimer()

        // 리스너.
        // startBtn을 눌렀을 때, start메서드 실행.
        binding.startBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - startBtn is clicked.")
            binding.startBtn.visibility = View.GONE
            binding.pauseBtn.visibility = View.VISIBLE
            binding.pomodoroStatus.text = "공부중"

            startTimer()
            binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)
        }
        // 정지 버튼 눌렀을 때, 처음 시간으로 초기화.
        binding.stopBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - stopBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE
            binding.pomodoroStatus.text = "아무것도 안함"

            stopTimer()
            binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)
        }

        //일시 정지 버튼 눌렀을 때, 타이머 중지하고 재개하면 시간 이어서쭉
        binding.pauseBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - pauseBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE
            binding.pomodoroStatus.text = "아무것도 안함"

            pauseTimer()
            binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"MainActivity - onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"MainActivity - onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"MainActivity - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"MainActivity - onStop() called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG,"MainActivity - onRestart() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"MainActivity - onDestroy() called")
        pauseTimer()
    }

    fun makeTimer() {
        Log.d(TAG,"MainActivity - makeTimer() called\nremainTime : ${viewModel.remainTime}")
        timer = object: CountDownTimer(viewModel.remainTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"MainActivity - onTick() called time : ${viewModel.remainTime}")
                binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)
                viewModel.remainTime -= 1000
            }

            override fun onFinish() {
                Log.d(TAG,"MainActivity - onFinish() called")
                val intent: Intent = Intent(applicationContext, VibeActivity::class.java)
                startActivity(intent)

                //공부 시간이 끝났으니, 휴식 시간이 남은 시간이 됨.
                if (viewModel.isStudyTime) {
                    viewModel.remainTime = viewModel.breakLength
                    binding.pomodoroStatus.text = "휴식중"
                } else {
                    viewModel.remainTime = viewModel.studyLength
                    binding.pomodoroStatus.text = "공부중"
                }
                viewModel.isStudyTime = !viewModel.isStudyTime

                binding.timeView.text = makeMilSecToMinSec(viewModel.remainTime)
                binding.pauseBtn.visibility = View.GONE
                binding.startBtn.visibility = View.VISIBLE
            }
        }
    }
    //타이머 관련 기능들.
    fun startTimer() {
        Log.d(TAG,"MainActivity - startTimer() called")
        makeTimer()
        timer.start()
    }

    private fun stopTimer() {
        Log.d(TAG,"MainActivity - stopTimer() called")
        timer?.cancel()

        if (viewModel.isStudyTime) {
            viewModel.remainTime = viewModel.studyLength
            binding.timeView.text = makeMilSecToMinSec(viewModel.studyLength)
        } else {
            viewModel.remainTime = viewModel.breakLength
            binding.timeView.text = makeMilSecToMinSec(viewModel.breakLength)
        }
    }

    fun pauseTimer() {
        Log.d(TAG,"MainActivity - pauseTimer() called")
        timer?.cancel()
    }

    //시간을 디스프레이에 보여줄 형식을 만들어주는 메소드.
    //mm:ss 형식임.
    fun makeMilSecToMinSec(time: Long): String {
        val timeFormated = StringBuilder()
        var min = time/1000/60
        var sec = (time % (1000*60)) / 1000

        //0~9분이면 0 앞에 붙여서 0m:ss 처리.
        if (min < 10) timeFormated.append(0)
        timeFormated.append(min)
        timeFormated.append(":")
        //0~9초면 0 앞에 붙여서 mm:0s 처리.
        if (sec < 10) timeFormated.append(0)
        timeFormated.append(sec)

        return timeFormated.toString()
    }
}