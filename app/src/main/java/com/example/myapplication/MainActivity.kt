package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.logic.FiveInLine
import com.example.myapplication.view.MyView

class MainActivity : AppCompatActivity() {
    private lateinit var gameBoard: MyView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard = findViewById(R.id.gameBoard)
        var chess = FiveInLine()
        chess.init(19, 19)
        gameBoard.setChess(chess)
    }
}