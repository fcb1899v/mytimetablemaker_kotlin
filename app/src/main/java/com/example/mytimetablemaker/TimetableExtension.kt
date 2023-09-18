package com.example.mytimetablemaker

//時刻表タイトルを取得
fun String.timeTableTitle(arriveStation: String) =
        "($this ${R.string.colon.strings} $arriveStation${R.string.houmen.strings})"

//内部ストレージに保存された各時間の時刻表データをStringとして取得
fun String.timeTableKey(i: Int, hour: Int, currentDay: Int): String =
        "${this}timetable${i + 1}hour${hour.addZeroTime}${currentDay.weekDayOrEnd}"

//内部ストレージに保存された各時間の時刻表データをStringとして取得
fun String.timeTableString(i: Int, hour: Int, currentDay: Int): String =
        timeTableKey(i, hour, currentDay).savedText("").timeSorting.timeString

//内部ストレージに保存された各時間の時刻表データをInt配列として取得
fun String.timeTableArrayInt(i: Int, hour: Int, currentDay: Int): Array<Int> =
        "${this}timetable${i + 1}hour${hour.addZeroTime}${currentDay.weekDayOrEnd}".savedText("").timeSorting.timeArrayInt(hour)
//全時刻のPreferenceに保存されている発車時刻の文字列を配列で取得する関数
fun String.getTimetableStringArray(i: Int, currentDay: Int): Array<String> =
        (4..25).map{this.timeTableString(i, it, currentDay)}.toTypedArray()

//
val Int.weekDayOrEnd: String get() =
        when(this) { 0, 6 -> "weekend" else -> "weekday" }
//
fun Int.dayOrEndString(weekendString: String, weekdayString: String): String =
        when (this) { 0, 6 -> weekendString else -> weekdayString }
//
fun Int.dayOrEndColor(weekendColor: Int, weekdayColor: Int): Int =
        when (this) { 0, 6 -> weekendColor else -> weekdayColor }
//
val Int.otherDay: Int get() =
        when (this) { 0, 6 -> 1 else -> 0 }

//
fun String.timeArrayInt(hour: Int): Array<Int> = if (this != "") { this
        .split(" ")
        .asSequence()
        .distinct()
        .takeWhile { it != "" }
        .map { it.toInt() }
        .takeWhile { it < 60 }
        .map { it + hour * 100 }
        .toList()
        .toTypedArray()
        .sortedArray()
} else {
    arrayOf()
}

//
val String.timeString: String get() = if (this != "") { this
        .split(" ")
        .distinct()
        .takeWhile { it != "" }
        .map{ it.toInt() }
        .takeWhile { it < 60 }
        .toTypedArray()
        .sortedArray()
        .joinToString(" ") { it.toString() }
} else {
    ""
}

//時刻表の時刻の追加
fun String.addTime(time: String): String = "$this $time"
        .removePrefix(" ")
        .split(" ")
        .asSequence()
        .distinct()
        .takeWhile { it != "" }
        .map { it.toInt() }
        .takeWhile { it < 60 }
        .sorted()
        .joinToString(" ") { it.toString() }

//時刻表の時刻の削除
fun String.deleteTime(time: String): String = this
        .split(" ")
        .filter { it != time }
        .joinToString(" ") { it }

//時刻データを整理
val String.timeSorting: String get() = this
        .replace("+","")
        .replace("-","")
        .replace("*","")
        .replace("/","")
        .replace("(","")
        .replace(")","")
        .replace(",","")
        .replace(".","")
        .replace(";","")
        .replace("N","")
        .replace("#","")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
