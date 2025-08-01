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

// Fragment for various settings management
class SettingsVariousFragment: Fragment() {

    // ViewBinding for accessing views
    private lateinit var binding: FragmentVarioussettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVarioussettingsBinding.inflate(layoutInflater, container, false)

        val mySettings = MySettings(requireContext())
        val myPreference = MyPreference(requireContext())

        val goOrBack: String? = arguments?.getString("BUNDLE_KEY_GOORBACK")
        val changeLine: Int = myPreference.changeLine(goOrBack!!)
        val intentArray: Array<Intent> = changeTimetableActivity(requireContext(), goOrBack)
        val isSettings = true

        // Layout mapping for different station configurations
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

        // Show/hide layouts based on number of transfers
        for (i: Int in 0..4) {
            layout["station2"]!![i].visibility = if (changeLine > 0) {View.VISIBLE} else {View.GONE}
            layout["station3"]!![i].visibility = if (changeLine > 1) {View.VISIBLE} else {View.GONE}
        }

        // TextView mapping for different data types
        val textView: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.departPointText, binding.departStationText1, binding.departStationText2, binding.departStationText3),
            "arrive" to arrayOf(binding.destinationText, binding.arriveStationText1, binding.arriveStationText2, binding.arriveStationText3),
            "lineName" to arrayOf(binding.lineNameText1, binding.lineNameText2, binding.lineNameText3),
            "rideTime" to arrayOf(binding.rideTimeText1, binding.rideTimeText2, binding.rideTimeText3),
            "transportation" to arrayOf(binding.transportationTextE, binding.transportationText1, binding.transportationText2, binding.transportationText3),
            "transitTime" to arrayOf(binding.transitTimeTextE, binding.transitTimeText1, binding.transitTimeText2, binding.transitTimeText3)
        )

        // Button mapping for different data types
        val buttonView: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.departPointButton, binding.departStationButton1, binding.departStationButton2, binding.departStationButton3),
            "arrive" to arrayOf(binding.destinationButton, binding.arriveStationButton1, binding.arriveStationButton2, binding.arriveStationButton3),
            "lineName" to arrayOf(binding.lineNameButton1, binding.lineNameButton2, binding.lineNameButton3),
            "rideTime" to arrayOf(binding.rideTimeButton1, binding.rideTimeButton2, binding.rideTimeButton3),
            "transportation" to arrayOf(binding.transportationButtonE, binding.transportationButton1, binding.transportationButton2, binding.transportationButton3),
            "transitTime" to arrayOf(binding.transitTimeButtonE, binding.transitTimeButton1, binding.transitTimeButton2, binding.transitTimeButton3)
        )

        // Display current settings values
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

        // Display settings for each transfer station
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

        // Display transportation and transit time settings
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

        // Set departure point name
        buttonView["depart"]!![0].setOnClickListener {
            mySettings.setDepartPointDialog(goOrBack, textView["depart"]!![0], isSettings)
        }
        // Set arrival point name
        buttonView["arrive"]!![0].setOnClickListener {
            mySettings.setArrivePointDialog(goOrBack, textView["arrive"]!![0], isSettings)
        }

        // Set up click listeners for each transfer station
        for (i: Int in 0..changeLine) {
            // Set departure station name
            buttonView["depart"]!![i + 1].setOnClickListener {
                mySettings.setDepartStationDialog(goOrBack, textView["depart"]!![i], i, isSettings)
            }
            // Set arrival station name
            buttonView["arrive"]!![i + 1].setOnClickListener {
                mySettings.setArriveStationDialog(goOrBack, textView["arrive"]!![i], i, isSettings)
            }
            // Set line name
            buttonView["lineName"]!![i].setOnClickListener {
                mySettings.setLineNameDialog(goOrBack, textView["lineName"]!![i], null, i)
            }
            // Set ride time
            buttonView["rideTime"]!![i].setOnClickListener {
                mySettings.setRideTimeDialog(goOrBack, textView["rideTime"]!![i], i, intentArray[i])
            }
        }

        // Set up click listeners for transportation and transit time
        for (i: Int in 0..changeLine + 1) {
            // Set transportation method
            buttonView["transportation"]!![i].setOnClickListener {
                mySettings.setTransportationDialog(goOrBack, textView["transportation"]!![i], i, isSettings)
            }
            // Set transit time
            buttonView["transitTime"]!![i].setOnClickListener {
                mySettings.setTransitTimeDialog(goOrBack, textView["transitTime"]!![i], i)
            }
        }
        return binding.root
    }

    companion object {
        // Create intent array for timetable activities
        private fun changeTimetableActivity(context: Context, goOrBack: String): Array<Intent> {

            val myPreference = MyPreference(context)
            val changeLine: Int = myPreference.changeLine(goOrBack)
            val lineNumber: Array<Int> = Array(changeLine + 1){it}
            val calendar: Calendar = Calendar.getInstance()
            val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1

            // Intent extra keys
            val intentGoOrBack = "goorback"
            val intentLineNumber = "linenumber"
            val intentCurrentDay = "currentday"

            // Create intent array for each line
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