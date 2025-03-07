package com.example.specialisedfitness_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ArthritisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arthritis)

        val warrior2Button: Button = findViewById(R.id.buttonWarrior2)
        warrior2Button.setOnClickListener {
            val intent = Intent(this, Warrior2Activity::class.java)
            startActivity(intent)
        }
    }
}
