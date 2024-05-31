package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.Random


/**
 * 用户在局域网下选择发起对局或者加入对局
 */
class LocalChooseActivity : AppCompatActivity() {
    private lateinit var launch: Button
    private lateinit var join: Button
    private lateinit var chessType: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_choose)

        launch = findViewById(R.id.launch)
        join = findViewById(R.id.join)
        chessType = findViewById(R.id.chessType)
        //发起对局
        launch.setOnClickListener {
            val id = chessType.checkedRadioButtonId
            //检查游戏类型radio有没有选
            if (id == -1) {
                Toast.makeText(this@LocalChooseActivity, "必须选择一种棋", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val tp = findViewById<RadioButton>(id).text.toString()
                (application as MyApplication).message.chessType = when (tp) {
                    "五子棋" -> 0
                    "黑白棋" -> 1
                    "围棋" -> 2
                    else -> 0
                }
            }
            udpServer()
        }
        join.setOnClickListener{
            udpClient()
        }
    }

    /**
     * 发起对局是作为udp服务端，获取到对方的ip后作为tcp的客户端
     */
    private fun udpServer(){
        Toast.makeText(this, "接收端启动", Toast.LENGTH_SHORT).show()
        Thread{
            //创建接收端对象，注册端口
            val d = DatagramSocket(9990)
            //创建数据包对象
            val buffer = ByteArray(1024 * 64)
            val dd = DatagramPacket(buffer, buffer.size)
            //等待接收数据
            d.receive(dd)
            val isa = dd.socketAddress as InetSocketAddress
            val ip = isa.address.toString().substring(1)
            d.close()

//            runOnUiThread{
//                //获取发送端的IP地址和端口
//                Toast.makeText(this@Choose2Activity, "ip"+ip+" 端口："+dd.port, Toast.LENGTH_SHORT).show()
//            }
            val socket = Socket(ip, 8880)
            (application as MyApplication).socket = socket
//            测试代码
//            (application as MySocket).getSocket().getOutputStream().write(23)

            //向对方发送棋类信息
            val type = (application as MyApplication).message.chessType
            socket.getOutputStream().write(type)

            //获取先后手信息
            val value = socket.getInputStream().read()

            //跳转到游戏
            remainingWork(value)
        }.start()
    }

    /**
     * 加入对局是作为udp客户端，但是作为tcp的服务端等待连接
     */
    private fun udpClient(){
        Toast.makeText(this, "发送端启动", Toast.LENGTH_SHORT).show()
        Thread{
            //创建一个发送端对象,,发送端自带默认端口号
            val d = DatagramSocket(6667) //参数可以指定发送端的端口
            //创建一个数据包对象封装数据
            val buffer = "zhi zhe bu ru ai he".toByteArray()
            //使用广播地址
            val dd = DatagramPacket(buffer, buffer.size, InetAddress.getByName("255.255.255.255"), 9990)
            //发送数据
            d.send(dd)
            d.close()

            //创建tcp server socket等待连接
            val serverSocket = ServerSocket(8880)
            val socket = serverSocket.accept()
            (application as MyApplication).socket = socket

            //获取棋类信息
            (application as MyApplication).message.chessType = socket.getInputStream().read()

            //决定猜先
            val value = (0..1).random()

            socket.getOutputStream().write(1-value)

            remainingWork(value)
        }.start()
    }

    private fun remainingWork(value: Int){
        (application as MyApplication).flag = value.toString()
        runOnUiThread{
            //跳转到游戏
            val intent = Intent(this@LocalChooseActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

}