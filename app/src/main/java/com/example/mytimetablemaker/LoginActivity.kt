package com.example.mytimetablemaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityLoginBinding

// Activity for user login functionality
class LoginActivity : AppCompatActivity() {

    // ViewBinding for accessing views
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize login manager and advertisement
        val myLogin = MyLogin(this)
        val admob = AdMob()

        // Get email and password input fields
        val email = binding.fieldEmail.text
        val password = binding.fieldPassword.text
        
        // Text watcher for real-time validation
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update button appearance based on validation
                binding.emailSignInButton.setBackgroundResource(
                    if (myLogin.isValidate(email.toString(), password.toString(), password.toString(), true))
                        R.drawable.register_button else R.drawable.not_button
                )
            }
        }

        // Add text watchers to input fields
        binding.fieldEmail.addTextChangedListener(textWatcher)
        binding.fieldPassword.addTextChangedListener(textWatcher)

        // Sign in button click handler
        binding.emailSignInButton.setOnClickListener {
            myLogin.login(email.toString(), password.toString(), binding.progressBar)
        }

        // Account registration button click handler
        binding.emailSignUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Password reset button click handler
        binding.resetPasswordButton.setOnClickListener {
            myLogin.passwordReset(binding.progressBar)
        }

        // Initialize AdMob advertisement
        admob.setAdMob(binding.signInAdmobView, this)
    }

    // Handle back button press in action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    // Handle back button press for navigation
    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }
}

