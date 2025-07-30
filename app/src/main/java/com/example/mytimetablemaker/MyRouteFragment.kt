package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

// Class for managing route fragment display and interactions
class MyRouteFragment(
    val context: Context,
    val currentDay: Int,
    private val currentTime: Int,
) {

    // Main function to initialize route display
    fun mainFun(
        goOrBack: String,
        textView: Map<String, Array<TextView>>,
        view: Map<String, Array<View>>,
        layoutView: Array<LinearLayout>
    ) {

        val mySettings = MySettings(context)
        val myPreference = MyPreference(context)
        val isSettings = false
        val changeLine: Int = myPreference.changeLine(goOrBack)
        val calcTime = CalcTime(context, goOrBack, changeLine, currentTime, currentDay)
        val time: Array<String> = calcTime.getDisplayTimeArray
        val intentArray: Array<Intent> = changeTimetableActivity(context, goOrBack, currentDay)

        // Set departure point
        val departPoint = textView["depart"]?.get(0)!!
        departPoint.apply {
            text = myPreference.departPoint(goOrBack)
            setOnClickListener {
                mySettings.setDepartPointDialog(goOrBack, departPoint, isSettings)
            }
        }
        // Set arrival point
        val arrivePoint = textView["arrive"]?.get(0)!!
        arrivePoint.apply {
            text = myPreference.arrivePoint(goOrBack)
            setOnClickListener {
                mySettings.setArrivePointDialog(goOrBack, arrivePoint, isSettings)
            }
        }

        // Configure each line segment
        for (i: Int in 0..changeLine) {
            // Set departure station
            val departStation = textView["depart"]?.get(i + 1)!!
            departStation.apply {
                text = myPreference.departStation(goOrBack, i)
                setOnClickListener {
                    mySettings.setDepartStationDialog(goOrBack, departStation, i, isSettings)
                }
            }
            // Set arrival station
            val arriveStation = textView["arrive"]?.get(i + 1)!!
            arriveStation.apply {
                text = myPreference.arriveStation(goOrBack, i)
                setOnClickListener {
                    mySettings.setArriveStationDialog(goOrBack, arriveStation, i, isSettings)
                }
            }
            // Set line name and line color
            val lineName: TextView = textView["line"]?.get(i)!!
            val lineLine: View = view["line"]?.get(i)!!
            lineName.apply {
                text = myPreference.lineName(goOrBack, i)
                setTextColor(myPreference.lineColor(goOrBack, i).setStringColor)
                setOnClickListener {
                    mySettings.setLineNameDialog(goOrBack, lineName, lineLine, i)
                }
            }
            lineLine.apply {
                setBackgroundColor(myPreference.lineColor(goOrBack, i).setStringColor)
                // Set ride time
                setOnClickListener {
                    mySettings.setRideTimeDialog(goOrBack, null, i, intentArray[i])
                }
            }
        }

        // Configure transportation and transit times
        for (i: Int in 0..changeLine + 1) {
            // Set transportation type
            val transportation: TextView = textView["transport"]?.get(i)!!
            transportation.apply {
                text = myPreference.transportation(goOrBack, i)
                setOnClickListener {
                    mySettings.setTransportationDialog(goOrBack, transportation, i, isSettings)
                }
            }
            // Set transit time
            val transitTime: View = view["transit"]?.get(i)!!
            transitTime.setOnClickListener {
                mySettings.setTransitTimeDialog(goOrBack, null, i)
            }
            // Set departure and arrival times
            val departTime: TextView = textView["departTime"]?.get(i)!!
            val arriveTime: TextView = textView["arriveTime"]?.get(i)!!
            departTime.text = time[2 * i]
            arriveTime.text = time[2 * i + 1]
        }
        // Route 2 visibility
        layoutView[0].visibility = if (changeLine > 0) {View.VISIBLE} else {View.GONE}
        layoutView[1].visibility = if (changeLine > 1) {View.VISIBLE} else {View.GONE}

        // Count down time
        textView["countdown"]?.get(0)!!.apply {
            text = calcTime.getCountdownTime
            setTextColor(calcTime.getCountDownColor)
        }
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goOrBack: String, currentDay: Int): Array<Intent> {

            val myPreference = MyPreference(context)
            val changeLine: Int = myPreference.changeLine(goOrBack)
            val linenumber: Array<Int> = Array(changeLine + 1){it}

            // Time table activity data key
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