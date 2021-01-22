package com.sakurax.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sakurax.rocker.Rocker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rocker: Rocker = findViewById(R.id.rocker)
        rocker.setRockerListener { x, y ->
            Log.d("MainActivity","x轴${x},y轴${y}")
        }
    }
}