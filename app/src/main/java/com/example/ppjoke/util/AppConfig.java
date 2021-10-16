package com.example.ppjoke.util;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.example.ppjoke.model.BottomBar;
import com.example.ppjoke.model.Destination;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.alibaba.fastjson.TypeReference;

public class AppConfig {

    private static HashMap<String, Destination> sDestConfig;

    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getsDestConfig() {
        if (sDestConfig == null) {
            String content = parseFile("destination.json");

            sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            });
        }

        return sDestConfig;
    }

    public static BottomBar getsBottomBar(){
        if (sBottomBar ==null){
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }

        return sBottomBar;
    }

    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();

        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (reader !=null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return builder.toString();
    }
}
