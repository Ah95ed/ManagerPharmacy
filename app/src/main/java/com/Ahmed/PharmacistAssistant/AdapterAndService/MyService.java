package com.Ahmed.PharmacistAssistant.AdapterAndService;

import android.annotation.SuppressLint;
import android.app.Service;

import android.content.Intent;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;

import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.Ahmed.PharmacistAssistant.activity.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;


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