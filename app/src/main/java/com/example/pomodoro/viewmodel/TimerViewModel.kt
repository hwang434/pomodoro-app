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
    private var studyLength: Long = 25 * 60 * 1000
    private var breakLength: Long = 5 * 60 * 1000
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
    }

    // viewModel 사라지기 전에 불리는 메소드
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"TimerViewModel - onCleared() called")
    }

    // 타이머 정지 (아예 시간 초기화)
    fun stopTimer() {
        reverseTimer()
        _isTimerRunning.value = false
    }

    // 타이머 일시 정지 (시간 기록)
    fun pauseTimer() {
        _isTimerRunning.value = false
    }

    // 시간 바꾸기
    private fun reverseTimer() {
        Log.d(TAG,"TimerViewModel - reverseTimer() called")
        if (isStudyTime.value == true) {        // 현재 공부시간이었으므로 휴식 시간으로 만들어줌.
            _remainTime.value = studyLength
        } else {                                // 현재 쉬는 시간이므로 공부 시간으로 만들어줌.
            _remainTime.value = breakLength
        }
    }

    fun toggleTime() {
        Log.d(TAG,"TimerViewModel - toggleTime() called")
        _isStudyTime.value = !_isStudyTime.value!!
        reverseTimer()
        _isTimerRunning.value = false
    }

    fun setTime(time: Long) {
        _remainTime.postValue(time)
    }
}