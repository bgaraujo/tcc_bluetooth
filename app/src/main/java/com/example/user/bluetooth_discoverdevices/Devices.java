package com.example.user.bluetooth_discoverdevices;

import android.media.MediaPlayer;

public class Devices {
    public String name, code;
    MediaPlayer sound;
    public int signalLimit,time;


    public Devices(final String name,final String code,final MediaPlayer sound , int signalLimit , int time) {
        this.name = name;
        this.code = code;
        this.sound = sound;
        this.signalLimit = signalLimit;
        this.time = time;
    }

}
