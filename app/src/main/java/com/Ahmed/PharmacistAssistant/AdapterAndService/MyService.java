package com.Ahmed.PharmacistAssistant.AdapterAndService;

import android.app.Service;

import android.content.Intent;

import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {



    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    CountDownTimer timer = new CountDownTimer(10000,2000) {
        @Override
        public void onTick(long l) {
            Log.d("START","ONTICK");
        }

        @Override
        public void onFinish() {
            Log.d("START","FINISH");
        }
    }.start();

        return START_STICKY;
    }

}