package com.example.mytimetablemaker

import android.graphics.Color.LTGRAY
import android.graphics.Color.RED
import android.graphics.Color.YELLOW

class CalcTime(
        private val goOrBack: String,
        private val changeLine: Int,
        private val currentTime: Int,
        private val currentDay: Int
    ) {

    //ルート内の各路線の乗車可能時刻[0]・発車時刻[1]・到着時刻[2]を取得する関数
    val getDisplayTimeArray: Array<String> get() {
        var displayTimeArray: Array<String> = arrayOf()
        //＜フラグメントの表示＞
        //出発時刻、到着時刻、発車時刻、降車時刻
        //出発時刻
        displayTimeArray += timeArray[0][1].minusHHMM(transitTimeArray[1]).stringTime
        //到着時刻
        displayTimeArray += timeArray[changeLine][2].plusHHMM(transitTimeArray[0]).stringTime
        for (i: Int in 0..changeLine) {
            displayTimeArray += timeArray[i][1].stringTime
            displayTimeArray += timeArray[i][2].stringTime
        }
        return displayTimeArray
    }

    private fun getTimetable(i: Int): Array<Int> =
        (4..25).flatMap { hour -> goOrBack.timeTableArrayInt(i, hour, currentDay).asIterable()}.toTypedArray()

    //内部ストレージに保存された各時刻表データを配列として取得する関数
    private val timetableArray: Array<Array<Int>> =
        (0..changeLine).map{getTimetable(it)}.toTypedArray()

    private val transitTimeArray: Array<Int> =
        (0..changeLine + 1).map{goOrBack.transitTimeInt(it)}.toTypedArray()

    private val rideTimeArray: Array<Int> =
       (0..changeLine).map{goOrBack.rideTimeInt(it)}.toTypedArray()

    //ルート内の各路線の乗車可能時刻[0]・発車時刻[1]・到着時刻[2]を取得する関数
    private val timeArray: Array<Array<Int>> get() {
        val timeArray: Array<Array<Int>> = Array(changeLine + 1) { Array(3) { 0 } }
        //路線1の乗車可能時刻・発車時刻・到着時刻を取得
        timeArray[0][0] = (currentTime / 100).plusHHMM(transitTimeArray[1])
        timeArray[0][1] = getNextStartTime(timeArray[0][0], timetableArray[0])
        timeArray[0][2] = timeArray[0][1].plusHHMM(rideTimeArray[0])
        //路線1以降の乗車可能時刻・発車時刻・到着時刻を取得
        if (changeLine > 0) {
            for (i: Int in 1..changeLine) {
                timeArray[i][0] = timeArray[i - 1][2].plusHHMM(transitTimeArray[i + 1])
                timeArray[i][1] = getNextStartTime(timeArray[i][0], timetableArray[i])
                timeArray[i][2] = timeArray[i][1].plusHHMM(rideTimeArray[i])
            }
        }
        return timeArray
    }

    //発車時刻を取得する関数
    private fun getNextStartTime(possibleTime: Int, eachTimetable: Array<Int>): Int {
        return try {
            var nextStartTime: Int
            var i = 0
            loop@do {
                if (i >= eachTimetable.size) {
                    nextStartTime = eachTimetable[0]
                    break@loop
                }
                nextStartTime = eachTimetable[i]
                i++
            } while (possibleTime > nextStartTime)
            nextStartTime
        } catch (e: ArrayIndexOutOfBoundsException) {
            9999
        }
    }

    //出発時刻
    private val departureTime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])

    //カウントダウン時間（mm:ss）を取得する関数
    val getCountdownTime: String get() {
        //カウントダウン（出発時刻と現在時刻の差）を計算
        var countdownTime: Int = (departureTime * 100).minusHHMMSS(currentTime).HHMMSStoMMSS
        countdownTime = if (countdownTime in 0..9999) countdownTime else -1000000
        val countdownMM: String = (countdownTime / 100).addZeroTime
        val countdownSS: String = (countdownTime % 100).addZeroTime
        return if (countdownTime == -1000000) "--:--"  else "$countdownMM:$countdownSS"
    }

    //カウントダウン表示の警告色を取得する関数
    val getCountDownColor: Int get() {
        val countdownMM: Int = departureTime.minusHHMM(currentTime / 100)
        return if (currentTime % 2 == 1) {
            LTGRAY
        } else {
            when (countdownMM) {
                in 11..99 -> { R.string.colorAccent.setColor }
                in 6..10 -> { YELLOW }
                in 0..5 -> { RED }
                else -> { R.string.lightGray.setColor }
            }
        }
    }
}