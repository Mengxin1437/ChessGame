package com.example.myapplication.Info;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 9991L;
    private String name;
    private String password;
    public int chessType; //棋的类型 0五子棋 1黑白棋 2围棋

    public Message(String name, String password, int chessType) {
        this.name = name;
        this.password = password;
        this.chessType = chessType;
    }
}
