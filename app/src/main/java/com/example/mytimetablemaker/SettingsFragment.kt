package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    //クラスの呼び出し
    private val emailAuth = EmailAuth(Application.context)
    private val firebaseFirestore = FirebaseFirestore(Application.context)

    //ViewBinding
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        val myPreference = MyPreference(requireContext())
        val goOrBackArray: Array<String> = arrayOf("back1", "go1", "back2", "go2")
        val loggedIn = myPreference.prefGetBoolean("loggedin", false)

        //スイッチの変更による表示の変更
        val switchView: Array<Switch> = arrayOf(
            binding.back2switch, binding.go2switch
        )

        for (i in 0..1) {
            switchView[i].apply {
                isChecked = (if (i == 0) "back2" else "go2").getRoute2Switch
                setOnCheckedChangeListener { _, isChecked: Boolean ->
                    myPreference.prefSaveBoolean("${(if (i == 0) "back2" else "go2")}switch", isChecked)
                    (if (i == 0) binding.back2ChangeLineLayout else binding.go2ChangeLineLayout).isVisible = isChecked
                    (if (i == 0) binding.back2SettingLayout else binding.go2SettingLayout).isVisible = isChecked
                }
            }
        }

        val textView: Map<String, Array<TextView>> = mapOf(
            //乗換回数の設定
            "changeLineButton" to arrayOf(
                binding.back1ChangeLineButton, binding.go1ChangeLineButton,
                binding.back2ChangeLineButton, binding.go2ChangeLineButton
            ),
            "changeLineText" to arrayOf(
                binding.back1ChangeLineText, binding.go1ChangeLineText,
                binding.back2ChangeLineText, binding.go2ChangeLineText
            ),
            //詳細設定
            "settingButton" to arrayOf(
                binding.back1SettingButton, binding.go1SettingButton,
                binding.back2SettingButton, binding.go2SettingButton,
            ),
            //Account
            "firestore" to arrayOf(
                binding.saveServerButton, binding.getServerDataButton,
                binding.signOutButton, binding.deleteAccountButton
            ),
        )

        for (i in 0..3) {
            //乗換回数の表示
            textView["changeLineText"]!![i].text = goOrBackArray[i].changeLineString
            ////乗換回数の変更
            textView["changeLineButton"]!![i].setOnClickListener {
                Settings(requireContext(), goOrBackArray[i]).setChangeLineDialog(textView["ChangeLineTx"]!![i])
                textView["changeLineText"]!![i].text = goOrBackArray[i].changeLineString
            }
            ////乗換回数の変更
            textView["settingButton"]!![i].setOnClickListener {
                val title: String = R.string.variousSettings.strings
                val bundle = Bundle()
                bundle.putString("BUNDLE_KEY_GOORBACK", goOrBackArray[i])
                val fragment = SettingsVariousFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                        .replace(R.id.settings, fragment)
                        .commitAllowingStateLoss()
                (activity as AppCompatActivity).supportActionBar?.title = title
            }
            textView["firestore"]!![i].setOnClickListener {
                //ログアウトおよびアカウント削除したらログイン画面に移る
                if (!loggedIn) {
                    startActivity(emailAuth.intentToEmailAuthActivity())
                } else {
                    when (i) {
                        0 -> firebaseFirestore.saveAlertFirestore(requireContext())
                        1 -> firebaseFirestore.getAlertFirestore(requireContext())
                        2 -> logoutAccount(requireContext())
                        3 -> deleteAccount(requireContext())
                    }
                }
            }
        }

        //バージョンの表示
        binding.versionNumber.text = BuildConfig.VERSION_NAME

        //利用規約・プライバシーポリシー
        binding.privacyPolicy.setOnClickListener {
            startActivity(emailAuth.intentPrivacyPolicy())
        }

        return binding.root
    }

    //ログアウト
    private fun logoutAccount(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.logout.strings)
            setMessage(R.string.logout_account.strings)
            setNegativeButton(R.string.ok) { _, _ ->
                emailAuth.afterLogoutAccount()
                startActivity(emailAuth.intentToEmailAuthActivity())
            }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }

    //アカウント削除
    private fun deleteAccount(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.delete_your_account.strings)
            setMessage(R.string.delete_account.strings)
            setNegativeButton(R.string.ok) { _, _ ->
                Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emailAuth.afterDeleteAccount()
                        startActivity(emailAuth.intentToEmailAuthActivity())
                    } else {
                        Log.d("DeleteAccount", R.string.delete_account_error.strings)
                        emailAuth.makeAuthAlert(
                            context,
                            R.string.delete_account_error.strings,
                            R.string.delete_account_unsuccessfully.strings
                        )
                    }
                }
            }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }
}
