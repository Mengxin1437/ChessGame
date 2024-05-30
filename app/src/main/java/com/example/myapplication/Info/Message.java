package com.example.myapplication.Info;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 9991L;
    private String name;
    private String password;

    public Message(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return name+" "+password;
    }
}
