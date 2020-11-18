package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    //クラスの呼び出し
    private val setting = Setting()

    //バージョン
    //private val versionCodes: Int = BuildConfig.VERSION_CODE
    private val versionName: String = BuildConfig.VERSION_NAME

    @SuppressLint("UseSwitchCompatOrMaterialCode", "UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //goorbackの設定
        val goorbackarray: Array<String> = arrayOf("back1", "go1", "back2", "go2")

        //乗換回数設定のボタンとしてのTextView
        var changelinebutton: Array<TextView?> = arrayOf()
        changelinebutton += view.findViewById<TextView?>(R.id.back1changelinebutton)
        changelinebutton += view.findViewById<TextView?>(R.id.go1changelinebutton)
        changelinebutton += view.findViewById<TextView?>(R.id.back2changelinebutton)
        changelinebutton += view.findViewById<TextView?>(R.id.go2changelinebutton)

        //設定した乗換回数を表示するTextView
        var changelinetext: Array<TextView?> = arrayOf()
        changelinetext += view.findViewById<TextView?>(R.id.back1changelinetext)
        changelinetext += view.findViewById<TextView?>(R.id.go1changelinetext)
        changelinetext += view.findViewById<TextView?>(R.id.back2changelinetext)
        changelinetext += view.findViewById<TextView?>(R.id.go2changelinetext)

        //乗換回数設定のLinearLayout
        val back2changelinelayout: LinearLayout? = view.findViewById(R.id.back2changelinelayout)
        val go2changelinelayout: LinearLayout? = view.findViewById(R.id.go2changelinelayout)

        //表示する路線名のTextViewの設定
        var varioussettingbutton: Array<TextView?> = arrayOf()
        varioussettingbutton += view.findViewById<TextView?>(R.id.back1settingbutton)
        varioussettingbutton += view.findViewById<TextView?>(R.id.go1settingbutton)
        varioussettingbutton += view.findViewById<TextView?>(R.id.back2settingbutton)
        varioussettingbutton += view.findViewById<TextView?>(R.id.go2settingbutton)

        //各種設定のLinearLayout
        val back2settinglayout: LinearLayout? = view.findViewById(R.id.back2settinglayout)
        val go2settinglayout: LinearLayout? = view.findViewById(R.id.go2settinglayout)

        //ルート2を表示・非表示するSwitchの設定
        val back2switch: Switch? = view.findViewById(R.id.back2switch)
        val go2switch: Switch? = view.findViewById(R.id.go2switch)

        //バージョンの表示
        val versiontext: TextView? = view.findViewById(R.id.versionnumber)
        versiontext!!.text = versionName

        //リンク
        val privacypolicy: TextView? = view.findViewById(R.id.privacypolicy)

        for (i in 0..3) {
            //乗換回数の表示
            changelinetext[i]!!.text = goorbackarray[i].changeLineString
            ////乗換回数の変更
            changelinebutton[i]!!.setOnClickListener {
                setting.setChangeLineDialog(changelinetext[i]!!, context!!, goorbackarray[i])
                changelinetext[i]!!.text = goorbackarray[i].changeLineString
            }
            ////乗換回数の変更
            varioussettingbutton[i]!!.setOnClickListener {
                val title: String = goorbackarray[i].variousSettingsTitle
                val bundle = Bundle()
                bundle.putString("BUNDLE_KEY_GOORBACK", goorbackarray[i])
                val fragment = VariousSettingsFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                        .replace(R.id.main_settings, fragment)
                        .commitAllowingStateLoss()
                (activity as AppCompatActivity).supportActionBar?.title = title
            }
        }

        //スイッチの表示・変更
        setting.display2Switch(back2switch!!, "back2switch", false, back2changelinelayout!!, back2settinglayout!!)
        setting.display2Switch(go2switch!!, "go2switch", false, go2changelinelayout!!, go2settinglayout!!)

        //利用規約・プライバシーポリシーに移動
        privacypolicy!!.setOnClickListener {
            val url = "https://landingpage-mytimetablemaker.web.app/privacypolicy.html"
            startActivity(Intent(Intent.ACTION_VIEW).apply{
                data = Uri.parse(url)
            })
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
