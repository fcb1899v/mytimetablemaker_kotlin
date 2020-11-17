package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.parseColor
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.preference.PreferenceManager
import java.util.*
import java.util.Calendar.*

class MainViewDialog {

    private val mainview = MainView()

    //＜日付の設定＞
    //(a)標準的なDatePickerDialogを表示する関数
    fun setDatePickerDialog(textview: TextView, context: Context) {
        val inputdate: Calendar = getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            inputdate.set(DAY_OF_MONTH, day)
            inputdate.set(MONTH, month)
            inputdate.set(YEAR, year)
            textview.text = mainview.localizeDateString(inputdate.time, "EEEMMMdyyyy")
        }
        DatePickerDialog(context, dateSetListener,
                inputdate.get(YEAR), inputdate.get(MONTH), inputdate.get(DAY_OF_MONTH)
        ).show()
    }

    //＜日時の設定＞
    //(b)標準的なTimePickerDialogを表示する関数
    @SuppressLint("SetTextI18n")
    fun setTimePickerDialog(textview: TextView, context: Context) {
        val inputtime: Calendar = getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
            inputtime.set(HOUR_OF_DAY, hour)
            inputtime.set(MINUTE, minute)
            textview.text = mainview.localizeDateString(inputtime.time, "HH:mm") + ":00"
        }
        TimePickerDialog(context, timeSetListener,
                inputtime.get(HOUR_OF_DAY), inputtime.get(MINUTE), true
        ).show()
    }

    //EditTextの書式設定（10文字に制限、中央配置、黒色、サイズ20）
    private fun setEditText20(edittext: EditText) {
        edittext.filters = arrayOf(InputFilter.LengthFilter(20))
        edittext.setHint(R.string.character10hint)
        edittext.gravity = Gravity.CENTER
        edittext.setTextColor(Color.BLACK)
        edittext.textSize = 20F
    }

    //EditTextの書式設定（2桁の数字、中央配置、黒色、サイズ24、指定したhintを表示）
    private fun setEditNumber2(edittext: EditText, hint: String) {
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        edittext.filters = arrayOf(InputFilter.LengthFilter(2))
        edittext.hint = hint
        edittext.gravity = Gravity.CENTER
        edittext.setTextColor(Color.BLACK)
        edittext.textSize = 24F
    }

    //★EditTextをPreferenceに保存する関数
    private fun saveEditText(edittext: EditText, context: Context, key: String) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, edittext.text.toString()).apply()
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setEditTextDialog(
            textview: TextView, edittext: EditText, context: Context, title: String, key: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key)
                textview.text = edittext.text.toString()
            }
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //★ラジオボタンで選択した値を保存する関数
    private fun saveChoiceValue(context: Context, key: String, which: Int, arrayvalue: Array<String>) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, arrayvalue[which]).apply()
    }

    //★リストを選択するラジオボタンDialogを表示する関数
    private fun getSingleChoiceItemsDialog(
            textview: TextView, context: Context, key: String, title: String, arraylist: Array<String>, arrayvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setSingleChoiceItems(arraylist, 0) { _, which ->
            saveChoiceValue(context, key, which, arrayvalue)
            textview.text = arraylist[which]
        }
        dialog.setNegativeButton((R.string.register), null)
        dialog.show()
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setStartActivityEditTextDialog (edittext: EditText, context: Context, key: String,
                                                title: String, neutraltitle: String, intent: Intent) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key)
            }
        }
        dialog.setNeutralButton(neutraltitle) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.context.startActivity(intent)
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //★保存した値を表示しない標準的なEditTextDialog関数
    private fun onlySetEditTextDialog(edittext: EditText, context: Context, title: String, key: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key)
            }
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //出発地名を設定するダイアログを表示する関数
    fun setDeparturePointDialog(textview: TextView, context: Context, goorback: String) {
        val edittext = EditText(context)
        val key: String = goorback.goOrBackString("destination", "departurepoint")
        val title: String = R.string.settingdepartplacetitle.strings
        val savedtext: String = goorback.departPoint("", "")
        edittext.setText(savedtext)
        setEditText20(edittext)
        setEditTextDialog(textview, edittext, context, title, key)
    }

    //発車駅名を設定するダイアログを表示する関数
    fun setDepartStationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key = "${goorback}line${i + 1}departstation"
        val title = "${R.string.settingstationnametitle.strings}${R.string.departstation.strings}${i + 1}"
        val savedtext: String = goorback.departStation(i, "")
        edittext.setText(savedtext)
        setEditText20(edittext)
        setEditTextDialog(textview, edittext, context, title, key)
    }

    //降車駅名を取得するダイアログ
    fun setArriveStationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key = "${goorback}line${i + 1}arrivestation"
        val title = "${R.string.settingstationnametitle.strings}${R.string.arrivestation.strings}${i + 1}"
        val savedtext: String = goorback.arriveStation(i, "")
        edittext.setText(savedtext)
        setEditText20(edittext)
        setEditTextDialog(textview, edittext, context, title, key)
    }

    //目的地名を取得するダイアログ
    fun setArrivalPointDialog(textview: TextView, context: Context, goorback: String) {
        val edittext = EditText(context)
        val key: String = goorback.goOrBackString("departurepoint", "destination")
        val title: String = R.string.settingdestinationtitle.strings
        val savedtext: String = goorback.arrivePoint("", "")
        edittext.setText(savedtext)
        setEditText20(edittext)
        setEditTextDialog(textview, edittext, context, title, key)
    }

    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setLineNameDialog(textview: TextView, view: View, context: Context, goorback: String, i: Int) {
        val key1 = "${goorback}line${i + 1}nameofline"
        val key2 = "${goorback}line${i + 1}colorofline"
        val title1 = "${R.string.settinglinenametitle.strings}${R.string.line.strings}${i + 1}"
        val title2: String = R.string.settinglinecolortitle.strings
        val linecolorlist: Array<String> = R.array.linecolorlist.arrayStrings
        val linecolorvalue: Array<String> = R.array.linecolorvalue.arrayStrings
        val linename: String = goorback.lineName(i, "")
        val edittext = EditText(context)
        setEditText20(edittext)
        edittext.setText(linename)
        setEditTextDialog2 (textview, view, edittext, context, key1, key2, title1, title2, linecolorlist, linecolorvalue)
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setEditTextDialog2 (textview: TextView, view: View, edittext: EditText, context: Context,
                                    key1: String, key2: String, title1: String, title2: String,
                                    arraylist: Array<String>, arrayvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title1)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key1)
                textview.text = edittext.text.toString()
            }
        }
        dialog.setNeutralButton(R.string.settinglinecolorbutton) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key1)
                textview.text = edittext.text.toString()
            }
            setChoiceColorsDialog(textview, view, context, key2, title2, arraylist, arrayvalue)
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setChoiceColorsDialog(textview: TextView, view: View, context: Context, key: String, title: String,
                                      arraycolorlist: Array<String>, arraycolorvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setSingleChoiceItems(arraycolorlist, 0) { _, which ->
            saveChoiceColor(textview, view, context, key, which, arraycolorvalue)
        }
        dialog.setNegativeButton((R.string.register), null)
        dialog.show()
    }

    //★設定するカラーををPreferenceに保存し、TextViewおよびViewに表示する関数
    private fun saveChoiceColor(textview: TextView, view: View, context: Context,
                                key: String, which: Int, arraycolorvalue: Array<String>) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, arraycolorvalue[which]).apply()
        textview.setTextColor(parseColor(arraycolorvalue[which]))
        view.setBackgroundColor(parseColor(arraycolorvalue[which]))
    }

    //移動手段を設定するDialogを表示する関数
    fun setTransportationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val key = "${goorback}transportation${i.e}"
        val title: String = "${R.string.settingtransportationtitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val transportationlist: Array<String> = R.array.transportationlist.arrayStrings
        getSingleChoiceItemsDialog(textview, context, key, title, transportationlist, transportationlist)
    }

    //乗車時間を設定するDialogを表示する関数
    fun setRideTimeDialog(context: Context, goorback: String, i: Int, intent: Intent) {
        val key = "${goorback}line${i + 1}ridetime"
        val title = "${R.string.settingridetimetitle.strings}${goorback.lineName(i, "${R.string.line.strings}${i + 1}")}"
        val hint: String = R.string.minite2hint.strings
        val neutraltitle: String = R.string.timetablesetting.strings
        val savedridetime: String = goorback.rideTime(i, "")
        getEditTimeDialog(context, key, title, hint, neutraltitle, savedridetime, intent)
    }

    //2桁の数字を入力する標準的なEditTextDialogの表示する関数
    private fun getEditTimeDialog (context: Context, key: String, title: String, hint: String,
                                   neutraltitle: String, savedtext: String, intent: Intent) {
        val edittext = EditText(context)
        edittext.setText(savedtext)
        setEditNumber2(edittext, hint)
        setStartActivityEditTextDialog (edittext, context, key, title, neutraltitle, intent)
    }

    //乗換時間を設定するDialogを表示する関数
    fun setTransitTimeDialog(context: Context, goorback: String, i: Int) {
        val key = "${goorback}transittime${i.e}"
        val title: String = "${R.string.settingtransittimetitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val hint: String = R.string.minite2hint.strings
        val savedtransittime: String = goorback.transitTime(i, "")
        getEditTime2Dialog(context, key, title, hint, savedtransittime)
    }

    //★2桁の数字を入力する標準的なEditTextDialogの表示する関数
    private fun getEditTime2Dialog (
            context: Context, key: String, title: String,
            hint: String, savedtext: String) {
        val edittext = EditText(context)
        edittext.setText(savedtext)
        setEditNumber2(edittext, hint)
        onlySetEditTextDialog(edittext, context, title, key)
    }
}