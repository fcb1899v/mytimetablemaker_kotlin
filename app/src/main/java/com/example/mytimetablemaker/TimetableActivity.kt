package com.example.mytimetablemaker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.parseColor
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_timetable.*
import java.io.InputStream

class TimetableActivity: AppCompatActivity() {

    //フラグメントから渡されるデータの初期化
    private var goorback: String = ""
    private var linenumber: Int = 0
    private var currentday: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)

        //フラグメントから受け渡されたデータ
        val intentgoorback = "goorback"
        val intentlinenumber = "linenumber"
        val intentcurrentday = "currentday"

        //時刻表の時刻表示のTextViewを配列として初期取得
        var weekdayhour: Array<TextView?> = arrayOf()
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour04))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour05))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour06))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour07))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour08))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour09))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour10))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour11))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour12))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour13))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour14))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour15))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour16))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour17))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour18))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour19))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour20))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour21))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour22))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour23))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour24))
        weekdayhour += arrayOf(findViewById<TextView?>(R.id.weekdayhour25))

        //フラグメントから渡されたデータ
        intent?.apply {
            goorback = getStringExtra(intentgoorback).toString()
            linenumber = getIntExtra(intentlinenumber, 1)
            currentday = getIntExtra(intentcurrentday, 1)
        }

        //アクションバーの表示設定
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.timetabletitle)

        //時刻表のタイトル
        val departstation: String = goorback.departStation(linenumber, "${R.string.depsta.strings}${linenumber + 1}")
        val linename: String = goorback.lineName(linenumber, "${R.string.line.strings}${linenumber + 1}")
        val arrivestation: String = goorback.arriveStation(linenumber, "${R.string.arrsta.strings}${linenumber + 1}")
        val timetabletitle: String = linename.timeTableTitle(arrivestation)
        timetablestationname.text = departstation
        timetablestationname2.text = timetabletitle

        //平日または土日の表示
        tablelineweekday.text = currentday.dayOrEndString(getString(R.string.weekend), getString(R.string.weekday))
        tablelineweekday.setTextColor(currentday.dayOrEndColor(Color.RED, Color.WHITE))
        daybutton.text = currentday.dayOrEndString(getString(R.string.weekday), getString(R.string.weekend))
        daybutton.setTextColor(currentday.dayOrEndColor(parseColor(R.string.primarydark.strings), Color.RED))

        val timetable = Timetable()

        //時刻の表示
        val timetablearray: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, currentday)
        for (i: Int in 0..21) { weekdayhour[i]?.text = timetablearray[i] }

        //平日と土日祝の表示の切替
        daybutton.setOnClickListener {
            when (daybutton.text) {
                getString(R.string.weekday) -> {
                    timetable.getTimetableText(tablelineweekday, 1)
                    timetable.getTimetableButton(daybutton, 1)
                    val timetablechange: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, 1)
                    for (i: Int in 0..21) { weekdayhour[i]?.text = timetablechange[i] }
                }
                else -> {
                    timetable.getTimetableText(tablelineweekday, 0)
                    timetable.getTimetableButton(daybutton, 0)
                    val timetablechange: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, 0)
                    for (i: Int in 0..21) { weekdayhour[i]?.text = timetablechange[i] }
                }
            }
        }

        //時刻表の時刻の設定
        for (i: Int in 0..21) {
            weekdayhour[i]?.setOnClickListener {
                val daynumber: Int = when (daybutton.text) { getString(R.string.weekday) -> 0 else -> 1 }
                timetable.makeTimetableDialog (weekdayhour[i]!!, this, goorback, linenumber, i + 4, daynumber, weekdayhour)
            }
        }

        val pictureselectbutton: Button = findViewById(R.id.pictureselectbutton)
        pictureselectbutton.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri: Uri ->
                        val inputStream: InputStream? = contentResolver?.openInputStream(uri)
                        val picture: Bitmap = BitmapFactory.decodeStream(inputStream)
                        val pictureview: ImageView = findViewById(R.id.PictureView)
                        pictureview.setImageBitmap(picture)
                    }
                } catch (e: Exception) {
                    //Toast.makeText(this, "エラーが発生しました"), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}