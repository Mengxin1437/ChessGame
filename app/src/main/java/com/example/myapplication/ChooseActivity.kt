package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ChooseActivity : AppCompatActivity() {
    private lateinit var practice: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        practice = findViewById(R.id.practice)
        practice.setOnClickListener{
            var intent = Intent(this@ChooseActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}