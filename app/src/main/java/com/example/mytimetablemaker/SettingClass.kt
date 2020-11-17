package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.parseColor
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager

class Setting {

    //スイッチの表示変更
    fun display2Switch(switch: Switch, key: String, checked: Boolean, changelinelayout: LinearLayout, settinglayout: LinearLayout) {
        //スイッチの状態に表示
        switch.isChecked = MainView().getRoot2Boolean(key, checked)
        changelinelayout.isVisible = switch.isChecked
        settinglayout.isVisible = switch.isChecked

        //スイッチの変更による表示の変更
        switch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    Application.context
            )
            sharedpreference.edit().putBoolean("dummy", true).apply()
            sharedpreference.edit().putBoolean(key, isChecked).apply()
            changelinelayout.isVisible = isChecked
            settinglayout.isVisible = isChecked
        }
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

    //乗換回数の設定用Dialogを表示する関数
    fun setChangeLineDialog(textview: TextView, context: Context, goorback: String) {
        val title: String = R.string.settingchangelinetitle.strings
        val key = "${goorback}changeline"
        val arraylist: Array<String> = R.array.changelinelist.arrayStrings
        val arrayvalue: Array<String> = R.array.changelinevalue.arrayStrings
        getSingleChoiceItemsDialog(textview, context, key, title, arraylist, arrayvalue)
    }

    //★リストを選択するラジオボタンDialogを表示する関数
    private fun getSingleChoiceItemsDialog(textview: TextView, context: Context, key: String, title: String,
                                           arraylist: Array<String>, arrayvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setSingleChoiceItems(arraylist, 0) { _, which ->
            saveChoiceValue(context, key, which, arrayvalue)
            textview.text = arraylist[which]
        }
        dialog.setNegativeButton((R.string.register), null)
        dialog.show()
    }

    //★ラジオボタンで選択した値を保存する関数
    private fun saveChoiceValue(context: Context, key: String, which: Int, arrayvalue: Array<String>) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, arrayvalue[which]).apply()
    }

    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setPrefLineNameDialog(textview1: TextView, textview2: TextView, context: Context, goorback: String, i: Int) {
        val key1 = "${goorback}line${(i + 1)}nameofline"
        val key2 = "${goorback}line${(i + 1)}colorofline"
        val title1 = "${R.string.settinglinenametitle.strings}${R.string.line.strings}${(i + 1)}"
        val title2: String = R.string.settinglinecolortitle.strings
        val linecolorlist: Array<String> = R.array.linecolorlist.arrayStrings
        val linecolorvalue: Array<String> = R.array.linecolorvalue.arrayStrings
        val linename: String = goorback.lineName(i, "")
        val edittext = EditText(context)
        setEditText20(edittext)
        edittext.setText(linename)
        setPrefEditTextDialog (textview1, textview2, context, edittext, key1, key2, title1, title2, linecolorlist, linecolorvalue)
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setPrefEditTextDialog(textview1: TextView, textview2: TextView, context: Context, edittext: EditText,
                                      key1: String, key2: String, title1: String, title2: String,
                                      arraylist: Array<String>, arrayvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title1)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key1)
                textview1.text = edittext.text.toString()
            }
        }
        dialog.setNeutralButton(context.getString(R.string.settinglinecolorbutton)) { _, _ ->
            if (edittext.text.toString() != "") {
                saveEditText(edittext, context, key1)
                textview1.text = edittext.text.toString()
            }
            setPrefChoiceColorsDialog(textview1, textview2, context, key2, title2, arraylist, arrayvalue)
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setPrefChoiceColorsDialog(textview1: TextView, textview2: TextView, context: Context, key: String, title: String,
                                          arraycolorlist: Array<String>, arraycolorvalue: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setSingleChoiceItems(arraycolorlist, 0) { _, which ->
            savePrefChoiceColor(textview1, textview2, context, key, which, arraycolorvalue)
        }
        dialog.setNegativeButton((R.string.register), null)
        dialog.show()
    }

    //★設定するカラーををPreferenceに保存し、TextViewおよびViewに表示する関数
    private fun savePrefChoiceColor(textview1: TextView, textview2: TextView, context: Context, key: String, which: Int, arraycolorvalue: Array<String>) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, arraycolorvalue[which]).apply()
        textview1.setTextColor(parseColor(arraycolorvalue[which]))
        textview2.setTextColor(parseColor(arraycolorvalue[which]))
    }

    //乗換時間を設定するDialogを表示する関数
    fun setPrefTransitTimeDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val title: String = "${R.string.settingtransittimetitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val key = "${goorback}transittime${i.e}"
        val hint: String = R.string.minite2hint.strings
        val savedtransittime: String = goorback.transitTime(i, "")
        getPrefEditTimeDialog(textview, context, key, title, hint, savedtransittime)
    }

    //★2桁の数字を入力する標準的なEditTextDialogの表示する関数
    private fun getPrefEditTimeDialog (textview: TextView, context: Context, key: String, title: String, hint: String, savedtext: String) {
        val edittext = EditText(context)
        edittext.setText(savedtext)
        setEditNumber2(edittext, hint)
        setPrefEditTimeDialog(textview, context, edittext, title, key)
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setPrefEditTimeDialog(textview: TextView, context: Context, edittext: EditText, title: String, key: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                prefSaveEditTime(edittext, context, key)
                textview.text = edittext.text.toString().addMinites
            }
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //乗車時間を設定するDialogを表示する関数
    fun setPrefRideTimeDialog(textview: TextView, context: Context, goorback: String, i: Int, intent: Intent) {
        val key = "${goorback}line${(i + 1)}ridetime"
        val title = "${R.string.settingridetimetitle.strings}${goorback.lineName(i, "")}"
        val hint: String = R.string.minite2hint.strings
        val neutraltitle: String = R.string.timetablesetting.strings
        val savedridetime: String = goorback.rideTime(i,"")
        getPrefEditTime2Dialog(textview, context, key, title, hint, neutraltitle, savedridetime, intent)
    }

    //2桁の数字を入力する標準的なEditTextDialogの表示する関数
    private fun getPrefEditTime2Dialog(textview: TextView, context: Context, key: String, title: String, hint: String,
                                       neutraltitle: String, savedtext: String, intent: Intent) {
        val edittext = EditText(context)
        edittext.setText(savedtext)
        setEditNumber2(edittext, hint)
        setPrefStartActivityEditTextDialog (textview, context, edittext, key, title, neutraltitle, intent)
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setPrefStartActivityEditTextDialog(textview: TextView, context: Context, edittext: EditText, key: String,
                                                   title: String, neutraltitle: String, intent: Intent) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setView(edittext)
        dialog.setPositiveButton(R.string.register) { _, _ ->
            if (edittext.text.toString() != "") {
                prefSaveEditTime(edittext, context, key)
                textview.text = edittext.text.toString().addMinites
            }
        }
        dialog.setNeutralButton(neutraltitle) { _, _ ->
            if (edittext.text.toString() != "") {
                prefSaveEditTime(edittext, context, key)
                textview.text = edittext.text.toString().addMinites
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        dialog.setNegativeButton((R.string.cancel), null)
        dialog.show()
    }

    //★EditTextをPreferenceに保存し、TextViewに表示する関数
    private fun prefSaveEditTime(edittext: EditText, context: Context, key: String) {
        val sharedpreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedpreference.edit().putString("dummy", "dummy").apply()
        sharedpreference.edit().putString(key, edittext.text.toString()).apply()
    }
}