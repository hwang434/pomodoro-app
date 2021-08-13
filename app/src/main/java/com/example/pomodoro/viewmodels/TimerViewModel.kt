package com.example.pomodoro.viewmodels

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pomodoro.service.VibrationService
import java.lang.StringBuilder

class TimerViewModel: ViewModel() {
    private val TAG: String = "로그"

    // timer를 service로 분리해야함.
    private lateinit var timer: CountDownTimer

    private var studyLength: Long
    private var breakLength: Long
    private val _remainTime: MutableLiveData<Long> = MutableLiveData()
    val remainTime: LiveData<Long>
        get() = _remainTime
    private val _isStudyTime: MutableLiveData<Boolean> = MutableLiveData()
    val isStudyTime: LiveData<Boolean>
        get() = _isStudyTime
    private val _isTimerRunning: MutableLiveData<Boolean> = MutableLiveData()
    val isTimerRunning: LiveData<Boolean>
        get() = _isTimerRunning
    private val _timeString: MutableLiveData<String> = MutableLiveData()
    val timeString: LiveData<String>
        get() = _timeString
    init {
        // 공부 시간 설정.
        studyLength = 10*1000
        // 쉬는 시간 설정.
        breakLength = 5*1000
        _remainTime.value = studyLength
        _isStudyTime.value = true
        _isTimerRunning.value = false
        makeMilSecToMinSec(remainTime.value!!)
        makeTimer()
    }

    // viewModel 사라지기 전에 불리는 메소드
    override fun onCleared() {
        super.onCleared()
    }

    // 카운트 다운 타이머 객체 만들기.
    fun makeTimer() {
        timer = object: CountDownTimer(_remainTime.value!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"TimerViewModel - onTick() called remainTime : ${remainTime.value}")
                _remainTime?.apply {
                    this.value = this.value?.minus(1000)
                    makeMilSecToMinSec(time = this.value!!)
                }
            }

            override fun onFinish() {
                Log.d(TAG,"TimerViewModel - onFinish() called")
                // 타이머가 끝나므로 공부 시간의 true false가 반대가 됨.
                _remainTime.value = _remainTime.value?.minus(1000)
                _isStudyTime.value = !_isStudyTime.value!!

                // 공부시간이 finish하므로 남은 시간이 휴식 시간이 됨.
                if (isStudyTime.value!!) {
                    _remainTime.value = studyLength
                } else {
                    _remainTime.value = breakLength
                }

                _remainTime?.apply {
                    makeMilSecToMinSec(this.value!!)
                }
                // 타이머가 종료됐으므로 false(타이머가 돌아가고 있지 않음)로 만듬.
                _isTimerRunning.value = false
            }
        }
    }
    // 타이머 시간 실행하기.
    fun startTimer() {
        timer.start()
        _isTimerRunning.value = true
    }
    // 타이머 정지 (아예 시간 초기화)
    fun stopTimer() {
        timer.also { timer ->
            timer.cancel()

            if (isStudyTime.value!!) {
                _remainTime.value = studyLength
            } else {
                _remainTime.value = breakLength
            }
        }

        _remainTime?.apply {
            makeMilSecToMinSec(this.value!!)
        }
        _isTimerRunning.value = false
    }

    // 타이머 일시 정지 (시간 기록)
    fun pauseTimer() {
        timer.cancel()
        _isTimerRunning.value = false
    }

    //시간을 디스프레이에 보여줄 형식을 만들어주는 메소드.
    //mm:ss 형식임.
    fun makeMilSecToMinSec(time: Long) {
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

        _timeString.value = timeFormated.toString()
    }
}