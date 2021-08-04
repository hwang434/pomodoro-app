package com.example.pomodoro.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    private val TAG: String = "로그"
    var isStudyTime: Boolean
    var studyLength: Long
    var breakLength: Long
    var remainTime: Long

    init {
        studyLength = 10*1000
        breakLength = 5*1000
        remainTime = studyLength
        isStudyTime = true
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"TimerViewModel - onCleared() called")
    }
}