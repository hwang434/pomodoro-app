package com.example.pomodoro.views

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pomodoro.R
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.receiver.AlarmReceiver
import com.example.pomodoro.viewmodels.TimerViewModel
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TimerViewModel
    // 알람 인텐트
    private lateinit var mAlarmIntet: Intent
    // 알람 매니저
    private lateinit var alarmManager: AlarmManager
    // PendingIntent
    private lateinit var mPendingIntent: PendingIntent
    // 리시버 보낼 인텐트 주소.
    private val BROADCAST = "com.example.pomodoro.ALARM_START"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // 어플리케이션 실행하는 동안 화면 계속 켜지게함.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 뷰모델 초기화
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        // binding의 뷰모델 설정하기
        binding.timerViewModel = viewModel
        binding.lifecycleOwner = this

        // 동적으로 리시버 등록하기.
        val myReceiver: AlarmReceiver = AlarmReceiver()
        val intentFilter = IntentFilter(BROADCAST)
        registerReceiver(myReceiver, intentFilter)

        // 리시버에 요청할 알람 인텐트 정의
        mAlarmIntet = Intent(BROADCAST)
        mPendingIntent = PendingIntent.getBroadcast(this,
            0,
            mAlarmIntet,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 공부 중인지 관찰자 설정
        viewModel.isStudyTime.observe(this, Observer { isStudyTime ->
            Log.d(TAG,"isStudyTime changed to ${viewModel.isStudyTime.value}")
            if (isStudyTime) {
                binding.pomodoroStatus.text = "공부중"
                binding.studyBreakSwitch.isChecked = false
            }
            else {
                binding.pomodoroStatus.text = "휴식중"
                binding.studyBreakSwitch.isChecked = true
            }
            Log.d(TAG,"MainActivity - isSelected : ${binding.studyBreakSwitch.isChecked}")
        })
        // 타이머가 돌아가고 있는지 관찰자 설정
        viewModel.isTimerRunning.observe(this, Observer { isTimerRunning ->
            Log.d(TAG,"MainActivity - isTimerRunning observed : ${isTimerRunning}")
            // 타이머가 실행중o이면  타이머 시작버튼 안보이고 일시정지 버튼 보임.
            // 타이머가 실행중x이면  타이머 일시정지 보이고   시작버튼 보임.
            if (isTimerRunning) {
                binding.startBtn.visibility = View.INVISIBLE
                binding.pauseBtn.visibility = View.VISIBLE
            } else {
                binding.startBtn.visibility = View.VISIBLE
                binding.pauseBtn.visibility = View.INVISIBLE
            }
        })

        //  observeForever 설정.
        viewModel.timeString.observeForever(Observer { it ->
            binding.timeView.text = it
        })

        //
        viewModel.remainTime.observeForever(Observer { it ->
            viewModel.makeMilSecToMinSec(it)
        })

        // 리스너.
        // startBtn을 눌렀을 때, start메서드 실행.
        binding.startBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - startBtn is clicked.")
            binding.startBtn.visibility = View.INVISIBLE
            binding.pauseBtn.visibility = View.VISIBLE

            startTimer()
        }
        // 정지 버튼 눌렀을 때, 처음 시간으로 초기화.
        binding.stopBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - stopBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.INVISIBLE

            stopTimer()
        }

        //일시 정지 버튼 눌렀을 때, 타이머 중지하고 재개하면 시간 이어서쭉
        binding.pauseBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - pauseBtn is clicked")
            binding.startBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.INVISIBLE

            pauseTimer()
        }
        // 타이머 시각 누르면 시간 설정 창이 나옴.
        binding.timeView.setOnClickListener { it ->
            Log.d(TAG,"MainActivity - timeView is clicked")
//            binding.editTime.visibility = View.VISIBLE
        }
        // 스위치 누르면 휴식 시간 -> 공부시간 공부시간 -> 휴식시간
        binding.studyBreakSwitch.setOnClickListener { view ->
            alarmManager?.cancel(mPendingIntent)
            viewModel.toggleTime()
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"MainActivity - onStart() called")
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"MainActivity - onResume() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"MainActivity - onDestroy() called")
    }

    private fun startTimer() {
        Log.d(TAG,"MainActivity - startTimer() called")
        setAlarmManager()
        viewModel.startTimer()
    }

    private fun stopTimer() {
        Log.d(TAG,"MainActivity - stopTimer() called")
        alarmManager.cancel(mPendingIntent)
        viewModel.stopTimer()
        binding.timeView.text = viewModel.timeString.value
    }

    private fun pauseTimer() {
        Log.d(TAG,"MainActivity - pauseTimer() called")
        alarmManager.cancel(mPendingIntent)
        viewModel.pauseTimer()
    }

    private fun setAlarmManager() {
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + viewModel.remainTime.value!!,
                    mPendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + viewModel.remainTime.value!!,
                    mPendingIntent
                )
            }
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + viewModel.remainTime.value!!,
                mPendingIntent
            )
        }
    }
}