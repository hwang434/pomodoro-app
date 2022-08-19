package com.example.pomodoro.views

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.pomodoro.R
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.receiver.AlarmReceiver
import com.example.pomodoro.viewmodel.TimerViewModel
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "로그"
        // 리시버 보낼 인텐트 주소.
        private const val BROAD_CAST = "com.example.pomodoro.ALARM_START"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var timerViewModel: TimerViewModel
    // 알람 매니저
    private lateinit var alarmManager: AlarmManager
    // PendingIntent
    private lateinit var mPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // 터치 이벤트 설정
        setEvent()
        // 화면 영구적으로 키게 설정.
        turnOnDisplayPermanently()
        // 뷰모델 초기화
        initViewModel()
        // 동적으로 리시버 등록하기.
        registerAlarmReceiver()
        initAlarmManager()
        initAlarmPendingIntent()
        setObserver()
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
        stopTimer()
    }

    // 공부 시간으로 설정
    private fun setToStudyTime() {
        Log.d(TAG,"MainActivity - setToStudyTime() called")
    }

    // 쉬는 시간으로 설정
    private fun setToRestTime() {
        Log.d(TAG,"MainActivity - setToRestTime() called")
    }

    // 시간 설정
    private fun setTime() {
        Log.d(TAG,"MainActivity - setTime() called")
    }

    private fun startTimer() {
        Log.d(TAG,"MainActivity - startTimer() called")
        binding.startBtn.visibility = View.INVISIBLE
        binding.pauseBtn.visibility = View.VISIBLE
        setAlarmManager()
        timerViewModel.startTimer()
    }

    private fun stopTimer() {
        Log.d(TAG,"MainActivity - stopTimer() called")
        binding.startBtn.visibility = View.VISIBLE
        binding.pauseBtn.visibility = View.INVISIBLE
        alarmManager.cancel(mPendingIntent)
        timerViewModel.stopTimer()
    }

    private fun pauseTimer() {
        Log.d(TAG,"MainActivity - pauseTimer() called")
        binding.startBtn.visibility = View.VISIBLE
        binding.pauseBtn.visibility = View.INVISIBLE
        alarmManager.cancel(mPendingIntent)
        timerViewModel.pauseTimer()
    }

    private fun setAlarmManager() {
        Log.d(TAG,"MainActivity - setAlarmManager() called")
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + timerViewModel.remainTime.value!!,
                    mPendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + timerViewModel.remainTime.value!!,
                    mPendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timerViewModel.remainTime.value!!,
                mPendingIntent
            )
        }
    }

    private fun turnOnDisplayPermanently() {
        Log.d(TAG,"MainActivity - turnOnDisplayPermanently() called")
        // 어플리케이션 실행하는 동안 화면 계속 켜지게함.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initViewModel() {
        Log.d(TAG,"MainActivity - initViewModel() called")
        // 뷰모델 초기화
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        // binding의 뷰모델 설정하기
        binding.timerViewModel = timerViewModel
        binding.lifecycleOwner = this
    }

    private fun registerAlarmReceiver() {
        Log.d(TAG,"MainActivity - registerAlarmReceiver() called")
        val alarmReceiver = AlarmReceiver()
        val intentFilter = IntentFilter(BROAD_CAST)
        registerReceiver(alarmReceiver, intentFilter)
    }

    private fun initAlarmManager() {
        Log.d(TAG,"MainActivity - initAlarmManager() called")
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun initAlarmPendingIntent() {
        Log.d(TAG,"MainActivity - initAlarmPendingIntent() called")
        val alarmIntent = Intent(BROAD_CAST)
        mPendingIntent = PendingIntent.getBroadcast(this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // LiveData를 기준으로 UI 변경
    private fun setObserver() {
        // 공부 중인지 관찰자 설정
        timerViewModel.isStudyTime.observe(this) { isStudyTime ->
            Log.d(TAG,"isStudyTime changed to ${timerViewModel.isStudyTime.value}")
            if (isStudyTime) {
                binding.pomodoroStatus.text = "공부중"
                binding.studyBreakSwitch.isChecked = false
            }
            else {
                binding.pomodoroStatus.text = "휴식중"
                binding.studyBreakSwitch.isChecked = true
            }
            Log.d(TAG,"MainActivity - isSelected : ${binding.studyBreakSwitch.isChecked}")
        }

        // 타이머가 돌아가고 있는지 관찰자 설정
        timerViewModel.isTimerRunning.observe(this) { isTimerRunning ->
            Log.d(TAG,"MainActivity - isTimerRunning observed : $isTimerRunning")
            // if : 타이머가 실행 중이면  타이머 시작버튼 안보이고 일시정지 버튼 보임.
            if (isTimerRunning) {
                binding.startBtn.visibility = View.INVISIBLE
                binding.pauseBtn.visibility = View.VISIBLE
            } else {
                // else : 타이머 일시정지 보이고   시작버튼 보임.
                binding.startBtn.visibility = View.VISIBLE
                binding.pauseBtn.visibility = View.INVISIBLE
            }
        }
        
        timerViewModel.remainTime.observe(this) { time ->
            Log.d(TAG,"MainActivity - time : $time")
            binding.timeView.text = makeMilSecToMinSec(time)
        }

        // 1초마다
        timerViewModel.remainTime
    }

    //시간을 디스프레이에 보여줄 형식을 만들어주는 메소드.
    //mm:ss 형식임.
    private fun makeMilSecToMinSec(time: Long): String {
        val timeFormat = StringBuilder()
        var min = time/1000/60
        var sec = (time % (1000*60)) / 1000

        //0~9분이면 0 앞에 붙여서 0m:ss 처리.
        if (min < 10) timeFormat.append(0)
        timeFormat.append(min)
        timeFormat.append(":")
        //0~9초면 0 앞에 붙여서 mm:0s 처리.
        if (sec < 10) timeFormat.append(0)
        timeFormat.append(sec)

        return timeFormat.toString()
    }

    private fun setEvent() {
        // startBtn을 눌렀을 때, start메서드 실행.
        binding.startBtn.setOnClickListener {
            Log.d(TAG,"MainActivity - startBtn is clicked.")
            startTimer()
        }

        // 정지 버튼 눌렀을 때, 처음 시간으로 초기화.
        binding.stopBtn.setOnClickListener {
            stopTimer()
        }

        //일시 정지 버튼 눌렀을 때, 타이머 중지하고 재개하면 시간 이어서쭉
        binding.pauseBtn.setOnClickListener {
            pauseTimer()
        }

        // 타이머 시각 누르면 시간 설정 창이 나옴.
        binding.timeView.setOnClickListener {
            setTime()
        }

        binding.studyBreakSwitch.setOnClickListener { view ->
            // 타이머를 멈추고
            stopTimer()
            // if : 공부시간이면 -> 휴식 시간으로 설정
            if (timerViewModel.isStudyTime.value == true) {
                setToRestTime()
            }
            // else : 공부 시간 아니면 -> 공부 시간으로 설정
            else {
                setToStudyTime()
            }
        }
    }
}