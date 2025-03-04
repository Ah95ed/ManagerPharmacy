package com.Ahmed.PharmacistAssistant.Controler.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.View.Activity.MainActivity;

public class BackGround extends Service {
@Nullable
@Override
public IBinder onBind(Intent intent) {
        return null;
        }

@Override
public void onCreate() {
        super.onCreate();
        }

@Override
public void onDestroy() {
        super.onDestroy();
        }

@SuppressLint("ForegroundServiceType")
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(this,0,notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this,"CHANNEL_ID")
        .setContentTitle("Recording Service")
        .setContentText("Running")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(pendingIntent)
        .build();
        startForeground(1,notification);
        return START_NOT_STICKY;
        }
        }