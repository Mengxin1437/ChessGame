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
import com.example.myapplication.logic.FiveInLine
import com.example.myapplication.logic.Go
import com.example.myapplication.logic.Reversi
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket

class ChooseActivity : AppCompatActivity(){
    private lateinit var practice: Button
    private lateinit var chessType: RadioGroup
    private lateinit var online: Button
    private var seria: Serializable? = null //上一个activity传递过来的对象

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
     * @param type 联机还是练习模式还是其它
     */
    private fun jumpToOtherActivity(type: String){
        var id = chessType.checkedRadioButtonId
        if(id == -1){
            Toast.makeText(this@ChooseActivity, "必须选择一种棋", Toast.LENGTH_SHORT).show()
        }else {
            var intent = Intent(this@ChooseActivity, MainActivity::class.java)
            val tp = findViewById<RadioButton>(id).text.toString()
            //棋的类型
            intent.putExtra("chessType", tp)
            intent.putExtra("gameMod", type)
            intent.putExtra("info", seria)
            if(type == "online"){
                val mp = MyUtil.getIpAndPort(assets)
                val host = mp["ip"].toString()
                val port = mp["port"].toString().toInt()
                val thread = Thread{
                    try {
                        var socket = Socket(host, port)
                        (application as MySocket).setSocket(socket)

                        //向服务器发送首信息
                        val outputStream = ObjectOutputStream(socket.getOutputStream())
                        (seria as Message).chessType = when(tp){
                            "五子棋" -> 0
                            "黑白棋" -> 1
                            "围棋" -> 2
                            else -> 0
                        }
                        outputStream.writeObject(seria)

                        //获取猜先
                        val b = socket!!.getInputStream().read()
                        runOnUiThread {
                            if(b==245){
                                Toast.makeText(this@ChooseActivity, "匹配失败", Toast.LENGTH_SHORT).show()
                            }else{
                                intent.putExtra("flag", b.toString())
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
                intent.putExtra("flag", "")
                startActivity(intent)
            }
        }
    }
}

