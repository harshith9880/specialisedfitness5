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

class BirdDogActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null
    private var isWorkoutValid = false
    private var rightLiftCompleted = false
    private var leftLiftCompleted = false
    private var tracker = 0
    private var overallCount = 0

    private lateinit var statusTextView: TextView
    private lateinit var trackerTextView: TextView
    private lateinit var overallCountTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brid_dog_workout)

        statusTextView = findViewById(R.id.statusTextView)
        trackerTextView = findViewById(R.id.trackerTextView)
        overallCountTextView = findViewById(R.id.overallCountTextView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (gyroscope == null || accelerometer == null) {
            Toast.makeText(this, "Sensors not available", Toast.LENGTH_SHORT).show()
            finish() // Exit activity if sensors are missing
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.encouraging)
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

        if (isWorkoutValid) {
            checkForLiftsCompletion()
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        // Ensure body is in Bird Dog workout position (adjust thresholds accordingly)
        val zRotation = event.values[2] // Rotation around Z axis
        isWorkoutValid = zRotation > -0.4 && zRotation < 0.4 // Adjust threshold for proper posture
    }

    private fun handleAccelerometer(event: SensorEvent) {
        // Detect lifting movements (right and left)
        val movement = sqrt(
            (event.values[0] * event.values[0] +
                    event.values[1] * event.values[1] +
                    event.values[2] * event.values[2]).toDouble()
        )

        if (movement > 3.0) {
            if (event.values[0] > 3.5) {
                rightLiftCompleted = true
            } else if (event.values[0] < -3.5) {
                leftLiftCompleted = true
            }
        }
    }

    private fun checkForLiftsCompletion() {
        if (rightLiftCompleted && leftLiftCompleted) {
            incrementTracker()
            resetLifts() // Reset lift flags after one complete right and left lift
        }
    }

    private fun incrementTracker() {
        tracker++
        trackerTextView.text = "Tracker: $tracker"

        if (tracker >= 10) {
            incrementOverallCount()
            resetTracker()
        }
    }

    private fun incrementOverallCount() {
        overallCount++
        overallCountTextView.text = "Overall Count: $overallCount"
        mediaPlayer.start() // Play sound when overall count reaches 10
        Toast.makeText(this, "Great Job! Completed 10 lifts.", Toast.LENGTH_SHORT).show()
    }

    private fun resetTracker() {
        tracker = 0
        trackerTextView.text = "Tracker: 0"
    }

    private fun resetLifts() {
        rightLiftCompleted = false
        leftLiftCompleted = false
        statusTextView.text = "Ready for next lift!"
    }

    private fun resetWorkout() {
        tracker = 0
        overallCount = 0
        trackerTextView.text = "Tracker: 0"
        overallCountTextView.text = "Overall Count: 0"
        statusTextView.text = "Workout Reset!"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
