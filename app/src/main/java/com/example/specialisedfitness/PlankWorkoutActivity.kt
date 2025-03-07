package com.example.specialisedfitness_1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class PlankWorkoutActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null
    private var isPlankValid = false
    private var timerStarted = false
    private var count = 0
    private var elapsedTime = 0

    private lateinit var statusTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var counterTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plank_workout)

        statusTextView = findViewById(R.id.statusTextView)
        timerTextView = findViewById(R.id.timerTextView)
        counterTextView = findViewById(R.id.counterTextView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (gyroscope == null || accelerometer == null) {
            Toast.makeText(this, "Sensors not available", Toast.LENGTH_SHORT).show()
            finish() // Exit activity if sensors are missing
        }
        mediaPlayer = MediaPlayer.create(this,R.raw.encouraging)
    }






    override fun onResume() {
        super.onResume()
        gyroscope?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            handleGyroscope(event)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            handleAccelerometer(event)
        }

        if (isPlankValid && !timerStarted) {
            startTimer()
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        // Ensure the phone is in a perpendicular position
        val zRotation = event.values[2] // Rotation around Z axis
        isPlankValid = zRotation > -0.1 && zRotation < 0.1
    }

    private fun handleAccelerometer(event: SensorEvent) {
        // Ensure minimal movement
        val movement = sqrt(
            (event.values[0] * event.values[0] +
                    event.values[1] * event.values[1] +
                    event.values[2] * event.values[2]).toDouble()
        )
        if (movement > 5) {
            resetTimer()
        }
        isPlankValid = event.values[2] < -5.0
    }

    private fun startTimer() {
        timerStarted = true
        elapsedTime = 0
        statusTextView.text = "Holding Plank..."
        updateTimer()
    }


    private fun updateTimer() {
        if (timerStarted) {
            handler.postDelayed({
                if (isPlankValid) {
                    elapsedTime += 1
                    timerTextView.text = "Timer: ${elapsedTime}s"
                }
                if (elapsedTime <= 30) {
                    updateTimer()
                } else
                {
                    timerStarted = false
                    handler.removeCallbacksAndMessages(null) // Stop further callbacks
                    incrementCounter()
                }
            }, 1000) // Delay for 1 second
        }
    }

    private fun incrementCounter() {
        count++
        counterTextView.text = "Planks Completed: $count"
        statusTextView.text = "Plank Complete!"
        mediaPlayer.start()
        resetTimer() // Reset the timer after reaching 30 seconds
    }

    private fun resetTimer() {
        timerStarted = false
        elapsedTime = 0
        timerTextView.text = "Timer: 0s"
        statusTextView.text = "Resetting Timer..."
        updateTimer()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
