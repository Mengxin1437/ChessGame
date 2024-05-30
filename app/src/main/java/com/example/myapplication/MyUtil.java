package com.example.myapplication;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 我的工具类
 */
public class MyUtil {
    /**
     * 从文件中获取ip和port
     */
    public static HashMap<String, String> getIpAndPort(AssetManager assetManager){
        try {
            InputStream inputStream = assetManager.open("config.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HashMap<String, String> hm = new HashMap<>();

            String line;
            while((line=reader.readLine())!=null){
                String[] kv = line.split("=");
                hm.put(kv[0], kv[1]);
            }
            return hm;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
