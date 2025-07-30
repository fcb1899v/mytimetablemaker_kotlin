package com.example.mytimetablemaker

import android.content.Context

// Time calculation class for route planning
class CalcTime(
        context: Context,
        private val goOrBack: String,
        private val changeLine: Int,
        private val currentTime: Int,
        private val currentDay: Int
    ) {

    private val myPreference = MyPreference(context)

    // Get timetable data for a specific line
    private fun getTimetable(i: Int): Array<Int> =
        (4..25).flatMap { hour -> myPreference.timeTableArrayInt(goOrBack, i, hour, currentDay).asIterable()}.toTypedArray()

    // Get timetable array data stored in internal storage
    private val timetableArray: Array<Array<Int>> =
        (0..changeLine).map{getTimetable(it)}.toTypedArray()

    // Get transit time array for each line
    private val transitTimeArray: Array<Int> =
        (0..changeLine + 1).map{myPreference.transitTimeInt(goOrBack, it)}.toTypedArray()

    // Get ride time array for each line
    private val rideTimeArray: Array<Int> =
       (0..changeLine).map{myPreference.rideTimeInt(goOrBack, it)}.toTypedArray()

    // Time array: 0: Possible time, 1: Depart time, 2: Arrive time
    private val timeArray: Array<Array<Int>> get() {
        val timeArray: Array<Array<Int>> = Array(changeLine + 1) { Array(3) { 0 } }
        for (i: Int in 0..changeLine) {
            timeArray[i][0] = (when(i) {0 -> currentTime / 100 else -> timeArray[i - 1][2]}).plusHHMM(transitTimeArray[i + 1])
            timeArray[i][1] = timetableArray[i].firstOrNull{it > timeArray[i][0]} ?: 9999
            timeArray[i][2] = timeArray[i][1].plusHHMM(rideTimeArray[i])
        }
        return timeArray
    }
    
    // Get display time array for UI
    val getDisplayTimeArray: Array<String> get() =
        (listOf(departureTime, destinationTime) + timeArray.flatMap{it.slice(1..2)}).map{it.stringTime}.toTypedArray()

    // Calculate departure time
    private val departureTime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])
    // Calculate destination arrival time
    private val destinationTime: Int = timeArray[changeLine][2].plusHHMM(transitTimeArray[0])
    // Calculate countdown time in seconds
    private val countdownTime: Int = (departureTime * 100).minusHHMMSS(currentTime).HHMMSStoMMSS
    // Format countdown minutes
    private val countdownMM: String = (countdownTime / 100).addZeroTime
    // Format countdown seconds
    private val countdownSS: String = (countdownTime % 100).addZeroTime
    // Countdown time in minutes (integer)
    private val countdownMMInt: Int = departureTime.minusHHMM(currentTime / 100)

    // Get countdown time string (MM:SS format)
    val getCountdownTime: String =
        when (countdownTime) { in 0..9999 -> "$countdownMM:$countdownSS" else -> "--:--" }

    // Get countdown color based on remaining time
    val getCountDownColor: Int = when {
        currentTime % 2 == 1 -> R.color.gray
        countdownMMInt in 11..99 -> R.color.accent
        countdownMMInt in 6..10 -> R.color.yellow
        countdownMMInt in 0..5 -> R.color.red
        else -> R.color.gray
    }
}