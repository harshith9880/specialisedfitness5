package com.example.specialisedfitness_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Make sure this matches your XML file name

        val loginButton: Button = findViewById(R.id.button)
        loginButton.setOnClickListener {
            // Start the next activity, e.g., HomeActivity or LoginActivity
            val intent = Intent(this, MainActivity::class.java) // Change to the correct activity
            startActivity(intent)
        }
    }
}
