
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    ActivityResultLauncher<String []> mResultLauncher;
    ActivityResultLauncher<Intent> mResultFile;
    private com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton,voice;
    private RecyclerView recordRv;
    private BottomNavigationView navigationView;
    private DBSqlite db;
    private ActionBar actionBar;
    private static final byte CAMERA_REQUEST_CODE = 100;
    private static final byte STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final byte STORAGE_REQUEST_CODE_IMPORT = 2;
    private static final byte SPEECH_REQUEST = 101;
    private String[] storagePermissions;
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

    private EditText search;

    private Boolean isReadPermissionGranted= false;
    private Boolean isWritePermissionGranted= false;

    // var pagination
    private boolean loading = true;
    private int pastVisibleItems,visibleItemCount,totalItemCount;
    private LinearLayoutManager layoutManager;


    @SuppressLint({"HardwareIds", "SimpleDateFormat", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        db = new DBSqlite(this);
        array = new ArrayList<>();

        mResultLauncher= registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
           if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
               isReadPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
           }
           if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null){
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);

           }
            }
        });
//        requestPermission();

        navigationView = findViewById(R.id.bottomnavigtionView);
        navigationView.setBackground(null);
        search = findViewById(R.id.search);

//        mCustomBottomSheet = findViewById(R.id.Relative);
        search.setVisibility(View.GONE);
//        loadRecords();
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
            getPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraPermissions = new String[]{Manifest.permission.CAMERA};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE};
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
        Log.d("totalItemCount",skip+"__skip__"+limit);
        ArrayList<Model> arrayList = db.getRecords(limit,skip);
        array.addAll(arrayList);
        AdapterRecord adapterRecord = new AdapterRecord(this,array);
        recordRv.setAdapter(adapterRecord);

        System.out.println(array.size());

    }
    private void loadRecords() {

        array =db.getAllRecords();
        array.addAll(db.getAllRecords());
        AdapterRecord adapter = new AdapterRecord(MainActivity.this,array);
        recordRv.setAdapter(adapter);
//        Log.d("DATABVASE_" ,array.get(0).getName()+"_____________");
//        actionBar.setSubtitle("" + db.getAllCounts());
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
//                    ActivityCompat.requestPermissions(
//                            MainActivity.this, new String[]
//                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
                }




//                selectCSVFile();
//                try {
//                try {
//                    exportCSV();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
////                } catch (IOException e) {
////                    throw new RuntimeException(e);
////                }
////                loadRecords();

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id._import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkImport();
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
            @Override
            public void onClick(View view) {
//                db.deletedAll();
//                onResume();
//                onStart();
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

    private void checkImport() {


        if (checkStoragePermission()) {

            try {
                importCSV();
            } catch (IOException | CsvValidationException e) {
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


    }
    private void selectCSVFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), 111);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
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
//                            String id = nextLine[4];
                            String date = nextLine[5];
                            String quantity = nextLine[6];
                            long getData = db.importData(new Model(name, code, cost, sell,date,quantity));
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
    private void requestPermission(){
        boolean minSDK= Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        isReadPermissionGranted = ContextCompat.checkSelfPermission(this,

                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(this,

                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        isWritePermissionGranted = isWritePermissionGranted ||minSDK;
        List<String> permissionRequest = new ArrayList<>();
        if (!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionRequest.isEmpty()){
            mResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }

    private void checkExport() throws IOException {

        if (checkStoragePermission()) {
            exportCSV();
        }
        else {
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
                    !Environment.isExternalStorageManager()) {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
            }
        }

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
    private boolean exportCSV() throws IOException {
//        File folder = new File(Environment.getExternalStorageDirectory()
//                + "/" + "SQLiteBackup");
//        boolean isFolderCreate = false;
//        if (!folder.exists()) {
//            isFolderCreate = folder.mkdir();
//        }
//        String csvFileName = "SQLite_Backup.csv";
//        String filePathAndName = folder.toString() + "/" + csvFileName;
        db = new DBSqlite(this);
        ArrayList<Model> recordArray = db.getAllRecords();
        String name = "my.csv";

//        try {
//            FileWriter fw = new FileWriter(name);
//            Arrays.toString(fw.getEncoding().getBytes(StandardCharsets.UTF_8));
//            for (int i = 0; i < recordArray.size(); i++) {
//                fw.append(recordArray.get(i).getName());
//                fw.append(",");
//                fw.append(recordArray.get(i).getCode());
//                fw.append(",");
//                fw.append(recordArray.get(i).getCost());
//                fw.append(",");
//                fw.append(recordArray.get(i).getSell());
//                fw.append(",");
//                fw.append("\n");
//            }
//
//            fw.flush();
//            fw.close();
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("csv/write");
        intent.putExtra(Intent.EXTRA_TITLE, "my.csv");
        startActivityForResult(intent, 111);


        Toast.makeText(MainActivity.this, "تم الاستخراج بنجاح" , Toast.LENGTH_LONG).show();

//        } catch (Exception e) {
//            Toast.makeText(MainActivity.this, "" + e.getMessage()+"1", Toast.LENGTH_LONG).show();
//            Log.d("EXPORT",e.getMessage());
//        }
        return false;
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
                break;
            case 111: {
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    db = new DBSqlite(this);
                    ArrayList<Model> recordArray = db.getAllRecords();
                    String name = "my.csv";
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        for (int i = 0; i < recordArray.size(); i++) {
                            outputStream.write(recordArray.get(i).getName().getBytes());
                            outputStream.write("\n".getBytes());

                        }

                        outputStream.close();
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }
    private void proImportCSV(File from){
        try {
            // Delete everything above here since we're reading from the File we already have
            ContentValues cv = new ContentValues();
            // reading CSV and writing table
            CSVReader dataRead = new CSVReader(new FileReader(from)); // <--- This line is key, and why it was reading the wrong file

//            SQLiteDatabase db = mHelper.getWritableDatabase(); // LEt's just put this here since you'll probably be using it a lot more than once
            String[] vv = null;
            while((vv = dataRead.readNext())!=null) {
                cv.clear();
                SimpleDateFormat currFormater  = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");

                String eDDte;
                try {
                    Date nDate = currFormater.parse(vv[0]);
                    eDDte = postFormater.format(nDate);
//                    cv.put(Table.DATA,eDDte);
                }
                catch (Exception e) {
                }
//                cv.put(Table.C,vv[1]);
//                cv.put(Table.E,vv[2]);
//                cv.put(Table.U,vv[3]);
//                cv.put(Table.C,vv[4]);
//                db.insert(Table.TABLE_NAME,null,cv);
            } dataRead.close();

        } catch (Exception e) { Log.e("TAG",e.toString());

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
    private static boolean isExternalStorageavailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void searchRecord(String name) {
        array = db.getAllRecords();
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
                try {
                    exportCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    Toast.makeText(this, grantResults.length+"_"+ grantResults[0], Toast.LENGTH_SHORT).show();
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
    @SuppressLint("MissingInflatedId")
    private void openCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null,false);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        scannerView = v.findViewById(R.id.scanner_view);
//        scannerView = findViewById(R.id.scanner_view);
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
//        barcodeView = v.findViewById(R.id.barcode_scanner);
//        cameraSettings =new CameraSettings();
//        cameraSettings.setRequestedCameraId(0);
//        cameraSettings.setAutoFocusEnabled(true);
//        barcodeView.getBarcodeView().setCameraSettings(cameraSettings);
//        barcodeView.resume();
//
//        barcodeView.decodeSingle(new BarcodeCallback() {
//            @Override
//            public void barcodeResult(BarcodeResult result) {
//                if (result.getText() != null) {
//                    searchBar(result.getText());
//                    barcodeView.pause();
//                    dialog.dismiss();
//                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,160);
//                    isFlash = false;
//                    barcodeView.setTorchOff();
//                }
//            }
//        });
//        Button Close = v.findViewById(R.id.close);
//        Button flash = v.findViewById(R.id.flash);
//
//        Close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               barcodeView.pause();
////                cameraSource.stop();
//                dialog.dismiss();
//                barcodeView.setTorchOff();
//                isFlash = false;
//            }
//        });
//        flash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               if (!isFlash) {
//                   barcodeView.setTorchOn();
//                   isFlash = true;
//               }
//               else {
//                   barcodeView.setTorchOff();
//                   isFlash = false;
//               }
//            }
//        });
        dialog.setView(v);
        dialog.show();
    }

    private void searchBar(String results) {
        ArrayList<Model> array = db.Search(results);
        System.out.println("ARRRRAY" + array.size());
        AdapterRecord adapterRecord = new AdapterRecord(this, array);
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
//        loadRecords();
        getData();
        jobService();
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

// intent.setType("text/comma-separated-values");