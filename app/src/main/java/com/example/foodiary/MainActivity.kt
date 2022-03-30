package com.example.foodiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moveHome = findViewById<ViewGroup>(R.id.mainView)
        moveHome.setOnClickListener {
            val itt = Intent(this, HomeActivity::class.java)
            startActivity(itt)
        }
    }
}