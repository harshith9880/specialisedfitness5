package com.example.specialisedfitness_1


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

class LowerBackPainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lower_back_pain)

        val plankWorkoutButton: Button = findViewById(R.id.buttonPlankWorkout)
        plankWorkoutButton.setOnClickListener {
            val intent = Intent(this, PlankWorkoutActivity::class.java)
            startActivity(intent)

            // Logic for showing the list of ailments
        }
        val birdDogWorkoutButton: Button = findViewById(R.id.buttonBirdDogWorkout)
        birdDogWorkoutButton.setOnClickListener {
            val intent = Intent(this, BirdDogActivity::class.java)
            startActivity(intent)
        }

        val pelvicTiltWorkoutButton: Button = findViewById(R.id.buttonPelvicTiltWorkout)
        pelvicTiltWorkoutButton.setOnClickListener {
            val intent = Intent(this, PelvicTiltActivity::class.java)
            startActivity(intent)
        }
    }

}