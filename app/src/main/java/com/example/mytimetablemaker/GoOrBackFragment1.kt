package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentGoorback1Binding

class GoOrBack1Fragment : Fragment() {

    //MainActivityから受け渡されるパラメータ
    private val argCurrentDay = "currentday"
    private val argCurrentHHMMSS = "currenthhmmss"
    private val argGoOrBack1 = "goorback1"

    //MainActivityから渡されたデータ
    private var currentDay: Int = 0
    private var currentHHMMSS: Int = 0
    private var goOrBack1: String = ""

    //クラスの呼び出し
    private val mainFragment = MainFragment()

    //ViewBinding
    private lateinit var binding: FragmentGoorback1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoorback1Binding.inflate(layoutInflater, container, false)

        val textView1: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.stationName110, binding.stationName111, binding.stationName113, binding.stationName115),
            "arrive" to arrayOf(binding.stationName11e, binding.stationName112, binding.stationName114, binding.stationName116),
            "line" to arrayOf(binding.lineName111, binding.lineName113, binding.lineName115),
            "transport" to arrayOf(binding.lineName11e, binding.lineName110, binding.lineName112, binding.lineName114),
            "departTime" to  arrayOf(binding.startTime110, binding.startTime111, binding.startTime113, binding.startTime115),
            "arriveTime" to  arrayOf(binding.startTime11e, binding.startTime112, binding.startTime114, binding.startTime116),
            "countdown" to arrayOf(binding.countdown11),
        )

        val view1: Map<String, Array<View>> = mapOf(
            "line" to arrayOf(binding.lineLine111, binding.lineLine113, binding.lineLine115),
            "transit" to arrayOf(binding.lineLine11e, binding.lineLine110, binding.lineLine112, binding.lineLine114),
        )

        //表示するレイアウトの設定
        val layoutView1: Array<LinearLayout> =
            arrayOf(binding.linearlayout112, binding.linearlayout113)

        //MainActivityから渡されたデータ
        val currentDay: Int = currentDay
        val currentHHMMSS: Int = currentHHMMSS
        val goOrBack1: String = goOrBack1

        mainFragment.mainFun(requireContext(), currentDay, currentHHMMSS, goOrBack1, textView1, view1, layoutView1)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentDay = it.getInt(argCurrentDay)
            currentHHMMSS = it.getInt(argCurrentHHMMSS)
            goOrBack1 = it.getString(argGoOrBack1)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentDay: Int, currentHHMMSS: Int, goOrBack1: String) =
            GoOrBack1Fragment().apply {
                arguments = Bundle().apply {
                    putInt(argCurrentDay, currentDay)
                    putInt(argCurrentHHMMSS, currentHHMMSS)
                    putString(argGoOrBack1, goOrBack1)
                }
            }
    }
}