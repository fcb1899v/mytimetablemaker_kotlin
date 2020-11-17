package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class GoOrBack2Fragment : Fragment() {

    //MainActivityから受け渡されるデータのパラメータ
    private val ARG_currentday = "currentday"
    private val ARG_currenthhmmss = "currenthhmmss"
    private val ARG_goorback2 = "back2"

    //MainActivityから受け渡されるデータの初期化
    private var currentday: Int = 0
    private var currenthhmmss: Int = 0
    private var goorback2: String = ""

    //クラスの呼び出し
    private val mainfragment = MainFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //表示する出発地名・目的地名のTextViewの設定
        val departurepointview: TextView? = view.findViewById<TextView?>(R.id.stationName120)
        val arrivalpointview: TextView? = view.findViewById<TextView?>(R.id.stationName12e)

        //表示する乗車駅名のTextViewの設定
        var departstationview: Array<TextView?> = arrayOf()
        departstationview += view.findViewById<TextView?>(R.id.stationName121)
        departstationview += view.findViewById<TextView?>(R.id.stationName123)
        departstationview += view.findViewById<TextView?>(R.id.stationName125)

        //表示する降車駅名のTextViewの設定
        var arrivestationview: Array<TextView?> = arrayOf()
        arrivestationview += view.findViewById<TextView?>(R.id.stationName122)
        arrivestationview += view.findViewById<TextView?>(R.id.stationName124)
        arrivestationview += view.findViewById<TextView?>(R.id.stationName126)

        //表示する路線名のTextViewの設定
        var linenameview: Array<TextView?> = arrayOf()
        linenameview += view.findViewById<TextView?>(R.id.lineName121)
        linenameview += view.findViewById<TextView?>(R.id.lineName123)
        linenameview += view.findViewById<TextView?>(R.id.lineName125)

        //表示する路線のViewの設定
        var linelineview: Array<View?> = arrayOf()
        linelineview += view.findViewById<View?>(R.id.lineLine121)
        linelineview += view.findViewById<View?>(R.id.lineLine123)
        linelineview += view.findViewById<View?>(R.id.lineLine125)

        //表示する移動手段方法のTextViewの設定
        var transportview: Array<TextView?> = arrayOf()
        transportview += view.findViewById<TextView?>(R.id.lineName12e)
        transportview += view.findViewById<TextView?>(R.id.lineName120)
        transportview += view.findViewById<TextView?>(R.id.lineName122)
        transportview += view.findViewById<TextView?>(R.id.lineName124)

        //表示する移動手段のViewの設定
        var transitline: Array<View?> = arrayOf()
        transitline += view.findViewById<View?>(R.id.lineLine12e)
        transitline += view.findViewById<View?>(R.id.lineLine120)
        transitline += view.findViewById<View?>(R.id.lineLine122)
        transitline += view.findViewById<View?>(R.id.lineLine124)

        //表示するレイアウトの設定
        var layoutview: Array<LinearLayout?> = arrayOf()
        layoutview += view.findViewById<LinearLayout?>(R.id.linearlayout122)
        layoutview += view.findViewById<LinearLayout?>(R.id.linearlayout123)

        //表示する時刻のTextViewの設定
        var timeview: Array<TextView?> = arrayOf()
        timeview += view.findViewById<TextView?>(R.id.startTime120)
        timeview += view.findViewById<TextView?>(R.id.startTime12e)
        timeview += view.findViewById<TextView?>(R.id.startTime121)
        timeview += view.findViewById<TextView?>(R.id.startTime122)
        timeview += view.findViewById<TextView?>(R.id.startTime123)
        timeview += view.findViewById<TextView?>(R.id.startTime124)
        timeview += view.findViewById<TextView?>(R.id.startTime125)
        timeview += view.findViewById<TextView?>(R.id.startTime126)

        //表示するカウントダウンのTextViewの設定
        val countdownview: TextView? = view.findViewById<TextView?>(R.id.countdown12)

        //MainActivityから渡されたデータ
        val currentday: Int = currentday
        val currenthhmmss: Int = currenthhmmss
        val goorback2: String = goorback2

        mainfragment.mainFun(requireContext(), currentday, currenthhmmss, goorback2,
            departurepointview, departstationview, arrivestationview, arrivalpointview,
            linenameview, linelineview, transportview, transitline, layoutview, timeview, countdownview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentday = it.getInt(ARG_currentday)
            currenthhmmss = it.getInt(ARG_currenthhmmss)
            goorback2 = it.getString(ARG_goorback2)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goorback2, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(currentday: Int, currenthhmmss: Int, goorback2: String) = GoOrBack2Fragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_currentday, currentday)
                putInt(ARG_currenthhmmss, currenthhmmss)
                putString(ARG_goorback2, goorback2)
            }
        }
    }
}

/*
//＜時刻表に関するJSONファイルの取得＞
//設定用JSONファイルの読込み
val readsettingfile: String = fileanddata.readJSONFile(context!!.filesDir, "setting.json")
//読込みたいJSONファイルのリスト
val jsonfile: Array<String> = fileanddata.makeJSONFileArray(goorback, changeline)
//時刻表に関するJSONファイルの読込み
val readjsonfile: Array<String> = fileanddata.readJSONFileArray(context!!.filesDir, jsonfile)
//時刻表
val timetable: Array<Array<Int>> = dateandtime.getTimetableArrayFromJson(readjsonfile, currentday)
//乗換時間
val walktime: Array<Int> = dateandtime.getWalkTimeArray(readsettingfile, goorback)
//乗車時間
val ridetime: Array<Int> = fileanddata.getSettingIntArray(readjsonfile, "ridetime")

//東海道線対応
val backtoukaidouridetime = 77 //東海道線の乗車時間
val backslasttime = 2255       //新幹線の終電時間
val backtlasttime = 2404       //東海道線の終電時間
if (changeline > 1) {
    if (time[2][0] in backslasttime + 1..backtlasttime) {
        time[2][2] += backtoukaidouridetime - ridetime[2]
    }
}

//京浜東北線対応
if (changeline > 0) {
val yamanotekeihintohokulinename = "JR山手線/京浜東北線"
linename3!!.text = yamanotekeihintohokulinename
lineline3!!.setBackgroundResource(R.drawable.gradient_color)
}
//東海道線対応
if (changeline > 1) {
val toukaidoulinename = "JR東海道線"
val toukaidoulinecolor = "#F68B1E"
if (time[2][0] in backslasttime + 1..backtlasttime) {
linename5!!.text = toukaidoulinename
linename5.setTextColor(Color.parseColor(toukaidoulinecolor))
lineline5!!.setBackgroundColor(Color.parseColor(toukaidoulinecolor))
}
}
*/

