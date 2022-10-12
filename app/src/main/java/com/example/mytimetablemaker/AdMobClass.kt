package com.example.mytimetablemaker

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

@Suppress("DEPRECATION")
class AdMobClass {

    fun setAdMob(adview: AdView, context: Context) {
        //Admob広告
        MobileAds.initialize(context)
        val adrequest: AdRequest = AdRequest.Builder().build()
        adview.loadAd(adrequest)

        adview.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("debug", "Code to be executed when an ad finishes loading.")
            }

            override fun onAdOpened() {
                Log.d("debug", "Code to be executed when an ad opens an overlay that covers the screen.")
            }

            override fun onAdClosed() {
                Log.d("debug", "Code to be executed when the user is about to return to the app after tapping on an ad.")
            }
        }
    }
}