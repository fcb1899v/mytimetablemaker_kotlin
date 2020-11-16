package com.example.timetable

import android.content.SharedPreferences
import android.graphics.Color.parseColor
import android.text.format.DateFormat.getBestDateTimePattern
import android.widget.Button
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class MainViewClass {

    //　日時をローカライズ表示のStringで取得する関数
    fun localizeDateString(date: Date, skeleton: String): String {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.format(date)
    }

    //　日時をローカライズ表示のDateで取得する関数
    fun getLocalizeDate(date: String, skeleton: String): Date? {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.parse(date)
    }

    //開始ボタンおよび停止ボタンを押したときの表示変更をする関数
    fun changeStartStop(button1: Button, button2: Button, timeflag: Boolean): Boolean {
        button1.setTextColor(parseColor(R.string.coloraccent.strings))
        button2.setTextColor(parseColor(R.string.lightgray.strings))
        return !timeflag
    }
    //帰宅ボタンおよび外出ボタンを押したときの表示変更をする関数
    fun changeGoBack(button1: Button, button2: Button, timeflag: Boolean): Boolean {
        button1.setTextColor(parseColor(R.string.primarydark.strings))
        button2.setTextColor(parseColor(R.string.lightgray.strings))
        return !timeflag
    }

    //MainActivityの表示された曜日をInt型で取得(日：0、月：1、火：2、水：3、木：4、金：5、土：6)
    //fun getDayOfWeekInt(displaydate: String): Int {
    //祝日
    //val gcalendar = CalendarApp.getCalenderById("ja.japanese#holiday@group.v.calender.google.com")
    //if (gcalender.getEventsForDay(date).length > 0) { return 7 }　//祝：7
    //}

    //設定画面のルート2を表示するSwitchPreferenceの状態に応じたBooleanを読み出す関数
    fun getRoot2Boolean(key: String, defaultflag: Boolean): Boolean {
        val sharedpreference: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(Application.context)
        return when (val root2switch: Boolean? = sharedpreference.getBoolean(key, defaultflag)) {
            null -> defaultflag
            else -> root2switch
        }
    }

}