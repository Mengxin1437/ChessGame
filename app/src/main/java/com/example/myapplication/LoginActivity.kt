package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.Info.Message

class LoginActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.btnLogin)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login.setOnClickListener{
            var intent = Intent(this@LoginActivity, ChooseActivity::class.java)
            intent.putExtra("info", Message(username.text.toString(), password.text.toString()))
            startActivity(intent)
        }
    }
}