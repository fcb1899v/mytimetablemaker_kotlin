package com.example.mytimetablemaker
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.Application.Companion.context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Boolean.parseBoolean

//Resourcesデータをどこでも参照できるようにするクラス
class Application: android.app.Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = this
    }
}

//String型Resourcesデータの取得
val Int.strings: String get() = context.getString(this)
//StringArray型Resourcesデータの取得
val Int.arrayStrings: Array<String> get() = context.resources.getStringArray(this)
//String型Resourcesデータから色データを取得
val Int.setColor: Int get() = parseColor(this.strings)
//String?がnullの場合に設定した初期を取得
fun String?.stringIfNullOrNot(defaultValue: String): String =
    when(this) {
        null -> defaultValue
        else -> this
    }
//Boolean?がnullの場合にfalseを取得
val Boolean?.booleanIfNullOrNot: Boolean get() =
    when(this) {
        null -> false
        else -> this
    }
//
fun Task<DocumentSnapshot>.taskResult(key: String, defaultValue: String) =
    this.result?.get(key).toString().stringIfNullOrNot(defaultValue)
//
fun Task<DocumentSnapshot>.taskResultBoolean(key: String) =
    parseBoolean(this.result?.get(key).toString()).booleanIfNullOrNot

//SharedPreferenceに保存された値を取得:key
fun String.savedText(defaultText: String): String =
    when (
        val edittext: String? = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(this, defaultText)
    ) {
        "", "未設定", "Not set", null -> defaultText
        else -> edittext.toString()
    }
//SharedPreferenceに保存された値を取得:key
val String.savedInt: Int get() =
    when (
        val editTextIntOrNull: Int? = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(this, "0")?.toIntOrNull()
    ) {
        null -> 0
        else -> editTextIntOrNull
    }

//乗換回数:goOrBack
val String.changeLine: Int get() = "${this}changeline".savedText("0").toInt()
//乗換回数の表示:goOrBack
val String.changeLineString: String get() =
    when (this.changeLine) {
        1 -> R.string.once.strings
        2 -> R.string.twice.strings
        else -> R.string.zero.strings
    }

//SharedPreference

//出発地を取得:goOrBack
val String.departPoint: String get() =
    this.goOrBackString(
        "destination".savedText(R.string.office.strings),
        "departurepoint".savedText(R.string.home.strings),
    )
val String.settingsDepartPoint: String get() =
    this.goOrBackString(
        "destination".savedText(R.string.notSet.strings),
        "departurepoint".savedText(R.string.notSet.strings),
    )

//目的地を取得:goOrBack
val String.arrivePoint: String get() =
    this.goOrBackString(
        "departurepoint".savedText(R.string.notSet.strings),
        "destination".savedText(R.string.notSet.strings),
    )
val String.settingsArrivePoint: String get() =
    this.goOrBackString(
        "departurepoint".savedText(R.string.office.strings),
        "destination".savedText(R.string.home.strings),
    )

//乗車駅名を取得:goOrBack
fun String.departStation(i: Int): String =
    "${this}departstation${i + 1}".savedText("${R.string.depSta.strings}${i + 1}")
fun String.settingsDepartStation(i: Int): String =
    "${this}departstation${i + 1}".savedText(R.string.notSet.strings)
val String.getDepartStation: Array<String> get() =
    (0 .. this.changeLine).map{this.departStation(it)}.toTypedArray()
val String.getDepartStationFirestore: Array<String> get() =
    (0 .. 2).map{this.departStation(it)}.toTypedArray()


//降車駅名を取得:goOrBack
fun String.arriveStation(i: Int): String =
        "${this}arrivalstation${i + 1}".savedText("${R.string.arrSta.strings}${i + 1}")
fun String.settingsArriveStation(i: Int): String =
    "${this}arrivalstation${i + 1}".savedText(R.string.notSet.strings)
val String.getArriveStation: Array<String> get() =
    (0 .. this.changeLine).map{this.arriveStation(it)}.toTypedArray()
val String.getArriveStationFirestore: Array<String> get() =
    (0 .. 2).map{this.arriveStation(it)}.toTypedArray()

//路線名を取得:goOrBack
fun String.lineName(i: Int): String =
    "${this}linename${i + 1}".savedText("${R.string.line.strings}${i + 1}")
fun String.settingsLineName(i: Int): String =
    "${this}linename${i + 1}".savedText(R.string.notSet.strings)
val String.getLineName: Array<String> get() =
    (0 .. this.changeLine).map{this.lineName(it)}.toTypedArray()
val String.getLineNameFirestore: Array<String> get() =
    (0 .. 2).map{this.lineName(it)}.toTypedArray()

//路線カラーを取得:goOrBack
fun String.lineColor(i: Int): String =
    "${this}linecolor${i + 1}".savedText(R.string.colorAccent.strings)
fun String.settingsLineColor(i: Int): String =
    "${this}linecolor${i + 1}".savedText(R.string.lightGray.strings)
val String.getLineColor: Array<String> get() =
    (0 .. this.changeLine).map{this.lineColor(it)}.toTypedArray()
val String.getLineColorFirestore: Array<String> get() =
    (0 .. 2).map{this.lineColor(it)}.toTypedArray()

//乗車時間を取得:goOrBack
fun String.rideTime(i: Int): String =
        "${this}ridetime${i + 1}".savedText("0")
fun String.rideTimeInt(i: Int): Int =
    "${this}ridetime${i + 1}".savedInt
fun String.settingsRideTime(i: Int): String =
    "${this}ridetime${i + 1}".savedText(R.string.notSet.strings)
val String.getRideTimeFirestore: Array<String> get() =
    (0 .. 2).map{this.rideTime(it)}.toTypedArray()

//乗換手段を取得:goOrBack
fun String.transportation(i: Int): String =
        "${this}transportation${i.e}".savedText(R.string.walking.strings)
fun String.settingsTransportation(i: Int): String =
    "${this}transportation${i.e}".savedText(R.string.notSet.strings)
val String.getTransportation: Array<String> get() =
    (0 .. this.changeLine + 1).map{this.transportation(it)}.toTypedArray()
val String.getTransportationFirestore: Array<String> get() =
    (0 .. 3).map{this.transportation(it)}.toTypedArray()

//乗換時間を取得:goOrBack
fun String.transitTime(i: Int): String =
        "${this}transittime${i.e}".savedText("0")
fun String.transitTimeInt(i: Int): Int =
        "${this}transittime${i.e}".savedInt
fun String.settingsTransitTime(i: Int): String =
    "${this}transittime${i.e}".savedText(R.string.notSet.strings)
val String.getTransitTimeFirestore: Array<String> get() =
    (0 .. 3).map{this.transitTime(it)}.toTypedArray()

//ルート2の表示の取得:goOrBack
val String.getRoute2Switch: Boolean get() = PreferenceManager
    .getDefaultSharedPreferences(context)
    .getBoolean("${this}switch", true)

//:goOrBack
fun String.goOrBackString(backString: String, goString: String): String =
    when (this) {
        "back1", "back2" -> backString
        else -> goString
    }

//Key:goOrBack
val String.departPointKey: String get() = this.goOrBackString("destination", "departurepoint")
val String.arrivePointKey: String get() = this.goOrBackString("departurepoint", "destination")
val String.departPointDefault: String get() = this.goOrBackString(R.string.office.strings, R.string.home.strings)
val String.arrivePointDefault: String get() = this.goOrBackString(R.string.home.strings, R.string.office.strings)

//:goOrBack
val String.otherGoOrBack: String get() = when (this) {
    "go1" -> "go2"
    "back2" -> "back1"
    "go2" -> "go1"
    else -> "back2"
}

//:goOrBack
fun String.transitStation(i: Int): String = when (i) {
    0 -> this.arrivePoint
    1 -> this.departPoint
    else -> this.arriveStation(i - 2)
}
//
fun String.changeWord(changeWord: String, i: Int):String = when (i) {
    0 -> this
    else -> changeWord
}

//
val Int.e:String get() = when (this) {
    0-> "e"
    else -> "$this"
}
//[分]の単位を追加
val String.addMinutes: String get() = when (this) {
    "", R.string.notSet.strings -> this
    else -> "$this ${R.string.minutesUnit.strings}"
}
