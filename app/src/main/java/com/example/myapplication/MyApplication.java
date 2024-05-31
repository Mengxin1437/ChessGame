package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.Info.Message;

import java.net.Socket;

//可以看成是全局变量
public class MyApplication extends Application {
    public Socket socket = null;
    public Message message = null;
    public String flag = null; //"0"执白 其它：执黑
    public String gameMod = null; //practice online localNetwork
}
