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
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket

class ChooseActivity : AppCompatActivity(){
    private lateinit var practice: Button
    private lateinit var chessType: RadioGroup
    private lateinit var online: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

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
     * @param type 联机还是练习模式还是其它
     */
    private fun jumpToOtherActivity(type: String){
        val id = chessType.checkedRadioButtonId
        //检查游戏类型radio有没有选
        if(id == -1){
            Toast.makeText(this@ChooseActivity, "必须选择一种棋", Toast.LENGTH_SHORT).show()
        }else {
            val tp = findViewById<RadioButton>(id).text.toString()
            (application as MyApplication).message.chessType = when(tp){
                "五子棋" -> 0
                "黑白棋" -> 1
                "围棋" -> 2
                else -> 0
            }

            val intent = Intent(this@ChooseActivity, MainActivity::class.java)

            (application as MyApplication).gameMod = type
            if(type == "online"){
                val mp = MyUtil.getIpAndPort(assets)
                val host = mp["ip"].toString()
                val port = mp["port"].toString().toInt()
                val thread = Thread{
                    try {
                        val socket = Socket(host, port)
                        (application as MyApplication).socket = socket

                        //向服务器发送首信息
                        val outputStream = ObjectOutputStream(socket.getOutputStream())
                        val seria = (application as MyApplication).message
                        outputStream.writeObject(seria)

                        //获取猜先
                        val b = socket.getInputStream().read()
                        runOnUiThread {
                            if(b==245){
                                Toast.makeText(this@ChooseActivity, "匹配失败", Toast.LENGTH_SHORT).show()
                            }else{
                                (application as MyApplication).flag = b.toString()
                                startActivity(intent)
                            }
                        }

                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
                thread.start()
                Toast.makeText(this, "正在等待其他人加入，请勿进行其他操作", Toast.LENGTH_SHORT).show()
            }else{
                (application as MyApplication).flag = ""
                startActivity(intent)
            }
        }
    }
}

