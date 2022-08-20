package com.example.pomodoro.util

import java.lang.StringBuilder

object LongToTime {
    // long to time String.
    fun makeMilSecToMinSec(time: Long): String {
        val timeFormat = StringBuilder()
        val min = time/1000/60
        val sec = (time % (1000*60)) / 1000

        //0~9분이면 0 앞에 붙여서 0m:ss 처리.
        if (min < 10) timeFormat.append(0)
        timeFormat.append(min)
        timeFormat.append(":")
        //0~9초면 0 앞에 붙여서 mm:0s 처리.
        if (sec < 10) timeFormat.append(0)
        timeFormat.append(sec)

        return timeFormat.toString()
    }
}