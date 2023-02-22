
package com.Ahmed.PharmacistAssistant.View.Activity;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.Ahmed.PharmacistAssistant.BuildConfig;

import android.widget.ProgressBar;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.View.Adapter.AdapterRecord;
import com.Ahmed.PharmacistAssistant.Controler.Service.MyJobService;
import com.Ahmed.PharmacistAssistant.Controler.Service.MyReceiver;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.Controler.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.model.Model;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Table;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.zxing.Result;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton;
    private RecyclerView recordRv;
    private BottomNavigationView navigationView;
    private androidx.core.widget.ContentLoadingProgressBar progressBar;
    private DBSqlite db;
    private ActionBar actionBar;
    private static final byte CAMERA_REQUEST_CODE = 100;
    private static final byte SPEECH_REQUEST = 101;
    private ArrayList<Model> array;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private String[] cameraPermissions;
    private DatabaseReference ref;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String deviceId,date;
    private Calendar calendar;
    private SimpleDateFormat simple;
    private ArrayList<String> result;
    private FirebaseRemoteConfig remoteConfig;
    private int currentVersionCod;
     int limit = 30;
     int skip = 0;

    private EditText search;;

    // var pagination
    private boolean loading = true;
    private int pastVisibleItems,visibleItemCount,totalItemCount;
    private LinearLayoutManager layoutManager;
    private AdapterRecord adapterRecord;
    private androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;
    @SuppressLint({"HardwareIds", "SimpleDateFormat", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        db = new DBSqlite(this);
        array = new ArrayList<>();
        progressBar = findViewById(R.id.progress);
        progressBar.hide();
        progressBar.setVisibility(View.GONE);
        navigationView = findViewById(R.id.bottomnavigtionView);
        navigationView.setBackground(null);
        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);
        coordinatorLayout = findViewById(R.id.Relative);
        coordinatorLayout.setOnClickListener((View view) -> {
            getData();
            System.out.println("______________________"+array.size());
        });
//        loadRecords();
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        if (checkCameraPermission()){
                            openCamera();
                        }else {
                            requestCameraPermission();
                        }
                        return true;
                    case R.id.search:
                        search.setVisibility(View.VISIBLE);
                        searchBar(search.getText().toString());
                        search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s,
                                                          int start,
                                                          int count,
                                                          int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                searchRecord(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        return true;
                    case R.id.voice:
                        speechToText();
                        break;
                    case R.id.menu:
                        openBottomSheet();
                        break;

                }
                return false;
            }
        });


//        jobService();
        calendar = Calendar.getInstance();
        simple = new SimpleDateFormat("dd-MM-yyyy");
        date = simple.format(calendar.getTime());
        ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://manager-pharmacy-default-rtdb.firebaseio.com/");
        deviceId = Settings.Secure.getString(getApplicationContext().
                getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            @SuppressLint("DiscouragedPrivateApi")
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size

        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraPermissions = new String[]{Manifest.permission.CAMERA};
        floatingActionButton = findViewById(R.id.add_item);

        recordRv = findViewById(R.id.recordRv);
        layoutManager = new LinearLayoutManager(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(MainActivity.this,AddActivity.class));
            }
        });

        currentVersionCod = getCurrentVersionCode();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        recordRv.setLayoutManager(layoutManager);
//        recordRv.setOnClickListener((View view) -> {
//            getData();
//            onStart();
//            System.out.println("______________________"+array.size());
//        });
        recordRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount =layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (dy>0){
                    if (loading){
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount){
                            loading = false;
                            skip += limit;
                            getData();
                            loading = true;
                        }
                    }
                }
            }
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    private void getData(){
        ArrayList<Model> arrayList = db.getRecords(limit,skip);
        array.addAll(arrayList);
        adapterRecord = new AdapterRecord(this,array);
        recordRv.setAdapter(adapterRecord);
        adapterRecord.notifyDataSetChanged();
    }
    private void openBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                MainActivity.this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.bottom_sheet,null,false);

        bottomSheetView.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)
                {
                    try {
                        exportCSV();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id._import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    importCSV();
                } catch (IOException | CsvValidationException e) {
                    throw new RuntimeException(e);
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAllCostAndSell();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.dose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalculate();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.delet).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                db.deletedAll();
                array.clear();
                adapterRecord.notifyDataSetChanged();
                onStart();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.Debts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DebtsActivity.class));
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    private void speechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                R.string.SpeechToText);
        try {

            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void exportCSV() throws IOException {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("csv/write");
        intent.putExtra(Intent.EXTRA_TITLE, "my.csv");
        startActivityForResult(intent, 111);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchRecord(result.get(0));
             }
                break;
            case 111: {
                if (resultCode == RESULT_OK){
                    assert data != null;
                    Uri uri = data.getData();
                    exportData(uri);
                }
            }
            break;
            case 1:{
                if (data == null){
                    Toast.makeText(this,
                            "isEmpty",
                            Toast.LENGTH_SHORT).show();
                return;
                }
                Uri uri = data.getData();
                importData(uri);

            }
            break;
        }
    }

    private void exportData(Uri uri) {
        progressBar.show();
        array = db.getAllRecords();
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            for (int i = 0; i < array.size(); i++) {
                outputStream.write(array.get(i).getName().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getCode().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getCost().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getSell().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getId().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getDate().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write(array.get(i).getQuantity().getBytes(StandardCharsets.UTF_8));
                outputStream.write(",".getBytes());
                outputStream.write("\n".getBytes());

            }

            outputStream.close();
            outputStream.flush();
            progressBar.hide();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void importData(Uri uri) {

        progressBar.show();
        progressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream =
                            getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] name = line.split(",");
                        String[] code = line.split(",");
                        String[] cost = line.split(",");
                        String[] sell = line.split(",");
                        String[] id = line.split(",");
                        String[] date = line.split(",");
                        String[] quo = line.split(",");
                        db.importData(
                                new Model(
                                        ""+ name[0],
                                        ""+code[1],
                                        ""+cost[2],
                                        ""+sell[3],
                                        ""+id[4],
                                        ""+date[5],
                                        ""+quo[6])
                        );

                    }
                    runOnUiThread(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            progressBar.hide();
                            progressBar.setVisibility(View.INVISIBLE);
                            onStart();
                        }
                    });
                } catch (Exception e) {
                            Log.d("IMPORT",e.getMessage());
                }
            }
        });
        thread.start();

    }

    private void importCSV() throws IOException, CsvValidationException {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        String[] mimeTypes = {"text/csv",
                "text/comma-separated-values",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);
    }


    private void showUpdateDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("New Update Available");
        dialog.setMessage("Update Now ..");
        dialog.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW
                            , Uri.parse("https://pharmacist-assistant.ar.uptodown.com/android/download")));
                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).create().show();
        dialog.setCancelable(false);
    }
    private int getCurrentVersionCode() {
        PackageInfo packageInfo = null;
        try {

            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        assert packageInfo != null;
        return packageInfo.versionCode;
    }

    private void searchRecord(String name) {
        array = db.getAllRecords();
        ArrayList<Model> models = new ArrayList<>();

        for (Model m : array) {
            if (m.getName().toLowerCase().contains(name.toLowerCase())) {
                models.add(m);
            }
        }
        AdapterRecord adapterRecord = new AdapterRecord(this, models);
        recordRv.setAdapter(adapterRecord);
    }
    private void updateAllCostAndSell() {
        startActivity(new Intent(MainActivity.this, UpdateColumnActivity.class));
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(MainActivity.this,
                            "تحتاج الى صلاحيات الكاميرا",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @SuppressLint("MissingInflatedId")
    private void openCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null,false);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        scannerView = v.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this,scannerView);
        codeScanner.getAutoFocusMode();
        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchBar(result.getText());
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 150);
                        codeScanner.stopPreview();
                        dialog.dismiss();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        dialog.setView(v);
        dialog.show();
    }

    private void searchBar(String results) {
        array = db.getAllRecords();
        ArrayList<Model> models = new ArrayList<>();

        for (Model m : array) {
            if (m.getCode().toLowerCase().contains(results.toLowerCase()))
            {
                models.add(m);
            }
        }
        AdapterRecord adapterRecord = new AdapterRecord(this, models);
        recordRv.setAdapter(adapterRecord);
    }
    private void openCalculate() {
        startActivity(new Intent(MainActivity.this, CalculateActivity.class));
    }

    private void openActivity() {
        startActivity(new Intent(MainActivity.this, CameraOpenActivity.class));
    }
    private void dialogCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("هل متأكد من الخروج");
        String[] options = {"نعم", "لا, شكرا على التنبيه"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                finish();
                } else if (i == 1) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onBackPressed() {
        dialogCancel();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Expired();
    }
    private void Expired() {
        preferences = getSharedPreferences("My preferences", MODE_PRIVATE);
                    if (!preferences.contains("key")){
                        finish();
                    }
    }
    @Override
    public void onStart() {
        super.onStart();
        getData();
        jobService();
        ref.child("Users").child(deviceId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
    }

    public  void jobService() {

        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,filter);
        ComponentName componentName = new ComponentName(MainActivity.this, MyJobService.class);
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
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);

    }
}
