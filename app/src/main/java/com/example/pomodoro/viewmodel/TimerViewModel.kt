package com.example.pomodoro.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.StringBuilder

class TimerViewModel: ViewModel() {
    private val TAG: String = "로그"
    // timer를 service로 분리해야함.
    private lateinit var timer: CountDownTimer
    private var studyLength: Long = 25 * 60 * 1000
    private var breakLength: Long = 1 * 60 * 1000
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
        _remainTime.value = studyLength
        _isStudyTime.value = true
        _isTimerRunning.value = false
        makeTimer()
    }

    // viewModel 사라지기 전에 불리는 메소드
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"TimerViewModel - onCleared() called")
    }

    // 카운트 다운 타이머 객체 만들기.
    private fun makeTimer() {
        timer = object: CountDownTimer(_remainTime.value!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"TimerViewModel - onTick() called remainTime : ${remainTime.value}")
                _remainTime.value?.let { _remainTime.postValue(it - 1000) }
            }

            override fun onFinish() {
                Log.d(TAG,"TimerViewModel - onFinish() called")
                // 타이머가 끝나므로 공부 시간의 true false가 반대가 됨.
                _remainTime.value = _remainTime.value!! - 1000
                // 공부시간이면 휴식 시간이 되고, 휴식 시간이면 공부시간이됨.
                _isStudyTime.value = !_isStudyTime.value!!

                // 시간을 돌림.
                reverseTimer()

                // 타이머가 종료됐으므로 false(타이머가 돌아가고 있지 않음)로 만듬.
                _isTimerRunning.value = false
            }
        }
    }

    // 타이머 시간 실행하기.
    fun startTimer() {
        makeTimer()
        timer.start()
        _isTimerRunning.value = true
    }

    // 타이머 정지 (아예 시간 초기화)
    fun stopTimer() {
        timer.cancel()
        reverseTimer()
        _isTimerRunning.value = false
    }

    // 타이머 일시 정지 (시간 기록)
    fun pauseTimer() {
        timer.cancel()
        _isTimerRunning.value = false
    }

    // 시간 바꾸기
    fun reverseTimer() {
        if (isStudyTime.value == true) {        // 현재 공부시간이었으므로 휴식 시간으로 만들어줌.
            _remainTime.value = studyLength
        } else {                                // 현재 쉬는 시간이므로 공부 시간으로 만들어줌.
            _remainTime.value = breakLength
        }
    }

    fun toggleTime() {
        timer.cancel()
        _isStudyTime.value = !_isStudyTime.value!!
        reverseTimer()
        _isTimerRunning.value = false
    }

    fun setTime(time: Long) {
        _remainTime.postValue(time)
    }
}