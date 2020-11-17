package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

//各種設定
class VariousSettingsFragment: Fragment() {

    private val setting = Setting()
    private val mainviewdialog = MainViewDialog()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goorback: String? = arguments?.getString("BUNDLE_KEY_GOORBACK")
        val changeline: Int = goorback!!.changeLine

        //出発地・到着地を設定するボタンの設定
        val departpointbutton: TextView? = view.findViewById<TextView?>(R.id.departpointbutton)
        val arrivepointbutton: TextView? = view.findViewById<TextView?>(R.id.arrivepointbutton)

        //設定した出発地・到着地を表示するテキストの設定
        val departpointtext: TextView? = view.findViewById<TextView?>(R.id.departpointtext)
        val arrivepointtext: TextView? = view.findViewById<TextView?>(R.id.arrivepointtext)

        //乗車駅を設定するボタンの設定
        var departstationbutton: Array<TextView?> = arrayOf()
        departstationbutton += view.findViewById<TextView?>(R.id.departstation1button)
        departstationbutton += view.findViewById<TextView?>(R.id.departstation2button)
        departstationbutton += view.findViewById<TextView?>(R.id.departstation3button)

        //設定した乗車駅を表示するテキストの設定
        var departstationtext: Array<TextView?> = arrayOf()
        departstationtext += view.findViewById<TextView?>(R.id.departstation1text)
        departstationtext += view.findViewById<TextView?>(R.id.departstation2text)
        departstationtext += view.findViewById<TextView?>(R.id.departstation3text)

        //降車駅を設定するボタンの設定
        var arrivestationbutton: Array<TextView?> = arrayOf()
        arrivestationbutton += view.findViewById<TextView?>(R.id.arrivestation1button)
        arrivestationbutton += view.findViewById<TextView?>(R.id.arrivestation2button)
        arrivestationbutton += view.findViewById<TextView?>(R.id.arrivestation3button)

        //設定した降車駅を表示するテキストの設定
        var arrivestationtext: Array<TextView?> = arrayOf()
        arrivestationtext += view.findViewById<TextView?>(R.id.arrivestation1text)
        arrivestationtext += view.findViewById<TextView?>(R.id.arrivestation2text)
        arrivestationtext += view.findViewById<TextView?>(R.id.arrivestation3text)

        //路線名・路線カラーを設定するボタンの設定
        var linenamebutton: Array<TextView?> = arrayOf()
        linenamebutton += view.findViewById<TextView?>(R.id.linename1button)
        linenamebutton += view.findViewById<TextView?>(R.id.linename2button)
        linenamebutton += view.findViewById<TextView?>(R.id.linename3button)

        //設定した路線名・路線カラーを表示するテキストの設定
        var linenametext: Array<TextView?> = arrayOf()
        linenametext += view.findViewById<TextView?>(R.id.linename1text)
        linenametext += view.findViewById<TextView?>(R.id.linename2text)
        linenametext += view.findViewById<TextView?>(R.id.linename3text)

        //乗車時間を設定するボタンの設定
        var ridetimebutton: Array<TextView?> = arrayOf()
        ridetimebutton += view.findViewById<TextView?>(R.id.ridetime1button)
        ridetimebutton += view.findViewById<TextView?>(R.id.ridetime2button)
        ridetimebutton += view.findViewById<TextView?>(R.id.ridetime3button)

        //設定した乗車時間を表示するテキストの設定
        var ridetimetext: Array<TextView?> = arrayOf()
        ridetimetext += view.findViewById<TextView?>(R.id.ridetime1text)
        ridetimetext += view.findViewById<TextView?>(R.id.ridetime2text)
        ridetimetext += view.findViewById<TextView?>(R.id.ridetime3text)

        //移動方法を設定するボタンの設定
        var transportationbutton: Array<TextView?> = arrayOf()
        transportationbutton += view.findViewById<TextView?>(R.id.transportationebutton)
        transportationbutton += view.findViewById<TextView?>(R.id.transportation1button)
        transportationbutton += view.findViewById<TextView?>(R.id.transportation2button)
        transportationbutton += view.findViewById<TextView?>(R.id.transportation3button)

        //設定した移動方法を表示するテキストの設定
        var transportationtext: Array<TextView?> = arrayOf()
        transportationtext += view.findViewById<TextView?>(R.id.transportationetext)
        transportationtext += view.findViewById<TextView?>(R.id.transportation1text)
        transportationtext += view.findViewById<TextView?>(R.id.transportation2text)
        transportationtext += view.findViewById<TextView?>(R.id.transportation3text)

        //乗換時間を設定するボタンの設定
        var transittimebutton: Array<TextView?> = arrayOf()
        transittimebutton += view.findViewById<TextView?>(R.id.transittimeebutton)
        transittimebutton += view.findViewById<TextView?>(R.id.transittime1button)
        transittimebutton += view.findViewById<TextView?>(R.id.transittime2button)
        transittimebutton += view.findViewById<TextView?>(R.id.transittime3button)

        //設定した乗換時間を表示するテキストの設定
        var transittimetext: Array<TextView?> = arrayOf()
        transittimetext += view.findViewById<TextView?>(R.id.transittimeetext)
        transittimetext += view.findViewById<TextView?>(R.id.transittime1text)
        transittimetext += view.findViewById<TextView?>(R.id.transittime2text)
        transittimetext += view.findViewById<TextView?>(R.id.transittime3text)

        //路線2に関するレイアウト
        var station2layout: Array<LinearLayout?> = arrayOf()
        station2layout += view.findViewById<LinearLayout?>(R.id.station2layout)
        station2layout += view.findViewById<LinearLayout?>(R.id.linename2layout)
        station2layout += view.findViewById<LinearLayout?>(R.id.ridetime2layout)
        station2layout += view.findViewById<LinearLayout?>(R.id.transportation2layout)
        station2layout += view.findViewById<LinearLayout?>(R.id.transittime2layout)

        //路線3に関するレイアウト
        var station3layout: Array<LinearLayout?> = arrayOf()
        station3layout += view.findViewById<LinearLayout?>(R.id.station3layout)
        station3layout += view.findViewById<LinearLayout?>(R.id.linename3layout)
        station3layout += view.findViewById<LinearLayout?>(R.id.ridetime3layout)
        station3layout += view.findViewById<LinearLayout?>(R.id.transportation3layout)
        station3layout += view.findViewById<LinearLayout?>(R.id.transittime3layout)

        //乗換回数に応じたレイアウトの表示・非表示
        for (i: Int in 0..4) {
            station2layout[i]!!.visibility = if (changeline > 0) {View.VISIBLE} else {View.GONE}
            station3layout[i]!!.visibility = if (changeline > 1) {View.VISIBLE} else {View.GONE}
        }

        //設定値の表示
        departpointtext!!.text = goorback.departPoint(R.string.notset.strings, R.string.notset.strings)
        arrivepointtext!!.text = goorback.arrivePoint(R.string.notset.strings, R.string.notset.strings)
        transportationtext[0]!!.text  = goorback.transportation(0, R.string.notset.strings)
        transittimetext[0]!!.text = goorback.transitTime(0, R.string.notset.strings).addMinites
        for (i: Int in 0..changeline) {
            departstationtext[i]!!.text = goorback.departStation(i, R.string.notset.strings)
            arrivestationtext[i]!!.text = goorback.arriveStation(i, R.string.notset.strings)
            linenametext[i]!!.text = goorback.lineName(i, R.string.notset.strings)
            linenametext[i]!!.setTextColor(parseColor(goorback.lineColor(i, R.string.lightgray.strings)))
            ridetimetext[i]!!.text = goorback.rideTime(i, R.string.notset.strings).addMinites
            ridetimetext[i]!!.setTextColor(parseColor(goorback.lineColor(i, R.string.lightgray.strings)))
            transportationtext[i + 1]!!.text = goorback.transportation(i + 1, R.string.notset.strings)
            transittimetext[i + 1]!!.text = goorback.transitTime(i + 1, R.string.notset.strings).addMinites
        }

        val intentarray: Array<Intent> =
                changeTimetableActivity((context as FragmentActivity?)!!, goorback, 1)

        //出発地名の設定
        departpointbutton!!.setOnClickListener {
            mainviewdialog.setDeparturePointDialog(departpointtext, (context as FragmentActivity?)!!, goorback)
        }
        //到着地名の設定
        arrivepointbutton!!.setOnClickListener {
            mainviewdialog.setArrivalPointDialog(arrivepointtext, (context as FragmentActivity?)!!, goorback)
        }

        for (i: Int in 0..changeline) {
            //乗車駅名の設定
            departstationbutton[i]!!.setOnClickListener {
                mainviewdialog.setDepartStationDialog(departstationtext[i]!!, (context as FragmentActivity?)!!, goorback, i)
            }
            //降車駅名の設定
            arrivestationbutton[i]!!.setOnClickListener {
                mainviewdialog.setArriveStationDialog(arrivestationtext[i]!!, (context as FragmentActivity?)!!, goorback, i)
            }
            //路線名の設定
            linenamebutton[i]!!.setOnClickListener {
                setting.setPrefLineNameDialog(linenametext[i]!!, ridetimetext[i]!!, (context as FragmentActivity?)!!, goorback, i)
            }
            //乗車時間の設定
            ridetimebutton[i]!!.setOnClickListener {
                setting.setPrefRideTimeDialog(ridetimetext[i]!!, (context as FragmentActivity?)!!, goorback, i, intentarray[i])
            }
        }

        for (i: Int in 0..changeline + 1) {
            //移動手段の設定
            transportationbutton[i]!!.setOnClickListener {
                mainviewdialog.setTransportationDialog(transportationtext[i]!!, (context as FragmentActivity?)!!, goorback, i)
            }
            //乗換時間の設定
            transittimebutton[i]!!.setOnClickListener {
                setting.setPrefTransitTimeDialog(transittimetext[i]!!, (context as FragmentActivity?)!!, goorback, i)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_varioussettings, container, false)
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goorback: String, currentday: Int): Array<Intent> {
            //乗換回数
            val changeline: Int = goorback.changeLine
            //路線番号
            val linenumber: Array<Int> = Array(changeline + 1){it}

            //時刻表アクティビティにデータを送るためのキー
            val intentgoorback = "goorback"
            val intentlinenumber = "linenumber"
            val intentcurrentday = "currentday"

            var intentarray: Array<Intent> = arrayOf()
            for (i: Int in 0..changeline) {
                intentarray += Intent(context, TimetableActivity::class.java).apply {
                    putExtra(intentgoorback, goorback)
                    putExtra(intentlinenumber, linenumber[i])
                    putExtra(intentcurrentday, currentday)
                }
            }
            return intentarray
        }
    }
}