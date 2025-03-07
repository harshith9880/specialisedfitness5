package com.example.specialisedfitness_1


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.specialisedfitness_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)

        // Setup button listeners for each ailment
        /*binding.buttonLowerBackPain.setOnClickListener {
            navigateToAilment("Lower Back Pain")
        }*/
        val buttonAilments: Button = findViewById(R.id.buttonAilments)

        buttonAilments.setOnClickListener {
            // Launch the Ailments list activity
            val intent = Intent(this, AilmentsActivity::class.java)
            startActivity(intent)
        }


        // Add more listeners for other ailment buttons
    }

    private fun navigateToAilment(ailment: String) {
        val intent = Intent(this, AilmentActivity::class.java)
        intent.putExtra("ailment", ailment)
        startActivity(intent)
    }
}
