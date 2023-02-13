package com.Ahmed.PharmacistAssistant.Controler.Service;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class MyReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult result = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isOnline(context)){
                    ComponentName componentName = new ComponentName(context, MyJobService.class);
        JobInfo info;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
            info= new JobInfo.Builder(10,componentName)
                    .setPeriodic(5000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();

        }
        else
        {
            info= new JobInfo.Builder(10,componentName)
                    .setMinimumLatency(5000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
        }
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
                }
            }
        }).start();
     }
     public boolean isOnline(Context context){

        try {
            ConnectivityManager connect = (ConnectivityManager)
                    context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = connect.getActiveNetworkInfo();
            return (info != null && info.isAvailable());
        }catch (Exception e){
            return false;
        }

     }
}