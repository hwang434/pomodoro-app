package com.example.pomodoro.viewmodels

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pomodoro.service.VibrationService

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
    init {
        studyLength = 10*1000
        breakLength = 5*1000
        _remainTime.value = studyLength
        _isStudyTime.value = true
        _isTimerRunning.value = false
        makeTimer()
    }
    // 공부 시간이 끝나면 isStudyTime이 false가 됨.
    fun onStudyFinish() {
        _isStudyTime.value = false
    }

    // 공부 시간이 끝나고, 메소드가 다 끝나면 다시 true가 됨.
    fun onStudyFinishComplete() {
        _isStudyTime.value = true
    }

    // 남은 시간 바꾸는 메서드
    fun changeRemainTime(time: Long) {
        _remainTime.value = time
    }

    // 카운트 다운 타이머 객체 만들기.
    fun makeTimer() {
        timer = object: CountDownTimer(_remainTime.value!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"TimerViewModel - onTick() called remainTime : ${remainTime.value}")
                _remainTime.value = _remainTime.value!!.minus(1000)
            }

            override fun onFinish() {
                Log.d(TAG,"TimerViewModel - onFinish() called")
                _remainTime.value = _remainTime.value!!.minus(1000)

                // 타이머가 끝나므로 공부 시간의 true false가 반대가 됨.
                _isStudyTime.value = !_isStudyTime.value!!

                // 공부시간이 finish하므로 남은 시간이 휴식 시간이 됨.
                if (isStudyTime.value!!) {
                    _remainTime.value = studyLength
                } else {
                    _remainTime.value = breakLength
                }
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
        _isTimerRunning.value = false
    }
    // 타이머 일시 정지 (시간 기록)
    fun pauseTimer() {
        timer.cancel()
        _isTimerRunning.value = false
    }
    override fun onCleared() {
        super.onCleared()
    }
}