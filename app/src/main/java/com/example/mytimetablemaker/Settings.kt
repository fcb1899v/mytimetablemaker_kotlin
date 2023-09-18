package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.parseColor
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView

class Settings (
    private val context: Context,
    private val goOrBack: String
){

    //クラスの呼び出し
    private val myPreference = MyPreference(context)

    //EditTextの書式設定（10文字に制限、中央配置、改行不可、黒色、サイズ20）
    private fun setEditText20(edittext: EditText) {
        edittext.apply{
            filters = arrayOf(InputFilter.LengthFilter(20))
            setHint(R.string.character10Hint)
            inputType = InputType.TYPE_CLASS_TEXT
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 20F
        }
    }

    //EditTextの書式設定（2桁の数字、中央配置、改行不可、黒色、サイズ24、指定したhintを表示）
    private fun setEditNumber2(edittext: EditText, hint: String) {
        edittext.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setHint(hint)
            inputType = InputType.TYPE_CLASS_TEXT
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 24F
        }
    }

    //乗換回数の設定用Dialogを表示する関数
    fun setChangeLineDialog(textview: TextView) {
        val title: String = R.string.settingsChangeLineTitle.strings
        val key = "${goOrBack}changeline"
        val arrayList: Array<String> = R.array.changeLineList.arrayStrings
        val arrayValue: Array<String> = R.array.changeLineValue.arrayStrings
        setSingleChoiceItemsAlertDialog(textview, key, title, arrayList, arrayValue)
    }

    //出発地名を設定するダイアログを表示する関数
    fun setDepartPointDialog(textview: TextView) {
        val edittext = EditText(context)
        val key: String = goOrBack.departPointKey
        val title: String = R.string.settingDepartPointTitle.strings
        val savedText: String = goOrBack.departPoint
        edittext.setText(savedText)
        setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, title, key)
    }

    //発車駅名を設定するダイアログを表示する関数
    fun setDepartStationDialog(textview: TextView, i: Int) {
        val edittext = EditText(context)
        val key = "${goOrBack}departstation${i + 1}"
        val title = "${R.string.settingStationName.strings}${R.string.departStation.strings}${i + 1}"
        val savedText: String = goOrBack.departStation(i)
        edittext.setText(savedText)
        setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, title, key)
    }

    //降車駅名を取得するダイアログ
    fun setArriveStationDialog(textview: TextView, i: Int) {
        val edittext = EditText(context)
        val key = "${goOrBack}arrivalstation${i + 1}"
        val title = "${R.string.settingStationName.strings}${R.string.arriveStation.strings}${i + 1}"
        val savedText: String = goOrBack.arriveStation(i)
        edittext.setText(savedText)
        setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, title, key)
    }

    //目的地名を取得するダイアログ
    fun setArrivePointDialog(textview: TextView) {
        val edittext = EditText(context)
        val key: String = goOrBack.arrivePointKey
        val title: String = R.string.settingDestinationTitle.strings
        val savedText: String = goOrBack.arrivePoint
        edittext.setText(savedText)
        setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, title, key)
    }

    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setLineNameDialog(textview: TextView, view: View, i: Int) {
        val edittext = EditText(context)
        val key1 = "${goOrBack}linename${i + 1}"
        val key2 = "${goOrBack}linecolor${i + 1}"
        val title1 = "${R.string.settingLineName.strings}${R.string.line.strings}${i + 1}"
        val title2: String = R.string.settingLineColorTitle.strings
        val lineColorList: Array<String> = R.array.lineColorList.arrayStrings
        val lineColorValue: Array<String> = R.array.lineColorValue.arrayStrings
        val lineName: String = goOrBack.lineName(i)
        edittext.setText(lineName)
        setEditText20(edittext)
        setEditTextPlusColorAlertDialog(textview, view, edittext, key1, key2, title1, title2, lineColorList, lineColorValue)
    }

    //移動手段を設定するDialogを表示する関数
    fun setTransportationDialog(textview: TextView, i: Int) {
        val key = "${goOrBack}transportation${i.e}"
        val title: String = "${R.string.settingTransportation.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goOrBack.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val transportationList: Array<String> = R.array.transportationList.arrayStrings
        setSingleChoiceItemsAlertDialog(textview, key, title, transportationList, transportationList)
    }

    //乗車時間を設定するDialogを表示する関数
    fun setRideTimeDialog(i: Int, intent: Intent) {
        val key = "${goOrBack}ridetime${i + 1}"
        val title = "${R.string.settingRideTime.strings}${goOrBack.lineName(i)}"
        val hint: String = R.string.minutes2Hint.strings
        val neutralTitle: String = R.string.timetableTitle.strings
        val savedRideTime: String = goOrBack.rideTime(i)
        val edittext = EditText(context)
        edittext.setText(savedRideTime)
        setEditNumber2(edittext, hint)
        setStartActivityEditTextAlertDialog (edittext, key, title, neutralTitle, intent)
    }

    //乗換時間を設定するDialogを表示する関数
    fun setTransitTimeDialog(i: Int) {
        val edittext = EditText(context)
        val key = "${goOrBack}transittime${i.e}"
        val title: String = "${R.string.settingTransitTime.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goOrBack.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val hint: String = R.string.minutes2Hint.strings
        val savedTransitTime: String = goOrBack.transitTime(i)
        edittext.setText(savedTransitTime)
        setEditNumber2(edittext, hint)
        setNotSaveEditTextAlertDialog(edittext, title, key)
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setEditTextAlertDialog(textview: TextView, edittext: EditText, title: String, key: String) {
        AlertDialog.Builder(context).apply{
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setStartActivityEditTextAlertDialog (
        edittext: EditText, key: String, title: String, neutralTitle: String, intent: Intent
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                }
            }
            setNeutralButton(neutralTitle) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.context.startActivity(intent)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★保存した値を表示しない標準的なEditTextDialog関数
    private fun setNotSaveEditTextAlertDialog(edittext: EditText, title: String, key: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setEditTextPlusColorAlertDialog (
        textview: TextView, view: View, edittext: EditText,
        key1: String, key2: String, title1: String, title2: String,
        arrayList: Array<String>, arrayValue: Array<String>
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key1, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
            }
            setNeutralButton(R.string.settingLineColorButton) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key1, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
                setChoiceColorAlertDialog(textview, view, key2, title2, arrayList, arrayValue)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setChoiceColorAlertDialog(
        textview: TextView, view: View, key: String, title: String,
        arrayColorList: Array<String>, arrayColorValue: Array<String>
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arrayColorList, 0) { _, which ->
                myPreference.prefSaveText(key, arrayColorValue[which])
                textview.setTextColor(parseColor(arrayColorValue[which]))
                view.setBackgroundColor(parseColor(arrayColorValue[which]))
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }



    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setPrefLineNameDialog(textview1: TextView, textview2: TextView, i: Int) {
        val key1 = "${goOrBack}line${(i + 1)}nameofline"
        val key2 = "${goOrBack}line${(i + 1)}colorofline"
        val title1 = "${R.string.settingLineName.strings}${R.string.line.strings}${(i + 1)}"
        val title2: String = R.string.settingLineColorTitle.strings
        val lineColorList: Array<String> = R.array.lineColorList.arrayStrings
        val lineColorValue: Array<String> = R.array.lineColorValue.arrayStrings
        val lineName: String = goOrBack.lineName(i)
        val edittext = EditText(context)
        edittext.setText(lineName)
        setEditText20(edittext)
        setPrefEditTextAlertDialog (textview1, textview2, edittext, key1, key2, title1, title2, lineColorList, lineColorValue)
    }

    //乗換時間を設定するDialogを表示する関数
    fun setPrefTransitTimeDialog(textview: TextView, i: Int) {
        val edittext = EditText(context)
        val title: String = "${R.string.settingTransitTime.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goOrBack.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val key = "${goOrBack}transittime${i.e}"
        val hint: String = R.string.minutes2Hint.strings
        val savedTransitTime: String = goOrBack.transitTime(i)
        edittext.setText(savedTransitTime)
        setEditNumber2(edittext, hint)
        setPrefEditTimeAlertDialog(textview, edittext, title, key)
    }

    //乗車時間を設定するDialogを表示する関数
    fun setPrefRideTimeDialog(textview: TextView, i: Int, intent: Intent) {
        val edittext = EditText(context)
        val key = "${goOrBack}line${(i + 1)}ridetime"
        val title = "${R.string.settingRideTime.strings}${goOrBack.lineName(i)}"
        val hint: String = R.string.minutes2Hint.strings
        val neutralTitle: String = R.string.timetableTitle.strings
        val savedRideTime: String = goOrBack.rideTime(i)
        edittext.setText(savedRideTime)
        setEditNumber2(edittext, hint)
        setPrefEditTextChangeActivityAlertDialog(textview, edittext, key, title, neutralTitle, intent)
    }

    //★リストを選択するラジオボタンDialogを表示する関数
    private fun setSingleChoiceItemsAlertDialog(
        textview: TextView, key: String, title: String,
        arraylist: Array<String>, arrayValue: Array<String>
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraylist, 0) { _, which ->
                myPreference.prefSaveText(key, arrayValue[which])
                textview.text = arraylist[which]
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setPrefEditTextAlertDialog(
        textview1: TextView, textview2: TextView, edittext: EditText,
        key1: String, key2: String, title1: String, title2: String,
        arraylist: Array<String>, arrayValue: Array<String>
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key1, edittext.text.toString())
                    textview1.text = edittext.text.toString()
                }
            }
            setNeutralButton(R.string.settingLineColorButton.strings) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key1, edittext.text.toString())
                    textview1.text = edittext.text.toString()
                }
                setPrefChoiceColorAlertDialog(textview1, textview2, key2, title2, arraylist, arrayValue)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setPrefChoiceColorAlertDialog(
        textview1: TextView, textview2: TextView, key: String, title: String,
        arrayColorList: Array<String>, arrayColorValue: Array<String>
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arrayColorList, 0) { _, which ->
                myPreference.prefSaveText(key, arrayColorValue[which])
                textview1.setTextColor(parseColor(arrayColorValue[which]))
                textview2.setTextColor(parseColor(arrayColorValue[which]))
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setPrefEditTimeAlertDialog(
        textview: TextView, edittext: EditText, title: String, key: String
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinutes
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setPrefEditTextChangeActivityAlertDialog(
        textview: TextView, edittext: EditText, key: String,
        title: String, neutralTitle: String, intent: Intent
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinutes
                }
            }
            setNeutralButton(neutralTitle) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinutes
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }
}