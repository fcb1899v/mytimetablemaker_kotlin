package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color.*
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.preference.PreferenceManager

class Timetable {

    //全時刻のPreferenceに保存されている発車時刻の文字列を配列で取得する関数
    fun getTimetableStringArray(goorback: String, i: Int, currentday: Int): Array<String> {
        var timetablearray: Array<String> = arrayOf()
        for (hour: Int in 4..25) {
            timetablearray += goorback.timeTableString(i, hour, currentday)
        }
        return timetablearray
    }

    //平日と土日の切替表示
    fun getTimetableText (textview: TextView, currentday: Int) {
        textview.text = currentday.dayOrEndString(R.string.weekend.strings, R.string.weekday.strings)
        textview.setTextColor(currentday.dayOrEndColor(RED, WHITE))
    }
    //平日と土日のボタン切替表示
    fun getTimetableButton (button: Button, currentday: Int) {
        button.text = currentday.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings)
        button.setTextColor(currentday.dayOrEndColor(parseColor(R.string.primarydark.strings), RED))
    }

    private fun timetableListArray(hour: Int, currentday: Int): Array<String> = arrayOf(
            "${hour - 1}${R.string.jidai.strings}",
            "${hour + 1}${R.string.jidai.strings}",
            currentday.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings),
            R.string.otherrouteline.strings + "1",
            R.string.otherrouteline.strings + "2",
            R.string.otherrouteline.strings + "3"
    )

    private fun timetableValueArray(goorback: String, linenumber: Int, hour: Int, currentday: Int): Array<String> =arrayOf(
            goorback.timeTableString(linenumber, hour - 1, currentday),
            goorback.timeTableString(linenumber, hour + 1, currentday),
            goorback.timeTableString(linenumber, hour, currentday.otherDay),
            goorback.otherGoOrBack.timeTableString(0, hour, currentday),
            goorback.otherGoOrBack.timeTableString(1, hour, currentday),
            goorback.otherGoOrBack.timeTableString(2, hour, currentday)
    )

    //時刻表に2桁の数字を入力するDialogの表示する関数
    fun makeTimetableDialog (textview: TextView, context: Context, goorback: String, linenumber: Int,
                             hour: Int, currentday: Int, textviewarray: Array<TextView?>) {
        val currenttext: String = textview.text.toString()
        val key = "${goorback}line${linenumber + 1}h${hour.addZeroTime}${currentday.addWeekend}setting"
        val title1: String = R.string.inputtime.strings
        val title2: String = R.string.copytime.strings
        val hint: String = R.string.inputtimehint.strings
        val edittext = EditText(context)
        setEditNumber2(edittext, hint)
        val arraylist: Array<String> = timetableListArray(hour, currentday)
        val arrayvalue: Array<String> = timetableValueArray(goorback, linenumber, hour, currentday)
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title1)
        dialog.setView(edittext)
        dialog.setNegativeButton(R.string.add) { _, _ ->
            textview.text = addTimeFromTimetable(context, edittext, currenttext, key)
            makeTimetableDialog (textview, context, goorback, linenumber, hour, currentday, textviewarray)
        }
        dialog.setPositiveButton(R.string.delete) { _, _ ->
            textview.text = deleteTimeFromTimetable(context, edittext, currenttext, key)
            makeTimetableDialog (textview, context, goorback, linenumber, hour, currentday, textviewarray)
        }
        dialog.setNeutralButton(R.string.copytime) { _, _ ->
            getChoiceListItemsDialog(textview, context, key, title2, arraylist, arrayvalue, textviewarray, goorback, linenumber, currentday)
        }
        dialog.show()
    }

    //EditTextの書式設定（2桁の数字、中央配置、黒色、サイズ24、指定したhintを表示）
    private fun setEditNumber2(edittext: EditText, hint: String) {
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        edittext.filters = arrayOf(InputFilter.LengthFilter(2))
        edittext.hint = hint
        edittext.gravity = Gravity.CENTER
        edittext.setTextColor(BLACK)
        edittext.textSize = 24F
    }

    //時刻を追加する関数
    private fun addTimeFromTimetable(context: Context, edittext: EditText, currenttext: String, key:String): String {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, currenttext.addTime(edittext.text.toString())).apply()
        return currenttext.addTime(edittext.text.toString())
    }

    //時刻を削除する関数
    private fun deleteTimeFromTimetable(context: Context, edittext: EditText, currenttext: String, key:String): String {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, currenttext.deleteTime(edittext.text.toString())).apply()
        return currenttext.deleteTime(edittext.text.toString())
    }

    //★リストを選択するDialogを表示する関数
    private fun getChoiceListItemsDialog(textview: TextView, context: Context, key: String, title: String,
                                         arraylist: Array<String>, arrayvalue: Array<String>, textviewarray: Array<TextView?>,
                                         goorback: String, linenumber: Int, currentday: Int) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        var selectnumber = 0
        dialog.setSingleChoiceItems(arraylist, 0) { _, which ->
            saveChoiceValue(context, key, which, arrayvalue)
            selectnumber = which
            textview.text = arrayvalue[which]
        }
        dialog.setNegativeButton(R.string.cancel) { _, _ ->
        }
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            if (selectnumber in 2..5) {
                allCopyTimetableDialog(textviewarray, context, selectnumber, goorback, linenumber, currentday)
            }
        }
        dialog.show()
    }

    private fun allCopyKey(goorback: String, linenumber: Int, hour: Int, currentday: Int, selectnumber: Int): String = when(selectnumber){
        3 -> goorback.otherGoOrBack.timeTableKey(0, hour, currentday)
        4 -> goorback.otherGoOrBack.timeTableKey(1, hour, currentday)
        5 -> goorback.otherGoOrBack.timeTableKey(2, hour, currentday)
        else -> goorback.timeTableKey(linenumber, hour, currentday.otherDay)
    }

    private fun allCopyTimetableDialog(textviewarray: Array<TextView?>, context: Context, selectnumber: Int,
                                       goorback: String, linenumber: Int, currentday: Int) {
        val title: String = R.string.copyalltime.strings
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setNegativeButton(R.string.yes) { _, _ ->
            for (hour in 4..25) {
                val selectkey: String = goorback.timeTableKey(linenumber, hour, currentday)
                val copykey: String = allCopyKey(goorback, linenumber, hour, currentday, selectnumber)
                saveText(copykey.savedText(""), context, selectkey)
                textviewarray[hour - 4]!!.text = copykey.savedText("")
            }
        }
        dialog.setPositiveButton(R.string.no) { _, _ ->
        }
        dialog.show()
    }

    //★EditTextをPreferenceに保存する関数
    private fun saveText(text: String, context: Context, key: String) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, text).apply()
    }

    //★ラジオボタンで選択した値を保存する関数
    private fun saveChoiceValue(context: Context, key: String, which: Int, arrayvalue: Array<String>) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, arrayvalue[which]).apply()
    }

}