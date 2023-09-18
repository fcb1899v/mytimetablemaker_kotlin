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
import java.util.Date
import java.util.Locale

class MainActivity: AppCompatActivity() {

    //クラスの定義
    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val date = MyDate()
    private val admobClass = AdMob()
    private val calendar: Calendar = Calendar.getInstance()

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //時刻を止めるためのフラグ
        var timeFlag = true

        //帰宅と外出を示すフラグ
        var goOrBackFlag = true
        var goOrBack1 = "back1"
        var goOrBack2 = "back2"

        val button: Map<String, Button> = mapOf(
            "date" to binding.currentDate,
            "time" to binding.currentTime,
            "start" to binding.timeStartButton,
            "stop" to binding.timeStopButton,
            "back" to binding.backButton,
            "go" to binding.goButton,
        )

        //ステータスバーの設定
        window.statusBarColor = getColor(R.color.colorPrimary)

        //設定画面に移動
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //＜日付および時刻に関する設定＞

        //時刻の停止ボタンの設定（時刻が止まってない場合）
        button["stop"]?.setOnClickListener {
            if (timeFlag) {
                timeFlag = false
                button["start"]?.setBackgroundResource(R.drawable.off_button)
                button["stop"]?.setBackgroundResource(R.drawable.on_button)
            }
        }
        //時刻の開始ボタンの設定（時刻が止まっている場合）
        button["start"]?.setOnClickListener {
            if (!timeFlag) {
                timeFlag = true
                button["start"]?.setBackgroundResource(R.drawable.on_button)
                button["stop"]?.setBackgroundResource(R.drawable.off_button)
            }
        }

        //日付の選択および表示（時刻が止まっている場合）
        button["date"]?.setOnClickListener {
            if (!timeFlag) {
                date.setDatePickerDialog(button["date"]!!, this)
            }
        }
        //時刻の選択および表示（時刻が止まっている場合）
        button["time"]?.setOnClickListener {
            if (!timeFlag) {
                date.setTimePickerDialog(button["time"]!!, this)
            }
        }

        //外出画面から帰宅画面に遷移するときの表示変更
        button["back"]?.setOnClickListener {
            if (!goOrBackFlag) {
                goOrBackFlag = true
                goOrBack1 = "back1"
                goOrBack2 = "back2"
                button["back"]?.setBackgroundResource(R.drawable.on_button)
                button["go"]?.setBackgroundResource(R.drawable.off_button)
            }
        }
        //帰宅画面から外出画面に遷移するときの表示変更
        button["go"]?.setOnClickListener {
            if (goOrBackFlag) {
                goOrBackFlag = false
                goOrBack1 = "go1"
                goOrBack2 = "go2"
                button["back"]?.setBackgroundResource(R.drawable.off_button)
                button["go"]?.setBackgroundResource(R.drawable.on_button)
            }
        }

        runnable = Runnable {

            //現在時刻および現在日時を表示
            if (timeFlag) {
                button["date"]?.text = date.localizeDateString(Date(), "EEEMMMdyyyy")
                button["time"]?.text = date.localizeDateString(Date(), "HH:mm:ss")
            }

            val displayDate: Date = date.getLocalizeDate(button["date"]?.text.toString(), "EEEMMMdyyyy")!!
            val displayTime: Date = date.getLocalizeDate(button["time"]?.text.toString(), "HH:mm:ss")!!

            //表示されている日時の曜日をInt型で取得(日：0 、月：1、火：2、水：3、木：4、金：5、土：6)
            calendar.time = displayDate
            val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1
            //表示されている時刻をInt型で取得
            val currentHHMMSS: Int = SimpleDateFormat("HHmmss", Locale.US).format(displayTime).toInt()

            //帰宅または外出ルート1の表示（goOrBack1Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.goOrBack1Fragment, GoOrBack1Fragment.newInstance(currentDay, currentHHMMSS, goOrBack1))
                    .commitAllowingStateLoss()
            }
            //帰宅または外出ルート2の表示（Go2Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.goOrBack2Fragment, GoOrBack2Fragment.newInstance(currentDay, currentHHMMSS, goOrBack2))
                    .commitAllowingStateLoss()
            }

            val back2display: Boolean = goOrBack2.getRoute2Switch
            binding.goOrBack2Fragment.visibility = if (back2display) { VISIBLE } else { GONE }
            binding.centerLine.visibility = if (back2display) { VISIBLE } else { GONE }

            //1秒ごとに更新:
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)

        //Admob広告
        admobClass.setAdMob(binding.admobView, this)
    }
}