package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import com.example.myapplication.logic.Chess
import com.example.myapplication.logic.FiveInLine
import com.example.myapplication.logic.Go
import com.example.myapplication.logic.Reversi
import com.example.myapplication.view.MyView

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var gameBoard: MyView
    private lateinit var left: Button
    private lateinit var right: Button
    private lateinit var up: Button
    private lateinit var down: Button
    private lateinit var confirm: Button
    private lateinit var cancel: Button
    private lateinit var chess: Chess
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var type = intent.getStringExtra("chessType")

        gameBoard = findViewById(R.id.gameBoard)
        chess = when(type){
            "五子棋" -> FiveInLine()
            "黑白棋" -> Reversi()
            "围棋" -> Go()
            else -> FiveInLine()
        }
        chess.init(19, 19)
        gameBoard.setChess(chess)

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

    override fun onClick(p0: View?) {
        Log.i("click", "click")
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