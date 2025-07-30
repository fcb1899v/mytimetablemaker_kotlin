package com.example.mytimetablemaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivitySignupBinding

// Activity for user account registration
class SignUpActivity : AppCompatActivity() {

    // ViewBinding for accessing views
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize login manager and advertisement
        val myLogin = MyLogin(this)
        val admob = AdMob()

        // Get input fields
        val email = binding.fieldEmail.text
        val password = binding.fieldPassword.text
        val confirmPass = binding.fieldConfirmPassword.text
        
        // Text watcher for real-time validation
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update button appearance based on validation
                binding.signUpRegisterButton.setBackgroundResource(
                    if (myLogin.isValidate(email.toString(), password.toString(), confirmPass.toString(), binding.signUpCheckBox.isChecked))
                        R.drawable.register_button else R.drawable.not_button
                )
            }
        }

        // Add text watchers to input fields
        binding.fieldEmail.addTextChangedListener(textWatcher)
        binding.fieldPassword.addTextChangedListener(textWatcher)
        binding.fieldConfirmPassword.addTextChangedListener(textWatcher)

        // Account registration button click handler
        binding.signUpRegisterButton.setOnClickListener {
            myLogin.signup(
                email.toString(),
                password.toString(),
                confirmPass.toString(),
                binding.signUpCheckBox.isChecked,
                binding.progressBar
            )
        }

        // Terms of service and privacy policy link
        binding.signUpPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(R.string.privacyPolicyUrl.strings)
            })
        }

        // Checkbox for terms agreement - controls registration button state
        binding.signUpCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val isValidate = myLogin.isValidate(email.toString(), password.toString(), confirmPass.toString(), isChecked)
            binding.signUpRegisterButton.setBackgroundResource(
                if (isValidate) R.drawable.register_button else R.drawable.not_button
            )
        }

        // Initialize AdMob advertisement
        admob.setAdMob(binding.signUpAdmobView, this)
    }

    // Handle back button press for navigation
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}

