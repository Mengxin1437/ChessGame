package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
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
    private lateinit var chess: Chess
    private lateinit var gameMod: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mod = intent.getStringExtra("gameMod")

        var flag = intent.getStringExtra("flag")

        var socket: Socket? = null
        if(mod.equals("online")){
            socket = (application as MySocket).getSocket()
        }


        var type = intent.getStringExtra("chessType")
        gameMod = intent.getStringExtra("gameMod").toString()

        gameBoard = findViewById(R.id.gameBoard)
        if(mod.equals("online")){
            gameBoard.socket = socket;
            if(flag.equals("0")) {
                Toast.makeText(this, "你执白后行", Toast.LENGTH_SHORT).show()
                gameBoard.flag = false
            }else{
                Toast.makeText(this, "你执黑先行", Toast.LENGTH_SHORT).show()
                gameBoard.flag = true
            }
        }
        chess = when(type){
            "五子棋" -> FiveInLine()
            "黑白棋" -> Reversi()
            "围棋" -> Go()
            else -> FiveInLine()
        }
        chess.init(19, 19)
        gameBoard.setChess(chess)

        //后台监听网络数据包
        if(mod.equals("online")){
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
        val mySocket = (application as MySocket)
        mySocket.getSocket().close()
        mySocket.setSocket(null)
        super.onDestroy()
    }

    override fun onClick(p0: View?) {
        Log.i("click", "click")
        //不是自己方的回合，按钮一律不能点
        if (chess.turn != gameBoard.flag) return
        when(p0){
            confirm->{
                Log.i("click", "confirm")
                gameBoard.dropDown() //调用落子方法
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