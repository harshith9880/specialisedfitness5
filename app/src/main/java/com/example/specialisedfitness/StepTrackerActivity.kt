package com.example.specialisedfitness_1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StepTrackerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var stepCount = 0
    private var isWalking = false
    private var lastStepTime: Long = 0 // Store the last step time
    private var caloriesBurned = 0f
    private val userWeight = 70f // Default weight in kg

    private lateinit var stepCountTextView: TextView
    private lateinit var startTrackingButton: Button
    private lateinit var stopTrackingButton: Button
    private lateinit var calorieCountTextView: TextView


    private var accelerationThresholdZ = 1.5f  // Threshold for Z-axis (vertical) accelerometer changes
    private var gyroThreshold = 1.0f           // Threshold for gyroscope changes (walking-like motion)
    private val stepInterval = 500             // Minimum time interval between steps in milliseconds
    private val holdDelay = 100L               // Delay before confirming a step

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)

        stepCountTextView = findViewById(R.id.stepCountTextView)
        startTrackingButton = findViewById(R.id.startTrackingButton)
        stopTrackingButton = findViewById(R.id.stopTrackingButton)
        calorieCountTextView = findViewById(R.id.calorieCountTextView)

        // Initialize SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        startTrackingButton.setOnClickListener {
            startTracking()
        }

        stopTrackingButton.setOnClickListener {
            stopTracking()
        }
    }

    private fun startTracking() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopTracking() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometerData(it.values)
                Sensor.TYPE_GYROSCOPE -> handleGyroscopeData(it.values)
            }
        }
    }

    private fun handleAccelerometerData(values: FloatArray) {
        val currentTime = System.currentTimeMillis()

        // Focus on Z-axis (vertical movement), values[2] corresponds to Z-axis
        val accelerationZ = values[2]

        // Check if the Z-axis acceleration passes the threshold and enough time has passed since the last step
        if (accelerationZ > accelerationThresholdZ && isWalking && currentTime - lastStepTime > stepInterval) {
            // Wait for the hold delay to confirm the step
            handler.postDelayed({
                if (isWalking && currentTime - lastStepTime > stepInterval) {
                    stepCount++
                    lastStepTime = currentTime
                    updateStepCount()
                }
            }, holdDelay)
        }
    }

    private fun handleGyroscopeData(values: FloatArray) {
        // Use the gyroscope to confirm walking-like rotational motion
        val angularSpeed = Math.sqrt(
            (values[0] * values[0] + values[1] * values[1] + values[2] * values[2]).toDouble()
        ).toFloat()

        // Update walking status based on whether the gyroscope detects rotation typical of walking
        isWalking = angularSpeed > gyroThreshold
    }

    private fun updateStepCount() {
        stepCountTextView.text = "Steps: $stepCount"
        caloriesBurned = (stepCount * userWeight * 0.04f) / 1000f
        calorieCountTextView.text = "Calories: %.2f".format(caloriesBurned)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No implementation needed
    }
}
