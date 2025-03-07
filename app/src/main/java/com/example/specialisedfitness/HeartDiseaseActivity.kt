package com.example.specialisedfitness_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HeartDiseaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_disease)

        val stepTrackerButton: Button = findViewById(R.id.buttonStepTracker)
        stepTrackerButton.setOnClickListener {
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
        }
    }
}
