package com.example.mytimetablemaker

import android.content.Context

class CalcTime(
        context: Context,
        private val goOrBack: String,
        private val changeLine: Int,
        private val currentTime: Int,
        private val currentDay: Int
    ) {

    private val myPreference = MyPreference(context)

    private fun getTimetable(i: Int): Array<Int> =
        (4..25).flatMap { hour -> myPreference.timeTableArrayInt(goOrBack, i, hour, currentDay).asIterable()}.toTypedArray()

    //内部ストレージに保存された各時刻表データを配列として取得する関数
    private val timetableArray: Array<Array<Int>> =
        (0..changeLine).map{getTimetable(it)}.toTypedArray()

    private val transitTimeArray: Array<Int> =
        (0..changeLine + 1).map{myPreference.transitTimeInt(goOrBack, it)}.toTypedArray()

    private val rideTimeArray: Array<Int> =
       (0..changeLine).map{myPreference.rideTimeInt(goOrBack, it)}.toTypedArray()

    //0: Possible time, 1: Depart time, 2: Arrive time
    private val timeArray: Array<Array<Int>> get() {
        val timeArray: Array<Array<Int>> = Array(changeLine + 1) { Array(3) { 0 } }
        for (i: Int in 0..changeLine) {
            timeArray[i][0] = (when(i) {0 -> currentTime / 100 else -> timeArray[i - 1][2]}).plusHHMM(transitTimeArray[i + 1])
            timeArray[i][1] = timetableArray[i].firstOrNull{it > timeArray[i][0]} ?: 9999
            timeArray[i][2] = timeArray[i][1].plusHHMM(rideTimeArray[i])
        }
        return timeArray
    }
    val getDisplayTimeArray: Array<String> get() =
        (listOf(departureTime, destinationTime) + timeArray.flatMap{it.slice(1..2)}).map{it.stringTime}.toTypedArray()

    private val departureTime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])
    private val destinationTime: Int = timeArray[changeLine][2].plusHHMM(transitTimeArray[0])
    private val countdownTime: Int = (departureTime * 100).minusHHMMSS(currentTime).HHMMSStoMMSS
    private val countdownMM: String = (countdownTime / 100).addZeroTime
    private val countdownSS: String = (countdownTime % 100).addZeroTime
    private val countdownMMInt: Int = departureTime.minusHHMM(currentTime / 100)

    //Countdown time (MM:SS)
    val getCountdownTime: String =
        when (countdownTime) { in 0..9999 -> "$countdownMM:$countdownSS" else -> "--:--" }

    //Countdown color
    val getCountDownColor: Int = when {
        currentTime % 2 == 1 -> R.string.lightGray.setColor
        countdownMMInt in 11..99 -> R.string.colorAccent.setColor
        countdownMMInt in 6..10 -> R.string.yellow.setColor
        countdownMMInt in 0..5 -> R.string.red.setColor
        else -> R.string.lightGray.setColor
    }
}