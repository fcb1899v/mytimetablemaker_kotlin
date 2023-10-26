package com.example.mytimetablemaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity: AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Define class
        val myPreference = MyPreference(this)
        val myDate = MyDate()
        val admobClass = AdMob()
        val calendar: Calendar = Calendar.getInstance()
        val handler = Handler(Looper.getMainLooper())

        //Define parameter
        var isTimeStart = true
        var currentDay = 0
        var currentHHMMSS = 0
        var goOrBack12 = arrayOf("back1", "back2")
        var runnable = Runnable {}

        fun getFragmentView() {
            myDate.getCurrentDate(binding.currentDate, binding.currentTime)
            val displayDate = myDate.getLocalizeDate(binding.currentDate.text.toString(), "EEEMMMdyyyy")!!
            val displayTime = myDate.getLocalizeDate(binding.currentTime.text.toString(), "HH:mm:ss")!!
            calendar.time = displayDate
            currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
            currentHHMMSS = SimpleDateFormat("HHmmss", Locale.US).format(displayTime).toInt()
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment1, RouteFragment1.newInstance(currentDay, currentHHMMSS, goOrBack12[0]))
                .commitAllowingStateLoss()
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment2, RouteFragment2.newInstance(currentDay, currentHHMMSS, goOrBack12[1]))
                .commitAllowingStateLoss()
        }

        fun changeGoOrBack(goOrBack1: String, goOrBack2: String) {
            goOrBack12 = arrayOf(goOrBack1, goOrBack2)
            val back2display = myPreference.getRoute2Switch(goOrBack2)
            binding.RouteFragment2.visibility = if (back2display) { VISIBLE } else { GONE }
            binding.centerLine.visibility = if (back2display) { VISIBLE } else { GONE }
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment1, RouteFragment1.newInstance(currentDay, currentHHMMSS, goOrBack1))
                .commitAllowingStateLoss()
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment2, RouteFragment2.newInstance(currentDay, currentHHMMSS, goOrBack2))
                .commitAllowingStateLoss()
        }

        //To settings button
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //＜日付および時刻に関する設定＞

        //時刻の停止ボタンの設定（時刻が止まってない場合）
        binding.timeStartButton.setOnClickListener {
            isTimeStart = setAccentColor(binding.timeStartButton, binding.timeStopButton, true)
        }
        //時刻の開始ボタンの設定（時刻が止まっている場合）
        binding.timeStopButton.setOnClickListener {
            isTimeStart = setAccentColor(binding.timeStartButton, binding.timeStopButton, false)
        }

        //日付の選択および表示（時刻が止まっている場合）
        binding.currentDate.setOnClickListener {
            myDate.setDatePickerDialog(binding.currentDate, this, isTimeStart)
        }
        //時刻の選択および表示（時刻が止まっている場合）
        binding.currentTime.setOnClickListener {
            myDate.setTimePickerDialog(binding.currentTime, this, isTimeStart)
        }

        //外出画面から帰宅画面に遷移するときの表示変更
        binding.backButton.setOnClickListener {
            setAccentColor(binding.backButton, binding.goButton, true)
            changeGoOrBack("back1", "back2")
        }
        //帰宅画面から外出画面に遷移するときの表示変更
        binding.goButton.setOnClickListener {
            setAccentColor(binding.backButton, binding.goButton, false)
            changeGoOrBack("go1", "go2")
        }

        runnable = Runnable {
            if (isTimeStart) {
                getFragmentView()
            }
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)

        //Admob広告
        admobClass.setAdMob(binding.admobView, this)
    }

    private fun setAccentColor(onButton: Button, offButton: Button, isOn: Boolean): Boolean {
        onButton.setBackgroundResource(if (isOn) R.drawable.on_button else R.drawable.off_button)
        offButton.setBackgroundResource(if (isOn) R.drawable.off_button else R.drawable.on_button)
        return isOn
    }
}