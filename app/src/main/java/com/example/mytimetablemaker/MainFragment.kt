package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class MainFragment {

    fun mainFun(
        context: Context,
        currentDay: Int,
        currentTime: Int,
        goOrBack: String,
        textView: Map<String, Array<TextView>>,
        view: Map<String, Array<View>>,
        layoutView: Array<LinearLayout>
    ) {

        //クラスの呼び出し
        val settings = Settings(context, goOrBack)

        //＜設定データの取得・表示＞
        //乗換回数
        val changeLine: Int = goOrBack.changeLine
        //＜各時刻の計算＞
        val calcTime = CalcTime(goOrBack, changeLine, currentTime, currentDay)
        //乗車可能時刻[0]・各発車時刻[1]・各到着時刻[2]
        val time: Array<String> = calcTime.getDisplayTimeArray
        //
        val intentArray: Array<Intent> = changeTimetableActivity(context, goOrBack, currentDay)

        //出発地
        val departPoint: TextView = textView["depart"]?.get(0)!!
        departPoint.apply {
            text = goOrBack.departPoint
            setOnClickListener {
                settings.setDepartPointDialog(departPoint)
            }
        }

        //目的地
        val arrivePoint: TextView = textView["arrive"]?.get(0)!!
        arrivePoint.apply {
            text = goOrBack.arrivePoint
            setOnClickListener {
                settings.setArrivePointDialog(arrivePoint)
            }
        }

        for (i: Int in 0..changeLine) {

            //乗車駅
            val departStation: TextView = textView["depart"]?.get(i + 1)!!
            departStation.apply {
                text = goOrBack.getDepartStation[i]
                setOnClickListener {
                    settings.setDepartStationDialog(departStation, i)
                }
            }

            //降車駅
            val arriveStation: TextView = textView["arrive"]?.get(i + 1)!!
            arriveStation.apply {
                text = goOrBack.getArriveStation[i]
                setOnClickListener {
                    settings.setArriveStationDialog(arriveStation, i)
                }
            }

            //路線名・路線カラー
            val lineName: TextView = textView["line"]?.get(i)!!
            val lineLine: View = view["line"]?.get(i)!!
            lineName.apply {
                text = goOrBack.getLineName[i]
                setTextColor(parseColor(goOrBack.getLineColor[i]))
                setOnClickListener {
                    settings.setLineNameDialog(lineName, lineLine, i)
                }
            }
            lineLine.apply {
                setBackgroundColor(parseColor(goOrBack.getLineColor[i]))
                //乗車時間
                setOnClickListener {
                    settings.setRideTimeDialog(i, intentArray[i])
                }
            }
        }
        for (i: Int in 0..changeLine + 1) {
            //乗換移動手段
            val transport: TextView = textView["transport"]?.get(i)!!
            transport.apply {
                text = goOrBack.getTransportation[i]
                setOnClickListener {
                    settings.setTransportationDialog(transport, i)
                }
            }
            //乗換時間
            val transit: View = view["transit"]?.get(i)!!
            transit.setOnClickListener {
                settings.setTransitTimeDialog(i)
            }
        }

        //出発時刻、到着時刻、発車時刻、降車時刻の表示
        for (i: Int in 0..changeLine + 1) {
            val departTime: TextView = textView["departTime"]?.get(i)!!
            val arriveTime: TextView = textView["arriveTime"]?.get(i)!!
            departTime.text = time[2 * i]
            arriveTime.text = time[2 * i + 1]
        }

        layoutView[0].visibility = if (changeLine > 0) {View.VISIBLE} else {View.GONE}
        layoutView[1].visibility = if (changeLine > 1) {View.VISIBLE} else {View.GONE}

        //出発までのカウントダウン
        val countdown: TextView = textView["countdown"]?.get(0)!!
        countdown.apply {
            text = calcTime.getCountdownTime
            setTextColor(calcTime.getCountDownColor)
        }
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goOrBack: String, currentDay: Int): Array<Intent> {

            //乗換回数
            val changeLine: Int = goOrBack.changeLine
            //路線番号
            val linenumber: Array<Int> = Array(changeLine + 1){it}

            //時刻表アクティビティにデータを送るためのキー
            val intentGoOrBack = "goorback"
            val intentLineNumber = "linenumber"
            val intentCurrentDay = "currentday"

            var intentarray: Array<Intent> = arrayOf()
            for (i: Int in 0..changeLine) {
                intentarray += Intent(context, TimetableActivity::class.java).apply {
                    putExtra(intentGoOrBack, goOrBack)
                    putExtra(intentLineNumber, linenumber[i])
                    putExtra(intentCurrentDay, currentDay)
                }
            }
            return intentarray
        }
    }
}