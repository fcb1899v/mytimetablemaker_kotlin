package com.example.mytimetablemaker

//＜時刻の変換＞
//Int型時刻HHMMをMMに変換
val Int.HHMMtoMM: Int get() = this / 100 * 60 + this % 100
//Int型時刻MMをHHMMに変換
val Int.MMtoHHMM: Int get() = this / 60 * 100 + this % 60
//Int型時刻MMSSをSSに変換
//val Int.MMSStoSS: Int get() = this / 100 * 60 + this % 100
//Int型時刻SSをMMSSに変換
//val Int.SStoMMSS: Int get() = this / 60 * 100 + this % 60
//Int型時刻HHMMSSをSSに変換
val Int.HHMMSStoSS: Int get() = this / 10000 * 3600 + (this % 10000) / 100 * 60 + this % 100
//Int型時刻SSをHHMMSSに変換
val Int.SStoHHMMSS:Int get() = this / 3600 * 10000 + (this % 3600) / 60 * 100 + this % 60
//Int型時刻HHMMSSをMMSSに変換
val Int.HHMMSStoMMSS: Int get() = (this / 10000 * 60 + (this % 10000) / 100) * 100 + this % 100
//Int型時刻MMSSをHHMMSSに変換
//val Int.MMSStoHHMMSS: Int get() = (this / 100 / 60) * 10000 + (this / 100 % 60) * 100 + this % 100

//＜時刻の足し算＞
//Int型時刻HHMMの足し算
fun Int.plusHHMM(time: Int): Int = (this.HHMMtoMM + time.HHMMtoMM).MMtoHHMM
//Int型時刻HHMMSSの足し算
//fun Int.plusHHMMSS(time: Int): Int = (this.HHMMSStoSS + time.HHMMSStoSS).SStoHHMMSS
//Int型時刻MMSSの足し算
//fun Int.plusMMSS(time: Int): Int = (this.MMSStoSS + time.MMSStoSS).SStoMMSS

//＜時刻の引き算＞
//Int型時刻HHMMの引き算
fun Int.minusHHMM(time: Int): Int = if (this.HHMMtoMM < time.HHMMtoMM) {
    ((this + 2400).HHMMtoMM - time.HHMMtoMM).MMtoHHMM
} else {
    (this.HHMMtoMM - time.HHMMtoMM).MMtoHHMM
}
//Int型時刻HHMMSSの引き算
fun Int.minusHHMMSS(time: Int): Int = if (this.HHMMSStoSS < time.HHMMSStoSS) {
    ((this + 240000).HHMMSStoSS - time.HHMMtoMM).SStoHHMMSS
} else {
    (this.HHMMSStoSS - time.HHMMSStoSS).SStoHHMMSS
}
//Int型時刻HHMMの引き算
//fun Int.minusMMSS(time: Int): Int = (this.MMSStoSS - time.MMSStoSS).SStoMMSS

//＜時刻の表示＞
//1桁のときに0を追加
val Int.addZeroTime: String get() = if (this in 0..9) {"0$this"} else {"$this"}
//Int型の時刻からHHを取得
val Int.HH: String get() = (this / 100 + (this % 100) / 60).addZeroTime
//Int型の時刻からmmを取得
val Int.mm: String get() = (this % 100 % 60).addZeroTime
//Int型時刻HHMMから時刻に変換
val Int.stringTime: String get() = if (this < 2700) this.HH + ":" + this.mm else "--:--"