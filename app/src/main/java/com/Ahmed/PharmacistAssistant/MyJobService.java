package com.Ahmed.PharmacistAssistant;



import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;

public class MyJobService extends JobService {
    private Long time;
    private String dateOld,date,deviceId;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference ref;

    @SuppressLint("HardwareIds")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        SimpleDateFormat simple = new SimpleDateFormat("dd-MM-yyyy");
        deviceId =android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://manager-pharmacy-default-rtdb.firebaseio.com/");
        ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long LoginDate = snapshot.child(deviceId).child("DateLogin").getValue(Long.class);
                int Expired = snapshot.child(deviceId).child("Expired").getValue(Integer.class);
                time = snapshot.child(deviceId).child("TimeStamp").getValue(Long.class);
                dateOld = simple.format(LoginDate);
                date = simple.format(time);
                /**
                 * That is true calculate time day--> focuses { DAY }
                 **/
                try {
                    Date date1;
                    Date date2;
                    SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");
                    date1 = dates.parse(dateOld);
                    date2 = dates.parse(date);
                    long difference = Math.abs(date1.getTime() - date2.getTime());
                    long differenceDates = difference / (24 * 60 * 60 * 1000);
                    String dayDifference = Long.toString(differenceDates);
                    /**
                     * ُExpired that mean user in day use app
                     *
                     * أذا كان تاريخ الاستخدام اقل من differenceDates
                     */
                    if (differenceDates >= Expired){
                        ref.child("Users").child(deviceId).child("key").setValue("");
                        preferences = getSharedPreferences("My preferences", MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.clear();
                    editor.remove("key");
                    editor.remove("user");
                    editor.remove("User Phone");
                    editor.remove("deviceId");
                    editor.commit();
                        Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getBaseContext().startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
