package com.example.pomodoro.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    private val TAG: String = "로그"
    var isStudyTime: Boolean

    var studyLength: Long
    var breakLength: Long
    private var _remainTime: MutableLiveData<Long> = MutableLiveData()
    val remainTime: LiveData<Long>
        get() = _remainTime

    init {
        studyLength = 10*1000+2000
        breakLength = 5*1000+2000
        _remainTime.value = studyLength
        isStudyTime = true
//        _isStudyTime.value = true
    }
    // 공부 시간이 끝나면 isStudyTime이 false가 됨.
    fun onStudyFinish() {
        isStudyTime = false
//        _isStudyTime.value = false
    }

    // 공부 시간이 끝나고, 메소드가 다 끝나면 다시 true가 됨.
    fun onStudyFinishComplete() {
        isStudyTime = true
//        _isStudyTime.value = true
    }

    // remainTime 1초씩 주는 걸 위한 코드
    fun onTickTime(time: Long) {
//        Log.d(TAG,"TimerViewModel - onTickTime() called")
//        Log.d(TAG,"_remainTime.value : .${_remainTime.value}")
        _remainTime.value = _remainTime.value!!.minus(1000)
    }

    // 남은 시간 바꾸는 메서드
    fun changeRemainTime(time: Long) {
        _remainTime.value = time
    }

    override fun onCleared() {
        super.onCleared()
//        Log.d(TAG,"TimerViewModel - onCleared() called")
    }
}