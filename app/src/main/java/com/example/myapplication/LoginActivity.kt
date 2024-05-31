package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.Info.Message

class LoginActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var localNetwork: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.btnLogin)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        localNetwork = findViewById(R.id.localNetwork)
        login.setOnClickListener{
            val intent = Intent(this@LoginActivity, ChooseActivity::class.java)
            (application as MyApplication).message = Message(username.text.toString(), password.text.toString(), 0)
            startActivity(intent)
        }
        //局域网对战
        localNetwork.setOnClickListener{
            (application as MyApplication).gameMod = "localNetwork"
            val intent = Intent(this@LoginActivity, LocalChooseActivity::class.java)
            (application as MyApplication).message = Message("", "", 0)
            startActivity(intent)
        }
    }
}