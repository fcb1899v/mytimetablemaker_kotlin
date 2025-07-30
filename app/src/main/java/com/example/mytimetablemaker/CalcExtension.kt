package com.example.mytimetablemaker

//＜Time Conversion＞
// Convert Int time HHMM to MM
val Int.HHMMtoMM: Int get() = this / 100 * 60 + this % 100
// Convert Int time MM to HHMM
val Int.MMtoHHMM: Int get() = this / 60 * 100 + this % 60
// Convert Int time MMSS to SS
//val Int.MMSStoSS: Int get() = this / 100 * 60 + this % 100
// Convert Int time SS to MMSS
//val Int.SStoMMSS: Int get() = this / 60 * 100 + this % 60
// Convert Int time HHMMSS to SS
val Int.HHMMSStoSS: Int get() = this / 10000 * 3600 + (this % 10000) / 100 * 60 + this % 100
// Convert Int time SS to HHMMSS
val Int.SStoHHMMSS:Int get() = this / 3600 * 10000 + (this % 3600) / 60 * 100 + this % 60
// Convert Int time HHMMSS to MMSS
val Int.HHMMSStoMMSS: Int get() = (this / 10000 * 60 + (this % 10000) / 100) * 100 + this % 100
// Convert Int time MMSS to HHMMSS
//val Int.MMSStoHHMMSS: Int get() = (this / 100 / 60) * 10000 + (this / 100 % 60) * 100 + this % 100

//＜Time Addition＞
// Add Int time HHMM
fun Int.plusHHMM(time: Int): Int = (this.HHMMtoMM + time.HHMMtoMM).MMtoHHMM
// Add Int time HHMMSS
//fun Int.plusHHMMSS(time: Int): Int = (this.HHMMSStoSS + time.HHMMSStoSS).SStoHHMMSS
// Add Int time MMSS
//fun Int.plusMMSS(time: Int): Int = (this.MMSStoSS + time.MMSStoSS).SStoMMSS

//＜Time Subtraction＞
// Subtract Int time HHMM
fun Int.minusHHMM(time: Int): Int = if (this.HHMMtoMM < time.HHMMtoMM) {
    ((this + 2400).HHMMtoMM - time.HHMMtoMM).MMtoHHMM
} else {
    (this.HHMMtoMM - time.HHMMtoMM).MMtoHHMM
}
// Subtract Int time HHMMSS
fun Int.minusHHMMSS(time: Int): Int = if (this.HHMMSStoSS < time.HHMMSStoSS) {
    ((this + 240000).HHMMSStoSS - time.HHMMtoMM).SStoHHMMSS
} else {
    (this.HHMMSStoSS - time.HHMMSStoSS).SStoHHMMSS
}
// Subtract Int time HHMM
//fun Int.minusMMSS(time: Int): Int = (this.MMSStoSS - time.MMSStoSS).SStoMMSS

//＜Time Display＞
// Add zero for single digit
val Int.addZeroTime: String get() = if (this in 0..9) {"0$this"} else {"$this"}
// Get HH from Int time
val Int.HH: String get() = (this / 100 + (this % 100) / 60).addZeroTime
// Get mm from Int time
val Int.mm: String get() = (this % 100 % 60).addZeroTime
// Convert Int time HHMM to time string
val Int.stringTime: String get() = if (this < 2700) this.HH + ":" + this.mm else "--:--"