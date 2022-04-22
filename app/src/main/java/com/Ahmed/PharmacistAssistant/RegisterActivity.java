package com.Ahmed.PharmacistAssistant;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
;
import android.net.Uri;

import android.os.Bundle;

import android.provider.Settings;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.databinding.ActivityRegisterBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;



public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private  String user , phone ,deviceId;
    private DatabaseReference ref;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.toHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog();
            }
        });
        preferences = getSharedPreferences("My preferences",MODE_PRIVATE);
        editor = preferences.edit();
        if (preferences.contains("User Name") ||
                preferences.contains("User Phone") ||
                preferences.contains("deviceId"))
        {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        }else if ( preferences.contains("User Name") ||
                preferences.contains("User Phone") ||
                preferences.contains("deviceId")||
        preferences.contains("key")){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }

        ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://manager-pharmacy-default-rtdb.firebaseio.com/");
        binding.register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {

                user = binding.user.getText().toString().trim();
                phone =binding.phone.getText().toString();

                 deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                         Settings.Secure.ANDROID_ID);
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(phone))
                {
                    Toast.makeText(RegisterActivity.this, "أكمل الحقول", Toast.LENGTH_SHORT).show();
                }else if (binding.phone.length() < 11)
                {
                    Toast.makeText(RegisterActivity.this, "الرقم غير صحيح", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(deviceId)) {
                                Toast.makeText(RegisterActivity.this, "تم التسجيل مسبقاً", Toast.LENGTH_SHORT).show();
                            } else {

                                ref.child("Users").child(deviceId).child("User Name").setValue(user);
                                ref.child("Users").child(deviceId).child("DateLogin").setValue(ServerValue.TIMESTAMP);
                                ref.child("Users").child(deviceId).child("Expired").setValue(0);
                                ref.child("Users").child(deviceId).child("User Phone").setValue(phone);
                                ref.child("Users").child(deviceId).child("deviceId").setValue(deviceId);
                                ref.child("Users").child(deviceId).child("key").setValue("");
                                editor.putString("user",user);
                                editor.putString("User Phone",phone);
                                editor.putString("deviceId",deviceId);
                                editor.commit();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }


    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String options [] = {"فيسبوك","وتساب","تليكرام"};
        builder.setTitle("أختر للمراسلة");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {

                    getMessenger();
                }else if (i==1){
                    onClickWhatsapp();

                }
                else if ( i == 2){
                    onClickTelegram();

                }
            }
        }).create().show();

    }

    @SuppressLint("HardwareIds")
    private void getMessenger() {
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Uri uri = Uri.parse("fb-messenger://user/100002612665292");

        Intent toMessenger= new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(toMessenger);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(getApplicationContext(), "Please Install Facebook Messenger",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint({"IntentReset", "HardwareIds"})
    private void onClickTelegram() {
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Uri uri = Uri.parse("https://t.me/Alqdees");
        Intent toTelegram= new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(toTelegram);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(getApplicationContext(), "Please Install Telegram",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("HardwareIds")
    private void onClickWhatsapp() {
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            Intent waIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/9647812591236?text="+deviceId));
            startActivity(waIntent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }

    }

}