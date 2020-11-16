package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.app.backup.BackupAgentHelper
import android.app.backup.SharedPreferencesBackupHelper
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.timetable.Application.Companion.context
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val mainview = MainView()
    private val mainviewdialog = MainViewDialog()
    private val admobclass = AdMobClass()
    private val calendar: Calendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Admob広告
        admobclass.setAdMob(findViewById(R.id.adview), this)

        //設定画面に移動
        settingimageview.setOnClickListener {
            startActivity(Intent(this, PreferenceActivity::class.java))
        }

        //＜日付および時刻に関する設定＞
        //時刻を止めるためのフラグ
        var timeflag = true

        //時刻の停止ボタンの設定（時刻が止まってない場合）
        timestopbutton.setOnClickListener {
            if (timeflag) { timeflag =
                    mainview.changeStartStop(timestopbutton, timestartbutton, true) }
        }
        //時刻の開始ボタンの設定（時刻が止まっている場合）
        timestartbutton.setOnClickListener {
            if (!timeflag) {timeflag =
                    mainview.changeStartStop(timestartbutton, timestopbutton, false)}
        }
        //日付の選択および表示（時刻が止まっている場合）
        currentdate.setOnClickListener {
            if (!timeflag) {mainviewdialog.setDatePickerDialog(currentdate, this)}
        }
        //時刻の選択および表示（時刻が止まっている場合）
        currenttime.setOnClickListener {
            if (!timeflag) {mainviewdialog.setTimePickerDialog(currenttime, this)}
        }

        //＜帰宅ルートと外出ルートの画面遷移時の表示変更＞
        //帰宅と外出を示すフラグ
        var gobackflag = true
        var goorback1 = "back1"
        var goorback2 = "back2"

        //帰宅画面から外出画面に遷移するときの表示変更
        gobutton.setOnClickListener {
            if (gobackflag) {gobackflag =
                    mainview.changeGoBack(gobutton, backbutton, true)}
            goorback1 = "go1"
            goorback2 = "go2"
        }
        //外出画面から帰宅画面に遷移するときの表示変更
        backbutton.setOnClickListener {
            if (!gobackflag) {gobackflag =
                    mainview.changeGoBack(backbutton, gobutton, false)}
            goorback1 = "back1"
            goorback2 = "back2"
        }

        runnable = Runnable {

            //現在時刻および現在日時を表示
            if (timeflag) {
                currentdate.text = mainview.localizeDateString(Date(), "EEEMMMdyyyy")
                currenttime.text = mainview.localizeDateString(Date(), "HH:mm:ss")
            }

            val displaydate: Date = mainview.getLocalizeDate(currentdate.text.toString(), "EEEMMMdyyyy")!!
            val displaytime: Date = mainview.getLocalizeDate(currenttime.text.toString(), "HH:mm:ss")!!

            //表示されている日時の曜日をInt型で取得(日：0 、月：1、火：2、水：3、木：4、金：5、土：6)
            calendar.time = displaydate
            val currentday: Int = calendar.get(Calendar.DAY_OF_WEEK)
            //表示されている時刻をInt型で取得
            val currenthhmmss: Int = SimpleDateFormat("HHmmss").format(displaytime).toInt()

            //帰宅または外出ルート1の表示（GoorBack1Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.goorback1fragment, GoorBack1Fragment.newInstance(currentday, currenthhmmss, goorback1))
                        .commitAllowingStateLoss()
            }
            //帰宅または外出ルート2の表示（Go2Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.goorback2fragment, GoorBack2Fragment.newInstance(currentday, currenthhmmss, goorback2))
                        .commitAllowingStateLoss()
            }

            val back2display: Boolean = mainview.getRoot2Boolean(goorback2 + "switch",false)
            goorback2fragment.visibility = if (back2display) { VISIBLE } else { GONE }
            centerLine.visibility = if (back2display) { VISIBLE } else { GONE }

            //1秒ごとに更新:
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }
}

//class MyPrefsBackupAgent: BackupAgentHelper() {
//    override fun onCreate() {
//        SharedPreferencesBackupHelper(this, "user_preference").also {
//            addHelper("prefs", it)
//        }
//    }
//}

/*
//Assetsフォルダ内のJSONファイルを内部ストレージのfilesにコピー
val jsonfilearray: Array<String> = arrayOf("setting.json",
    "back1timetable1.json", "back1timetable2.json", "back1timetable3.json",
    "back2timetable1.json", "back2timetable2.json", "back2timetable3.json",
    "go1timetable1.json", "go1timetable2.json", "go1timetable3.json",
    "go2timetable1.json", "go2timetable2.json", "go2timetable3.json"
)
var inputstream: InputStream
var outputstream: OutputStream
for (i: Int in 0 until jsonfilearray.size) {
    inputstream = assets.open(jsonfilearray[i])
    outputstream = openFileOutput(jsonfilearray[i], Context.MODE_PRIVATE)
    fileanddata.copyData(inputstream, outputstream)
}
 */

