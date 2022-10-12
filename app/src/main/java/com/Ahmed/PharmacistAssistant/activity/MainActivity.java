
package com.Ahmed.PharmacistAssistant.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.Adapter.AdapterRecord;
import com.Ahmed.PharmacistAssistant.AdapterAndService.MyJobService;
import com.Ahmed.PharmacistAssistant.AdapterAndService.MyReceiver;
import com.Ahmed.PharmacistAssistant.BuildConfig;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.model.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton,voice;
    private RecyclerView recordRv;
    private BottomNavigationView navigationView;
    private DBSqlite db;
    public static DecoratedBarcodeView barcodeView;
    public static CameraSettings cameraSettings;
    private static final byte CAMERA_REQUEST_CODE = 100;
    private static final byte STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final byte STORAGE_REQUEST_CODE_IMPORT = 2;
    private static final byte SPEECH_REQUEST = 101;
    private String[] storagePermissions;
    private ArrayList<Model> array;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;

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
    private Boolean isFlash;
//    private ImageButton imageButton;
    private BottomAppBar appBar;
    private EditText search;


    @SuppressLint({"HardwareIds", "SimpleDateFormat", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.bottomnavigtionView);
        navigationView.setBackground(null);
        search = findViewById(R.id.search);

        search.setVisibility(View.GONE);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        openCamera();
                        return true;
                    case R.id.search:
                        search.setVisibility(View.VISIBLE);
                        searchBar(search.getText().toString());
                        search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
                    case R.id.menu:
                        speechToText();
                        break;


                }
                return false;
            }
        });

        isFlash = false;
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
            getPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraPermissions = new String[]{Manifest.permission.CAMERA};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE};
        floatingActionButton = findViewById(R.id.add_item);
//        voice = findViewById(R.id.speech);
//        voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                speechToText();
//            }
//        });
        recordRv = findViewById(R.id.recordRv);
        db = new DBSqlite(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        currentVersionCod = getCurrentVersionCode();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                final String newVersion = remoteConfig.getString("newVersion");
                if (Integer.parseInt(newVersion) > getCurrentVersionCode()) {
                    showUpdateDialog();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        MainActivity.this,
                        e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchRecord(result.get(0));
             }else {

                }
        }
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
    private void importCSV() throws IOException, CsvValidationException {
        String filePathAndName = Environment
                .getExternalStorageDirectory() + "/" + "SQLiteBackup/" + "SQLite_Backup.csv";
        File csvFile = new File(filePathAndName);
        if (csvFile.exists()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
                        String[] nextLine;
                        while ((nextLine = csvReader.readNext()) != null) {
                            String name = nextLine[0];
                            String code = nextLine[1];
                            String cost = nextLine[2];
                            String sell = nextLine[3];
                            long getData = db.importData(name, code, cost, sell);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             onStart();
                            }
                        });

                    } catch (Exception e) {

                        Log.d("IMPORT",e.getMessage());
                    }
                }
            });
            thread.start();

        } else {
            Toast.makeText(this, "الفايل غير موجود", Toast.LENGTH_SHORT).show();
        }
    }
    private void exportCSV() {

        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup");
        boolean isFolderCreate = false;
        if (!folder.exists()) {
            isFolderCreate = folder.mkdir();
        }
        Log.d("CSV", "exportCSV" + isFolderCreate);
        String csvFileName = "SQLite_Backup.csv";
        String filePathAndName = folder.toString() + "/" + csvFileName;
        db = new DBSqlite(this);
        ArrayList<Model> recordArray = new ArrayList<>();
        recordArray.clear();
        recordArray = db.getAllRecords(DBSqlite.C_ID);
        try {
            FileWriter fw = new FileWriter(filePathAndName);
            Arrays.toString(fw.getEncoding().getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < recordArray.size(); i++) {
                fw.append(recordArray.get(i).getName());
                fw.append(",");
                fw.append(recordArray.get(i).getCode());
                fw.append(",");
                fw.append(recordArray.get(i).getCost());
                fw.append(",");
                fw.append(recordArray.get(i).getSell());
                fw.append(",");
                fw.append( recordArray.get(i).getDose());
                fw.append(",");
                fw.append( recordArray.get(i).getDrugName());
                fw.append(",");
                fw.append(recordArray.get(i).getMostSideEffect());
                fw.append(",");
                fw.append(recordArray.get(i).getMechanismOfAction());
                fw.append(",");
                fw.append(recordArray.get(i).getPregnancy());
                fw.append("\n");
            }
            fw.flush();
            fw.close();
            Toast.makeText(MainActivity.this, "تم الاستخراج بنجاح" + filePathAndName, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void loadRecords() {
        array =  db.getAllRecords(DBSqlite.C_ID);
        AdapterRecord adapter = new AdapterRecord(MainActivity.this,array);
        recordRv.setAdapter(adapter);
//        actionBar.setSubtitle("" + db.getAllCounts());
    }
    private void searchRecord(String name) {
        array = db.getAllRecords(DBSqlite.C_ID);
        ArrayList<Model> models = new ArrayList<>();

        for (Model m : array) {
            if (m.getName().toLowerCase().contains(name.toLowerCase()))
            {
                models.add(m);
            }
        }
        AdapterRecord adapterRecord = new AdapterRecord(this, models);
        recordRv.setAdapter(adapterRecord);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.navigationbottom,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = null;
        searchView = (SearchView) item.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecord(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecord(newText);
                return true;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Backup) {
            if (checkStoragePermission()) {
                exportCSV();
            } else {
                requestStoragePermissionExport();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        false == Environment.isExternalStorageManager()) {
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        false == Environment.isExternalStorageManager()) {
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2 &&
                        false == Environment.isExternalStorageManager()) {
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
                }
            }

        } else if (id == R.id.restore) {
            if (checkStoragePermission()) {

                try {
                    importCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvValidationException e) {
                    e.printStackTrace();
                }
            } else {
                requestStoragePermissionImport();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        false == Environment.isExternalStorageManager()) {
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
                }
            }
        } else if (id == R.id.listShow) {
            openActivity();
        } else if (id == R.id.camera) {
            if (!checkCameraPermission()) {
                requestCameraPermission();
            }
            openCamera();
        } else if (id == R.id.update) {
            updateAllCostAndSell();
        } else if (id == R.id.Calculate) {
            openCalculate();
        }else if (id == R.id.deleted){
            db.deletedAll();
            onResume();
            onStart();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAllCostAndSell() {
        startActivity(new Intent(MainActivity.this, UpdateColumnActivity.class));
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        exportCSV();
                    } catch (Exception e) {
                        Log.d("" + e.getMessage(), "GRANTED");
                    }
                } else {
                    Toast.makeText(this, "نحتاج صلاحيات للذاكرة", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE_IMPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
//                        importCSV();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "نحتاج صلاحيات للذاكرة" + requestCode, Toast.LENGTH_LONG).show();
                }
            }
            break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "تحتاج الى صلاحيات الكاميرا", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void openCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null,false);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        barcodeView = v.findViewById(R.id.barcode_scanner);
        cameraSettings =new CameraSettings();
        cameraSettings.setRequestedCameraId(0);
        cameraSettings.setAutoFocusEnabled(true);
        barcodeView.getBarcodeView().setCameraSettings(cameraSettings);
        barcodeView.resume();

        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    searchBar(result.getText());
                    barcodeView.pause();
                    dialog.dismiss();
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,160);
                    isFlash = false;
                    barcodeView.setTorchOff();
                }
            }
        });
        Button Close = v.findViewById(R.id.close);
        Button flash = v.findViewById(R.id.flash);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               barcodeView.pause();
//                cameraSource.stop();
                dialog.dismiss();
                barcodeView.setTorchOff();
                isFlash = false;
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!isFlash) {
                   barcodeView.setTorchOn();
                   isFlash = true;
               }
               else {
                   barcodeView.setTorchOff();
                   isFlash = false;
               }
            }
        });
        dialog.setView(v);
        dialog.show();
    }

    private void searchBar(String results) {
        array = db.getAllRecords(DBSqlite.C_ID);
        ArrayList<Model> models = new ArrayList<>();
        for (Model m : array) {
            if (m.getCode().contains(results))
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
        isFlash = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Expired();

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
        loadRecords();

        getPermission();
        ref.child("Users").child(deviceId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
    }
    @SuppressLint("ObsoleteSdkInt")
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED) {
                requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
            } else {
                checkSelfPermission(Manifest.permission.CAMERA);
            }
        }
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