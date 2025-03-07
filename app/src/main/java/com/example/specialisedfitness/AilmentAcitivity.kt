package com.example.specialisedfitness_1


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.specialisedfitness_1.databinding.ActivityAilmentBinding
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AilmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAilmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAilmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get ailment from the intent
        val ailment = intent.getStringExtra("ailment") ?: "Unknown Ailment"
        binding.description.text = "$ailment Description"
        binding.questions.text = "$ailment related questions"
        binding.workoutText.text = "Workouts for $ailment"

        // Start tracking button action
        binding.startTracking.setOnClickListener {
            startTrackingWorkouts()
        }
    }

    private fun startTrackingWorkouts() {
        // Code to start gyrometer and accelerometer tracking for workouts
        // This will require setting up sensor listeners
    }
}



class WorkoutTracking(private val context: Context) : SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Register listeners
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Handle gyroscope data
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Handle accelerometer data
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    fun stopTracking() {
        // Unregister listeners when done
        sensorManager.unregisterListener(this)
    }
}
