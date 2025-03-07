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
import kotlin.math.abs

class Warrior2Activity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null
    private var isWarriorPoseValid = false
    private var tracker = 0
    private var overallCount = 0
    private var isGyroPoseValid = false
    private var isWarriorPoseValid2 = false
    private var checker = false

    private lateinit var statusTextView: TextView
    private lateinit var trackerTextView: TextView
    private lateinit var overallCountTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer
    private var isTracking = false
    private var qualityScore = 0 // To keep track of the quality score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warrior2_pose)

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

        if (isGyroPoseValid && (isWarriorPoseValid || isWarriorPoseValid2)) {
            checkForCompletion()
            if (isTracking) {
                calculateQualityScore(event.values) // Calculate quality score if tracking
            }
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        val yRotation = event.values[1] // Rotation around Y axis (upper body alignment)
        val zRotation = event.values[2] // Rotation around Z axis (leg alignment)
        isGyroPoseValid =
            abs(yRotation) < 0.3 && abs(zRotation) < 0.3 // Thresholds for proper alignment
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val zMovement = abs(event.values[2])
        isWarriorPoseValid = (zMovement in 7.0..9.0 || zMovement in -9.0..-7.0)
        isWarriorPoseValid2 = (zMovement in 4.0..7.0 || zMovement in -7.0..-4.0)
    }

    private fun calculateQualityScore(sensorValues: FloatArray) {
        val zMovement = abs(sensorValues[2])
        val gyroscopeY = abs(sensorValues[1])
        val gyroscopeZ = abs(sensorValues[2])

        // Define valid ranges for the Warrior II pose
        val zValidRange = 7.0..9.0
        val gyroYValidRange = 0.0..0.3
        val gyroZValidRange = 0.0..0.3

        // Calculate how much closer the current values are to the center of the valid ranges
        val zScore = when {
            zMovement in zValidRange -> 1.0
            zMovement < zValidRange.start -> (zValidRange.start - zMovement) / (zValidRange.endInclusive - zValidRange.start)
            zMovement > zValidRange.endInclusive -> (zMovement - zValidRange.endInclusive) / (zValidRange.endInclusive - zValidRange.start)
            else -> 0.0
        }

        val gyroYScore = when {
            gyroscopeY in gyroYValidRange -> 1.0
            gyroscopeY < gyroYValidRange.start -> (gyroYValidRange.start - gyroscopeY) / (gyroYValidRange.endInclusive - gyroYValidRange.start)
            gyroscopeY > gyroYValidRange.endInclusive -> (gyroscopeY - gyroYValidRange.endInclusive) / (gyroYValidRange.endInclusive - gyroYValidRange.start)
            else -> 0.0
        }

        val gyroZScore = when {
            gyroscopeZ in gyroZValidRange -> 1.0
            gyroscopeZ < gyroZValidRange.start -> (gyroZValidRange.start - gyroscopeZ) / (gyroZValidRange.endInclusive - gyroZValidRange.start)
            gyroscopeZ > gyroZValidRange.endInclusive -> (gyroscopeZ - gyroZValidRange.endInclusive) / (gyroZValidRange.endInclusive - gyroZValidRange.start)
            else -> 0.0
        }

        // Calculate the overall quality score as an average
        qualityScore = ((zScore + gyroYScore + gyroZScore) / 3 * 100).toInt() // Scale to percentage

        // Update UI with the quality score
        statusTextView.text = "Quality Score: $qualityScore"
    }

    private fun checkForCompletion() {
        startIncrementingTracker()
    }

    private fun startIncrementingTracker() {
        if (!isTracking) {
            isTracking = true
            handler.postDelayed(incrementTask, 1000) // Start with a 1 second delay
        }
    }

    private val incrementTask = object : Runnable {
        override fun run() {
            if (checker && isWarriorPoseValid && isGyroPoseValid || !checker && isWarriorPoseValid2 && isGyroPoseValid) {
                tracker++
                trackerTextView.text = "Tracker: $tracker"

                if (tracker >= 10) {
                    incrementOverallCount()
                    resetTracker()
                } else {
                    handler.postDelayed(this, 1000) // Post the next increment in 1 second
                }
            } else {
                stopIncrementingTracker() // Stop if pose is invalid
            }
        }
    }

    private fun stopIncrementingTracker() {
        isTracking = false
        handler.removeCallbacks(incrementTask) // Stop the task
    }

    private fun incrementOverallCount() {
        overallCount++
        overallCountTextView.text = "Overall Count: $overallCount"
        mediaPlayer.start() // Play sound when overall count reaches 10
        Toast.makeText(this, "Great Job! Completed 10 Warrior II poses.", Toast.LENGTH_SHORT).show()
    }

    private fun resetTracker() {
        tracker = 0
        trackerTextView.text = "Tracker: 0"
        checker = !checker
        stopIncrementingTracker() // Stop the tracker when reset
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
