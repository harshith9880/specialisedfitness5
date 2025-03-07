package com.example.specialisedfitness_1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlin.math.abs
class PelvicTiltActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var tracker = 0
    private var overallCount = 0
    private var isTilted = false
    private var timerValue = 0L // Timer value in milliseconds
    private var userTimeThreshold = 5000L // Default user-defined time threshold (in ms)
    private var userCountThreshold = 10 // Default user-defined count threshold
    private var isTracking = false // To ensure tracking happens only once per second

    private lateinit var statusTextView: TextView
    private lateinit var trackerTextView: TextView
    private lateinit var overallCountTextView: TextView
    private lateinit var timeThresholdInput: EditText
    private lateinit var countThresholdInput: EditText
    private lateinit var setThresholdsButton: Button
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())

    private val incrementTask = object : Runnable {
        override fun run() {
            if (isTilted) {
                tracker++
                updateUI()

                if (tracker >= userCountThreshold) {
                    overallCount++
                    mediaPlayer.start() // Play sound when the userCountThreshold is reached
                    tracker = 0 // Reset tracker after reaching count threshold
                }

                handler.postDelayed(this, 1000) // Re-run the task after 1 second
            } else {
                stopTracking() // Stop tracking if the user is not in the correct position
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelvic_tilt)

        // Initialize UI components
        statusTextView = findViewById(R.id.statusTextView)
        trackerTextView = findViewById(R.id.trackerTextView)
        overallCountTextView = findViewById(R.id.overallCountTextView)
        timeThresholdInput = findViewById(R.id.timeThresholdInput)
        countThresholdInput = findViewById(R.id.countThresholdInput)
        setThresholdsButton = findViewById(R.id.setThresholdsButton)

        // Initialize sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Initialize media player
        mediaPlayer = MediaPlayer.create(this, R.raw.encouraging)

        // Set button click listener to apply user input
        setThresholdsButton.setOnClickListener {
            val timeInput = timeThresholdInput.text.toString()
            val countInput = countThresholdInput.text.toString()

            if (timeInput.isNotEmpty() && countInput.isNotEmpty()) {
                userTimeThreshold = timeInput.toLong() * 1000 // Convert seconds to milliseconds
                userCountThreshold = countInput.toInt()

                Toast.makeText(this, "Thresholds set: Time = ${userTimeThreshold / 1000}s, Count = $userCountThreshold", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter valid thresholds", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        stopTracking() // Stop tracking on pause
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val zAxis = event.values[2]

            if (abs(zAxis) >= 9.8) { // Detect pelvic tilt position
                if (!isTilted) {
                    isTilted = true
                    statusTextView.text = "Hold the position!"
                    startTracking()
                }
            } else {
                if (isTilted) {
                    isTilted = false
                    statusTextView.text = "Adjust your position"
                    stopTracking()
                }
            }
        }
    }

    private fun startTracking() {
        if (!isTracking) {
            isTracking = true
            handler.postDelayed(incrementTask, 1000) // Start incrementing after 1 second
        }
    }

    private fun stopTracking() {
        isTracking = false
        handler.removeCallbacks(incrementTask) // Stop incrementing when not tilted
    }

    private fun updateUI() {
        trackerTextView.text = "Tracker: $tracker"
        overallCountTextView.text = "Overall Count: $overallCount"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
