package com.Ahmed.PharmacistAssistant.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseReference ref;
    public static String keyUser, deviceId,date;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Calendar calendar;
    private SimpleDateFormat simple;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        calendar = Calendar.getInstance();
        simple = new SimpleDateFormat("dd-MM-yyyy");
        date = simple.format(calendar.getTime());

        binding.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog();
            }
        });

        preferences = getSharedPreferences("My preferences",MODE_PRIVATE);
        editor = preferences.edit();
        if(preferences.contains("key")){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://manager-pharmacy-default-rtdb.firebaseio.com/");

        binding.login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {

                keyUser = binding.key.getText().toString();
                deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                if (keyUser.isEmpty()){
                    Toast.makeText(LoginActivity.this, "الحقل فارغ", Toast.LENGTH_SHORT).show();
                }else
                {
                    ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (deviceId.equals(snapshot.child(deviceId).child("deviceId").getValue(String.class))) {

                                if (keyUser.equals(snapshot.child(deviceId).child("key").getValue(String.class))) {
                                    ref.child("Users").child(deviceId).child("DateLogin").setValue(ServerValue.TIMESTAMP);
                                    editor.putString("key" ,keyUser);
                                    editor.commit();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "المفتاح غير صحيح", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(LoginActivity.this, "هذا الرمز لجهاز آخر", Toast.LENGTH_LONG).show();
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String [] options  = {"فيسبوك","وتساب","تليكرام"};
        builder.setTitle("أختر للمراسلة");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    clickMessenger();
                }else if (i==1){
                    onClickWhatsapp();
                }
                else if ( i == 2){
                    onClickTelegram();
                }
            }
        }).create().show();

        }

    private void clickMessenger() {
        Uri uri = Uri.parse("fb-messenger://user/100002612665292");

        Intent toMessenger= new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(toMessenger);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(getApplicationContext(), "Please Install Messenger",Toast.LENGTH_LONG).show();
        }
    }

    private void onClickTelegram() {
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

    private void onClickWhatsapp() {
        try {
            Intent waIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/9647812591236"));
            startActivity(waIntent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }

    }

  }