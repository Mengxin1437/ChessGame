package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.logic.Chess
import com.example.myapplication.logic.FiveInLine
import com.example.myapplication.logic.Go
import com.example.myapplication.logic.Reversi
import com.example.myapplication.view.MyView
import java.io.IOException
import java.net.Socket


class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var gameBoard: MyView
    private lateinit var left: Button
    private lateinit var right: Button
    private lateinit var up: Button
    private lateinit var down: Button
    private lateinit var confirm: Button
    private lateinit var cancel: Button
    private lateinit var turnInfo: TextView
    private lateinit var chess: Chess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var socket: Socket? = null

        val mod = (application as MyApplication).gameMod

        //获取到socket对象
        if(mod.equals("online") || mod.equals("localNetwork")){
            socket = (application as MyApplication).socket
        }

        val type = (application as MyApplication).message.chessType

        gameBoard = findViewById(R.id.gameBoard)
        turnInfo = findViewById(R.id.turnInfo)
        if(mod.equals("online") || mod.equals("localNetwork")){
            gameBoard.socket = socket;
            val flag = (application as MyApplication).flag
            if(flag != null && flag.equals("0")) {
                Toast.makeText(this, "你执白后行", Toast.LENGTH_SHORT).show()
                turnInfo.text = "对手的回合"
                gameBoard.flag = false
            }else{
                Toast.makeText(this, "你执黑先行", Toast.LENGTH_SHORT).show()
                turnInfo.text = "你的回合"
                gameBoard.flag = true
            }
        }
        chess = when(type){
            0 -> FiveInLine()
            1 -> Reversi()
            2 -> Go()
            else -> FiveInLine()
        }
        chess.init(19, 19)
        gameBoard.setChess(chess)

        //后台监听网络数据包
        if(mod.equals("online") || mod.equals("localNetwork")){
            Thread{
                while (true){
                    val x = socket?.getInputStream()?.read()
                    if(x == 235){
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "游戏已经结束", Toast.LENGTH_SHORT).show()
                        }
                        Thread.sleep(1000)
                        this@MainActivity.finish()
                        break
                    }
                    val y = socket?.getInputStream()?.read()
                    if(x!=null && y!=null) {
                        runOnUiThread {
                            turnInfo.text = "你的回合"
                        }
                        gameBoard.moveDownAndJudgeWin(x, y)
                    }
                }
            }.start()
        }

        left = findViewById(R.id.btnLeft)
        right = findViewById(R.id.btnRight)
        up = findViewById(R.id.btnUp)
        down = findViewById(R.id.btnDown)
        confirm = findViewById(R.id.btnConfirm)
        cancel = findViewById(R.id.btnCancel)
        left.setOnClickListener(this)
        right.setOnClickListener(this)
        up.setOnClickListener(this)
        down.setOnClickListener(this)
        confirm.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onDestroy() {
        val mySocket = (application as MyApplication)
        mySocket.socket.close()
        mySocket.socket = null
        super.onDestroy()
    }

    override fun onClick(p0: View?) {
        Log.i("click", "click")
        //不是自己方的回合，按钮一律不能点
        if (chess.turn != gameBoard.flag) return
        when(p0){
            confirm->{
                Log.i("click", "confirm")
                val b = gameBoard.dropDown() //调用落子方法
                if(b) turnInfo.text = "对手的回合"
            }
            left->{
                if(gameBoard.confirmPoint.y > 0)
                    gameBoard.confirmPoint.y--
                gameBoard.invalidate()
            }
            right->{
                if(gameBoard.confirmPoint.y < chess.board[0].size-1)
                    gameBoard.confirmPoint.y++
                gameBoard.invalidate()
            }
            up->{
                if(gameBoard.confirmPoint.x > 0)
                    gameBoard.confirmPoint.x--
                gameBoard.invalidate()
            }
            down->{
                if(gameBoard.confirmPoint.x < chess.board.size-1)
                    gameBoard.confirmPoint.x++
                gameBoard.invalidate()
            }
            cancel->{
                chess.cancelOneStep()
                gameBoard.invalidate()
            }
        }

    }
}