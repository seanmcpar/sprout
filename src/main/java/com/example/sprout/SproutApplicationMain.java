package com.example.sprout;

import com.example.sprout.annotations.SproutApplication;
import com.example.sprout.boot.SproutApplicationRunner;

@SproutApplication
public class SproutApplicationMain {

    public static void main(String[] args) {
        SproutApplicationRunner.run(SproutApplicationMain.class);
    }

}
