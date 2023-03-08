package com.Ahmed.PharmacistAssistant.View.Activity;


import static com.Ahmed.PharmacistAssistant.Controler.database.DBSqlite.C_CODE;
import static com.Ahmed.PharmacistAssistant.Controler.database.DBSqlite.C_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Ahmed.PharmacistAssistant.Controler.Service.MyJobService;
import com.Ahmed.PharmacistAssistant.Controler.Service.MyReceiver;
import com.Ahmed.PharmacistAssistant.View.Adapter.AdapterTwo;
import com.Ahmed.PharmacistAssistant.View.Adapter.PdfDocumentAdapter;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.Controler.database.DB;
import com.Ahmed.PharmacistAssistant.Controler.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.model.Favorite;
import com.Ahmed.PharmacistAssistant.model.Model;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.installations.BuildConfig;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;


public class CameraOpenActivity extends AppCompatActivity {
    private final String savePrice = "My AllPrice";
    private final String All = "AllPrice";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CodeScanner codeScanner;
    private DBSqlite db;
    private int numberPage;
    private Uri uri;
    public TextView result;
    private String  id, named, selles, cost, code;
    private RecyclerView recyclerview;
    public double results;
    private DB d;
    private double res, calc;
    private AdapterTwo adapterRecord;
    private ToneGenerator toneGen1;
    private String[] camera;
    private CodeScannerView scannerView;
    private static int CAMERA_REQUEST_CODE = 100;

    @SuppressLint({"NotifyDataSetChanged", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_open);
        preferences = getSharedPreferences(savePrice, MODE_PRIVATE);
        editor = preferences.edit();
        camera = new String[]{Manifest.permission.CAMERA};
        scannerView = findViewById(R.id.scanner_view);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 150);
        codeScanner = new CodeScanner(this, scannerView);
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
        d = new DB(this);
        recyclerview = findViewById(R.id.recordR);
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        builders.detectFileUriExposure();
        result = findViewById(R.id.tv_total);
        if (checkCameraPermission()){
            opeeen();
        }else {
            requestCameraPermission();
        }
        db = new DBSqlite(this);
    }

    private void opeeen() {
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getData(result.getText());
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 150);
                    }
                });
            }
        });
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, camera, CAMERA_REQUEST_CODE);
    }

    private ArrayList<Model> getDataName(String N) {
        String selectQuery = "SELECT * FROM " + DBSqlite.DB_TABLE + " WHERE " + C_NAME + " LIKE '%" + N + "%'";
        SQLiteDatabase database = db.getWritableDatabase();
        ArrayList<Model> models = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                named = "" + cursor.getString(0);
                code = "" + cursor.getString(1);
                cost = "" + cursor.getString(2);
                selles = "" + cursor.getString(3);
                id = "" + cursor.getString(4);
                models.add(new Model(named, code, cost, selles, id));
            } while (cursor.moveToNext());

            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            dialogNum();
        } else {
            Toast.makeText(this, "Not Found !!", Toast.LENGTH_SHORT).show();
            codeScanner.startPreview();
        }
        database.close();
        return models;

    }

    private void getData(String C) {
        String selectQuery = "SELECT * FROM " + DBSqlite.DB_TABLE + " WHERE " + C_CODE + " LIKE '%" + C + "%'";
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                named = "" + cursor.getString(0);
                code = "" + cursor.getString(1);
                cost = "" + cursor.getString(2);
                selles = "" + cursor.getString(3);
                id = "" + cursor.getString(4);
            } while (cursor.moveToNext());
            dialogNum();
        } else {
            Toast.makeText(this,
                    "Not Found !!",
                    Toast.LENGTH_SHORT).show();
            opeeen();
            codeScanner.startPreview();
        }
        database.close();
    }
    public void dialogNum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("الكمية");
        EditText edit = new EditText(this);
        edit.setText("1");
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edit);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                calc = Double.parseDouble(edit.getText().toString());
                res = calc * Double.parseDouble(selles);
                addData();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                opeeen();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void addData() {
        Favorite m = new Favorite(named, cost, String.valueOf(res), code, id, String.valueOf(calc));
        boolean add = d.add(m);
        if (add) {
            adapterRecord = new AdapterTwo(new ArrayList<Favorite>(), CameraOpenActivity.this);
            adapterRecord.updateItems(d.getFav(id));
            recyclerview.setAdapter(adapterRecord);

            if (!result.getText().toString().isEmpty() && !result.getText().toString().equals("المجموع")) {
                results = Double.parseDouble(result.getText().toString());
                results += res;
                result.setText(String.valueOf(results));
                onStart();
            } else {
                results += res;
                result.setText(String.valueOf(results));
                onStart();
            }

        } else {
            Toast.makeText(this, "Field", Toast.LENGTH_SHORT).show();
            opeeen();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_name, menu);
        MenuItem item = menu.findItem(R.id.searchName);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getDataName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ide = item.getItemId();
        if (ide == R.id.delete) {
            d.deletedList();
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.clear();
            editor2.commit();
            result.setText("المجموع");
            results = 0.0;
            onStart();
        } else if (ide == R.id.print) {
            sendData();
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendData() {
//      Create a new folder using Intent
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "PDF");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 2);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {

            //  ** /document/primary:My New PDF.pdf: open failed: ENOENT (No such file or directory)*/
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    uri = data.getData();
                    int numberItem = 1;
                    ArrayList<Favorite> arrayList = d.getFav(DB.id);
                    try {
                        // Create a new PDF document
                        PdfDocument document = new PdfDocument();
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(12);
                        int yHi = 60;
                        canvas.drawText("مع تمنياتنا لكم بالشفاء العاجل", 200, 20, paint);
                        canvas.drawText("اسم العلاج", 20, yHi, paint);
                        canvas.drawText("الكمية", 235, yHi, paint);
                        canvas.drawText("السعر", pageInfo.getPageWidth() - 60, yHi, paint);
                        int StartY = 80;
                        for (int i = 0; i < arrayList.size(); i++) {
                            canvas.drawText(String.valueOf(numberItem),
                                    5, StartY, paint);
                            canvas.drawText(arrayList.get(i).getName(),
                                    22, StartY, paint);
                            canvas.drawText(arrayList.get(i).getQuantity(),
                                    235, StartY, paint);
                            canvas.drawText(arrayList.get(i).getSell(),
                                    pageInfo.getPageWidth() - 75,
                                    StartY, paint);
                            canvas.drawLine(10,
                                    StartY + 4,
                                    pageInfo.getPageWidth() - 10,
                                    StartY + 4, paint);

                            StartY += 20;
                            numberItem++;
                            if (numberItem == 24) {
                                numberPage += 1;
                                document.finishPage(page);
                                document.startPage(pageInfo);
                            }
                        }
                        document.finishPage(page);
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        document.writeTo(outputStream);
                        document.close();
                        outputStream.close();
                        arrayList.clear();
                        db.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         Intent shareIntent = new Intent(Intent.ACTION_SEND);
                         shareIntent.setType("application/pdf");
                         shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                         startActivity(Intent.createChooser(shareIntent, "أرسال ملف PDF"));
                     }
                 });
                }
            });
            thread.start();
        }else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            opeeen();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        codeScanner.stopPreview();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreference();
        codeScanner.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
//        barcodeView.resume();
        codeScanner.startPreview();
        getShared();
    }

    public void getShared() {
        preferences = getSharedPreferences(savePrice, MODE_PRIVATE);

        if (!preferences.contains(All)) {
            return;
        } else {
            result.setText(preferences.getString(All, "0.0"));
            editor.clear();
            editor.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        codeScanner.stopPreview();
//        barcodeView.pause();
        sharedPreference();
    }

    private void sharedPreference() {
        if (result.getText().toString().isEmpty() || result.getText().toString().equals("المجموع")) {
            return;
        } else {
            editor = preferences.edit();
            editor.putString(All, result.getText().toString());
            editor.commit();
            editor.apply();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        codeScanner.stopPreview();
        sharedPreference();
//        barcodeView.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        barcodeView.resume();
        getShared();
        codeScanner.startPreview();
//        openCam();

    }


    @Override
    public void onStart() {
        super.onStart();
//        openCam();
        codeScanner.startPreview();
        adapterRecord = new AdapterTwo(d.getFav(DB.id), CameraOpenActivity.this);
        adapterRecord.updateItems(d.getFav(DB.id));
        recyclerview.setLayoutManager(new LinearLayoutManager(CameraOpenActivity.this));
        recyclerview.hasFixedSize();
        recyclerview.setAdapter(adapterRecord);
        jobService();

    }
    public  void jobService() {

        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,filter);
        ComponentName componentName = new ComponentName(CameraOpenActivity.this, MyJobService.class);
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