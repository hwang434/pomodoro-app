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
        //뷰모델 초기화
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

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

        // 라이브데이터 관찰자 설정.
        // 남은시간 관찰자 설정.
        viewModel.remainTime.observe(this, Observer { time ->
            binding.timeView.text = makeMilSecToMinSec(time)
            if (time == 0L) {
                Log.d(TAG,"time == 0")
                binding.pauseBtn.visibility = View.INVISIBLE
                binding.startBtn.visibility = View.VISIBLE
            }
        })
        // 공부 중인지 관찰자 설정
        viewModel.isStudyTime.observe(this, Observer { isStudyTime ->
            Log.d(TAG,"is studyTime is changed.")
            if (isStudyTime) binding.pomodoroStatus.text = "공부중"
            else binding.pomodoroStatus.text = "휴식중"
        })
        // 타이머가 돌아가고 있는지 관찰자 설정
        viewModel.isTimerRunning.observe(this, Observer { isTimerRunning ->
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

        // 일시적으로 만든 메인에서 진동 멈추기
        binding.stopVibration.setOnClickListener {
            Toast.makeText(this, "vibrator cancle button is clicked.",Toast.LENGTH_SHORT).show()
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
        }
    }

    private fun startTimer() {
        Log.d(TAG,"MainActivity - startTimer() called Build.VERSION : ${Build.VERSION.SDK_INT}")
        viewModel.makeTimer()
        viewModel.startTimer()
    }

    private fun stopTimer() {
        Log.d(TAG,"MainActivity - stopTimer() called")
        alarmManager.cancel(mPendingIntent)
        viewModel.stopTimer()
    }

    private fun pauseTimer() {
        Log.d(TAG,"MainActivity - pauseTimer() called")
        alarmManager.cancel(mPendingIntent)
        viewModel.pauseTimer()
    }

    //시간을 디스프레이에 보여줄 형식을 만들어주는 메소드.
    //mm:ss 형식임.
    private fun makeMilSecToMinSec(time: Long): String {
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

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"MainActivity - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"MainActivity - onStop() called")

        // 공부 시간이었으면 휴식 시간으로 설정되어 있어야함.
        viewModel.stopTimer()

        // 백그라운드에서만 alarmManager로 알림 울리게 하는 게 나음.
        // foreground에선 정확하게 타이머로 울리게 하고.
        // 이게 타이머가 돌아가는 중에만 onStop했을 때 알람이 울려야하는데
        // 타이머를 시작 안하고 화면 꺼도 onStop이 되면서 알람이 울림. 알림이 진행 중인가를 viewModel에 변수로 선언하는 게 좋을 거 같음.
        // 진행 중이어야 밑에 알람매니저가 실행 되게해야함.
        if (viewModel.isTimerRunning.value == true) {
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

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG,"MainActivity - onRestart() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"MainActivity - onDestroy() called")
        pauseTimer()
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"MainActivity - onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"MainActivity - onResume() called")
    }
}