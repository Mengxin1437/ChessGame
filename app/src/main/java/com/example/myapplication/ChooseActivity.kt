package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class ChooseActivity : AppCompatActivity(){
    private lateinit var practice: Button
    private lateinit var chessType: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        practice = findViewById(R.id.practice)
        practice.setOnClickListener {
            var id = chessType.checkedRadioButtonId
            if(id == -1){
                Toast.makeText(this@ChooseActivity, "必须选择一种棋", Toast.LENGTH_SHORT).show()
            }else {
                var intent = Intent(this@ChooseActivity, MainActivity::class.java)
                intent.putExtra("chessType", findViewById<RadioButton>(id).text.toString())
                startActivity(intent)
            }
        }
        chessType = findViewById(R.id.chessType)


//        chessType.setOnCheckedChangeListener{
//            p0: RadioGroup?, p1: Int->
//        }
    }
}

