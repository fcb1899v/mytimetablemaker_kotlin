package com.example.mytimetablemaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    //ViewBinding
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        val myLogin = MyLogin(requireContext())
        val myFirestore = MyFirestore(requireContext())
        val myPreference = MyPreference(requireContext())
        val mySettings = MySettings(requireContext())
        val isLogin = myPreference.prefGetBoolean(loginKey, false)

        //スイッチの変更による表示の変更
        val switchView: Array<Switch> = arrayOf(binding.back2switch, binding.go2switch)

        for (i in 0..1) {
            switchView[i].apply {
                isChecked = myPreference.getRoute2Switch(goOrBack2Array[i])
                setOnCheckedChangeListener { _, isChecked: Boolean ->
                    myPreference.prefSaveBoolean(goOrBack2Array[i].route2Key, isChecked)
                    if (i == 0) binding.back2ChangeLineLayout.isVisible = isChecked
                    if (i == 0) binding.back2SettingLayout.isVisible = isChecked
                    if (i == 1) binding.go2ChangeLineLayout.isVisible = isChecked
                    if (i == 1) binding.go2SettingLayout.isVisible = isChecked
                }
            }
        }

        val buttonView: Map<String, Array<TextView>> = mapOf(
            "changeLine" to arrayOf(
                binding.back1ChangeLineButton, binding.go1ChangeLineButton,
                binding.back2ChangeLineButton, binding.go2ChangeLineButton
            ),
            "settings" to arrayOf(
                binding.back1SettingButton, binding.go1SettingButton,
                binding.back2SettingButton, binding.go2SettingButton,
            ),
            "firestore" to arrayOf(
                binding.saveServerButton, binding.getServerDataButton,
                binding.signOutButton, binding.deleteAccountButton
            )
        )

        val textView: Map<String, Array<TextView>> = mapOf(
            "changeLine" to arrayOf(
                binding.back1ChangeLineText, binding.go1ChangeLineText,
                binding.back2ChangeLineText, binding.go2ChangeLineText
            ),
        )

        for (i in 0..3) {

            //Change Line
            textView["changeLine"]!![i].text = myPreference.changeLineString(goOrBackArray[i])
            buttonView["changeLine"]!![i].setOnClickListener {
                mySettings.setChangeLineDialog(goOrBackArray[i], textView["changeLine"]!![i])
                textView["changeLine"]!![i].text = myPreference.changeLineString(goOrBackArray[i])
            }

            //Various Settings
            buttonView["settings"]!![i].setOnClickListener {
                val bundle = Bundle()
                bundle.putString("BUNDLE_KEY_GOORBACK", goOrBackArray[i])
                val fragment = SettingsVariousFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings, fragment)
                    .commitAllowingStateLoss()
                (activity as AppCompatActivity).supportActionBar?.apply {
                    title = R.string.variousSettings.strings
                }
            }

            //Account
            buttonView["firestore"]!![i].text = accountTextArray(isLogin)[i]
            buttonView["firestore"]!![i].setOnClickListener {
                if (isLogin) {
                    when (i) {
                        0 -> myFirestore.getFirestore(binding.progressBar)
                        1 -> myFirestore.setFirestore(binding.progressBar)
                        2 -> myLogin.logout(binding.progressBar)
                        3 -> myLogin.deleteAccount(binding.progressBar)
                    }
                } else {
                    startActivity(Intent(context, LoginActivity::class.java))
                }
            }
        }
        binding.signOutLayOut.isVisible = isLogin
        binding.deleteAccountLayOut.isVisible = isLogin

        //バージョンの表示
        binding.versionNumber.text = BuildConfig.VERSION_NAME

        //利用規約・プライバシーポリシー
        binding.privacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(R.string.privacyPolicyUrl.strings)
            })
        }

        return binding.root
    }
}
