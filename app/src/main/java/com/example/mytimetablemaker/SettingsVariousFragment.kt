package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentVarioussettingsBinding

//各種設定
class SettingsVariousFragment: Fragment() {

    //ViewBinding
    private lateinit var binding: FragmentVarioussettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVarioussettingsBinding.inflate(layoutInflater, container, false)

        val goOrBack: String? = arguments?.getString("BUNDLE_KEY_GOORBACK")
        val changeLine: Int = goOrBack!!.changeLine
        val intentArray: Array<Intent> = changeTimetableActivity(requireContext(), goOrBack)

        //クラスの呼び出し
        val settings = Settings(requireContext(), goOrBack)

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
            "transportation" to arrayOf(binding.transportationTextEnd, binding.transportationText1, binding.transportationText2, binding.transportationText3),
            "transitTime" to arrayOf(binding.transitTimeTextEnd, binding.transitTimeText2, binding.transitTimeText2, binding.transitTimeText3)
        )

        val buttonView: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.departPointButton, binding.departStationButton1, binding.departStationButton2, binding.departStationButton3),
            "arrive" to arrayOf(binding.destinationButton, binding.arriveStationButton1, binding.arriveStationButton2, binding.arriveStationButton3),
            "lineName" to arrayOf(binding.lineNameButton1, binding.lineNameButton2, binding.lineNameButton3),
            "rideTime" to arrayOf(binding.rideTimeButton1, binding.rideTimeButton2, binding.rideTimeButton3),
            "transportation" to arrayOf(binding.transportationButtonEnd, binding.transportationButton1, binding.transportationButton2, binding.transportationButton3),
            "transitTime" to arrayOf(binding.transitTimeButtonEnd, binding.transitTimeButton2, binding.transitTimeButton2, binding.transitTimeButton3)
        )

        //設定値の表示
        textView["depart"]!![0].text = goOrBack.settingsDepartPoint
        textView["arrive"]!![0].text = goOrBack.settingsArrivePoint

        for (i: Int in 0..changeLine) {
            textView["depart"]!![i + 1].text = goOrBack.settingsDepartStation(i)
            textView["arrive"]!![i + 1].text = goOrBack.settingsArriveStation(i)
            textView["lineName"]!![i].apply{
                text = goOrBack.settingsLineName(i)
                setTextColor(parseColor(goOrBack.settingsLineColor(i)))
            }
            textView["rideTime"]!![i].apply {
                text = goOrBack.settingsRideTime(i).addMinutes
                setTextColor(parseColor(goOrBack.settingsLineColor(i)))
            }
        }

        for (i: Int in 0..changeLine + 1) {
            textView["transportation"]!![i].text = goOrBack.settingsTransportation(i)
            textView["transitTime"]!![i].text = goOrBack.settingsTransitTime(i).addMinutes
        }

        //出発地名の設定
        buttonView["depart"]!![0].setOnClickListener {
            settings.setDepartPointDialog(textView["depart"]!![0])
        }
        //到着地名の設定
        buttonView["arrive"]!![0].setOnClickListener {
            settings.setArrivePointDialog(textView["arrive"]!![0])
        }

        for (i: Int in 0..changeLine) {
            //乗車駅名の設定
            buttonView["depart"]!![i + 1].setOnClickListener {
                settings.setDepartStationDialog(textView["depart"]!![i + 1], i)
            }
            //降車駅名の設定
            buttonView["arrive"]!![i + 1].setOnClickListener {
                settings.setArriveStationDialog(textView["arrive"]!![i + 1], i)
            }
            //路線名の設定
            buttonView["lineName"]!![i].setOnClickListener {
                settings.setPrefLineNameDialog(textView["lineName"]!![i], textView["rideTime"]!![i], i)
            }
            //乗車時間の設定
            buttonView["rideTime"]!![i].setOnClickListener {
                settings.setPrefRideTimeDialog(textView["rideTime"]!![i], i, intentArray[i])
            }
        }

        for (i: Int in 0..changeLine + 1) {
            //移動手段の設定
            buttonView["transportation"]!![i].setOnClickListener {
                settings.setTransportationDialog(textView["transportation"]!![i], i)
            }
            //乗換時間の設定
            buttonView["transitTime"]!![i].setOnClickListener {
                settings.setPrefTransitTimeDialog(textView["transitTime"]!![i], i)
            }
        }
        return binding.root
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goOrBack: String): Array<Intent> {
            //乗換回数
            val changeLine: Int = goOrBack.changeLine
            //路線番号
            val lineNumber: Array<Int> = Array(changeLine + 1){it}

            //時刻表アクティビティにデータを送るためのキー
            val intentGoOrBack = "goorback"
            val intentLineNumber = "linenumber"
            val intentCurrentDay = "currentday"

            var intentArray: Array<Intent> = arrayOf()
            for (i: Int in 0..changeLine) {
                intentArray += Intent(context, TimetableActivity::class.java).apply {
                    putExtra(intentGoOrBack, goOrBack)
                    putExtra(intentLineNumber, lineNumber[i])
                    putExtra(intentCurrentDay, 1)
                }
            }
            return intentArray
        }
    }
}