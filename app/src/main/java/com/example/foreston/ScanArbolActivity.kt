package com.example.foreston

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foreston.databinding.ActivityHomeBinding
import com.example.foreston.databinding.ActivityScanArbolBinding

class ScanArbolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanArbolBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanArbolBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}