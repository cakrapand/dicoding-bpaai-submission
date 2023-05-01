package com.example.storyapp.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.storyapp.R
import com.example.storyapp.ui.auth.AuthActivity

class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val btnStart: Button = findViewById(R.id.btnStart)
        btnStart.setOnClickListener {
            startActivity(Intent(this@OnBoardingActivity, AuthActivity::class.java))
            finish()
        }
    }
}