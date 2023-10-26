package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentVarioussettingsBinding
import java.util.Calendar

//各種設定
class SettingsVariousFragment: Fragment() {

    //ViewBinding
    private lateinit var binding: FragmentVarioussettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVarioussettingsBinding.inflate(layoutInflater, container, false)

        val mySettings = MySettings(requireContext())
        val myPreference = MyPreference(requireContext())

        val goOrBack: String? = arguments?.getString("BUNDLE_KEY_GOORBACK")
        val changeLine: Int = myPreference.changeLine(goOrBack!!)
        val intentArray: Array<Intent> = changeTimetableActivity(requireContext(), goOrBack)
        val isSettings = true

        val layout: Map<String, Array<LinearLayout>> = mapOf(
            "station2" to arrayOf(
                binding.station2layout, binding.lineNameLayout2, binding.rideTimeLayout2,
                binding.transportationLayout2, binding.transitTimeLayout2
            ),
            "station3" to arrayOf(
                binding.station3layout, binding.lineNameLayout3, binding.rideTimeLayout3,
                binding.transportationLayout3, binding.transitTimeLayout3
            ),
        )

        //乗換回数に応じたレイアウトの表示・非表示
        for (i: Int in 0..4) {
            layout["station2"]!![i].visibility = if (changeLine > 0) {View.VISIBLE} else {View.GONE}
            layout["station3"]!![i].visibility = if (changeLine > 1) {View.VISIBLE} else {View.GONE}
        }

        val textView: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.departPointText, binding.departStationText1, binding.departStationText2, binding.departStationText3),
            "arrive" to arrayOf(binding.destinationText, binding.arriveStationText1, binding.arriveStationText2, binding.arriveStationText3),
            "lineName" to arrayOf(binding.lineNameText1, binding.lineNameText2, binding.lineNameText3),
            "rideTime" to arrayOf(binding.rideTimeText1, binding.rideTimeText2, binding.rideTimeText3),
            "transportation" to arrayOf(binding.transportationTextE, binding.transportationText1, binding.transportationText2, binding.transportationText3),
            "transitTime" to arrayOf(binding.transitTimeTextE, binding.transitTimeText1, binding.transitTimeText2, binding.transitTimeText3)
        )

        val buttonView: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.departPointButton, binding.departStationButton1, binding.departStationButton2, binding.departStationButton3),
            "arrive" to arrayOf(binding.destinationButton, binding.arriveStationButton1, binding.arriveStationButton2, binding.arriveStationButton3),
            "lineName" to arrayOf(binding.lineNameButton1, binding.lineNameButton2, binding.lineNameButton3),
            "rideTime" to arrayOf(binding.rideTimeButton1, binding.rideTimeButton2, binding.rideTimeButton3),
            "transportation" to arrayOf(binding.transportationButtonE, binding.transportationButton1, binding.transportationButton2, binding.transportationButton3),
            "transitTime" to arrayOf(binding.transitTimeButtonE, binding.transitTimeButton1, binding.transitTimeButton2, binding.transitTimeButton3)
        )

        //設定値の表示
        textView["depart"]!![0].apply{
            val departPointText = myPreference.settingsDepartPoint(goOrBack)
            text = departPointText
            setTextColor(departPointText.settingsTextColor)
        }
        textView["arrive"]!![0].apply{
            val arrivePointText = myPreference.settingsArrivePoint(goOrBack)
            text = arrivePointText
            setTextColor(arrivePointText.settingsTextColor)
        }

        for (i: Int in 0..changeLine) {
            textView["depart"]!![i + 1].apply{
                val departStationText = myPreference.settingsDepartStation(goOrBack, i)
                text = departStationText
                setTextColor(departStationText.settingsTextColor)
            }
            textView["arrive"]!![i + 1].apply{
                val arriveStationText = myPreference.settingsArriveStation(goOrBack, i)
                text = arriveStationText
                setTextColor(arriveStationText.settingsTextColor)
            }
            textView["lineName"]!![i].apply{
                text = myPreference.settingsLineName(goOrBack, i)
                setTextColor( myPreference.settingsLineColorInt(goOrBack, i))
            }
            textView["rideTime"]!![i].apply {
                val rideTimeText = myPreference.settingsRideTime(goOrBack, i)
                text = rideTimeText.addMinutes
                setTextColor(rideTimeText.settingsTextColor)
            }
        }

        for (i: Int in 0..changeLine + 1) {
            textView["transportation"]!![i].apply{
                val transportationText = myPreference.settingsTransportation(goOrBack, i)
                text = transportationText
                setTextColor(transportationText.settingsTextColor)
            }
            textView["transitTime"]!![i].apply{
                val transitTimeText = myPreference.settingsTransitTime(goOrBack, i)
                text = transitTimeText.addMinutes
                setTextColor(transitTimeText.settingsTextColor)
            }
        }

        //出発地名の設定
        buttonView["depart"]!![0].setOnClickListener {
            mySettings.setDepartPointDialog(goOrBack, textView["depart"]!![0], isSettings)
        }
        //到着地名の設定
        buttonView["arrive"]!![0].setOnClickListener {
            mySettings.setArrivePointDialog(goOrBack, textView["arrive"]!![0], isSettings)
        }

        for (i: Int in 0..changeLine) {
            //乗車駅名の設定
            buttonView["depart"]!![i + 1].setOnClickListener {
                mySettings.setDepartStationDialog(goOrBack, textView["depart"]!![i], i, isSettings)
            }
            //降車駅名の設定
            buttonView["arrive"]!![i + 1].setOnClickListener {
                mySettings.setArriveStationDialog(goOrBack, textView["arrive"]!![i], i, isSettings)
            }
            //路線名の設定
            buttonView["lineName"]!![i].setOnClickListener {
                mySettings.setLineNameDialog(goOrBack, textView["lineName"]!![i], null, i)
            }
            //乗車時間の設定
            buttonView["rideTime"]!![i].setOnClickListener {
                mySettings.setRideTimeDialog(goOrBack, textView["rideTime"]!![i], i, intentArray[i])
            }
        }

        for (i: Int in 0..changeLine + 1) {
            //移動手段の設定
            buttonView["transportation"]!![i].setOnClickListener {
                mySettings.setTransportationDialog(goOrBack, textView["transportation"]!![i], i, isSettings)
            }
            //乗換時間の設定
            buttonView["transitTime"]!![i].setOnClickListener {
                mySettings.setTransitTimeDialog(goOrBack, textView["transitTime"]!![i], i)
            }
        }
        return binding.root
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goOrBack: String): Array<Intent> {

            val myPreference = MyPreference(context)
            val changeLine: Int = myPreference.changeLine(goOrBack)
            val lineNumber: Array<Int> = Array(changeLine + 1){it}
            val calendar: Calendar = Calendar.getInstance()
            val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1

            val intentGoOrBack = "goorback"
            val intentLineNumber = "linenumber"
            val intentCurrentDay = "currentday"

            var intentArray: Array<Intent> = arrayOf()
            for (i: Int in 0..changeLine) {
                intentArray += Intent(context, TimetableActivity::class.java).apply {
                    putExtra(intentGoOrBack, goOrBack)
                    putExtra(intentLineNumber, lineNumber[i])
                    putExtra(intentCurrentDay, currentDay)
                }
            }
            return intentArray
        }
    }
}