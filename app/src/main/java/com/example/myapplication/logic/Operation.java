package com.example.myapplication.logic;


import android.graphics.Point;

public class Operation {
    public Point p; //对哪个点的操作
    public Boolean bl; //赋值之前的值

    public Operation(Point p, Boolean bl) {
        this.p = p;
        this.bl = bl;
    }
}
