package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.myapplication.Info.Message
import java.io.IOException
import java.io.Serializable
import java.net.Socket

class ChooseActivity : AppCompatActivity(){
    private lateinit var practice: Button
    private lateinit var chessType: RadioGroup
    private lateinit var online: Button
    private var seria: Serializable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        seria = intent.getSerializableExtra("info")

        online = findViewById(R.id.online)
        chessType = findViewById(R.id.chessType)
        practice = findViewById(R.id.practice)
        //练习模式
        practice.setOnClickListener {
            jumpToOtherActivity("practice")
        }
        //联机对战
        online.setOnClickListener{
            jumpToOtherActivity("online")
        }
    }

    /**
     * 跳转到其它
     */
    private fun jumpToOtherActivity(type: String){
        var id = chessType.checkedRadioButtonId
        if(id == -1){
            Toast.makeText(this@ChooseActivity, "必须选择一种棋", Toast.LENGTH_SHORT).show()
        }else {
            var intent = Intent(this@ChooseActivity, MainActivity::class.java)
            intent.putExtra("chessType", findViewById<RadioButton>(id).text.toString())
            intent.putExtra("gameMod", type)
//            Log.i("choose", seria.toString())
            intent.putExtra("info", seria)

            startActivity(intent)
        }
    }
}

