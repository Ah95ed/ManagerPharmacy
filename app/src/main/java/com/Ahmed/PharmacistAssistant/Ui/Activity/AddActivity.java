package com.Ahmed.PharmacistAssistant.Ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.Controler.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.model.Model;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class AddActivity extends AppCompatActivity {
    private EditText nameEt,codeEt,CostPriceEt,sellPriceEt ,dateEt,quantityEt;
    public static DecoratedBarcodeView barcodeView;
    public static CameraSettings cameraSettings;

    private static final byte CAMERA_REQUEST_CODE=100;
    private static final byte STORAGE_REQUEST_CODE=102;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    public static String ID,name,code,cost,sell,date,quantity;
    private DBSqlite dataBase;
    private boolean isEditMode = false;
    private boolean isFlash = false ;
    private int C_day,C_month,C_year;
    private ToneGenerator toneGen1;
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEt = findViewById(R.id.nameEt);
        codeEt = findViewById(R.id.barCodeEt);
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        CostPriceEt = findViewById(R.id.CostPrice);
        sellPriceEt = findViewById(R.id.sellPrice);
        dateEt = findViewById(R.id.date);
        quantityEt = findViewById(R.id.quantity);
        dateEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar calendar = Calendar.getInstance();
                 C_year = calendar.get(Calendar.YEAR);
                 C_month = calendar.get(Calendar.MONTH);
                 C_day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog  =
                        new DatePickerDialog(
                                AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEt.setText(year+"-"+month+"-"+dayOfMonth);
                    }
                },C_year,C_month,C_day);
                dialog.show();


                return false;
            }
        });

        dataBase = new DBSqlite(this);
        Intent intent =getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);
        if (isEditMode){

        ID = intent.getStringExtra("ID");
        name = intent.getStringExtra("NAME");
        code = intent.getStringExtra("CODE");
        cost = intent.getStringExtra("COST");
        sell = intent.getStringExtra("SELL");
        date = intent.getStringExtra("DATE");
//        date = intent.getStringExtra("DATE");


        nameEt.setText(name);
        codeEt.setText(code);
        CostPriceEt.setText(cost);
        sellPriceEt.setText(sell);
        dateEt.setText(date);
            System.out.println(date);


        }
    }
    private void insertData() {
        name = nameEt.getText().toString();
        code = codeEt.getText().toString();
        cost = CostPriceEt.getText().toString();
        sell = sellPriceEt.getText().toString();
        date = dateEt.getText().toString();
        quantity= quantityEt.getText().toString();

        if (name == "" || date.isEmpty()){
            Toast.makeText(AddActivity.this, "isEmpty", Toast.LENGTH_SHORT).show();
        }
        if (isEditMode) {
//            name, code, cost, sell,ID
            dataBase.updateData(
                    new Model(name, code, cost, sell,ID,date,quantity));
            Toast.makeText(AddActivity.this, "تم تحديث المعلومات", Toast.LENGTH_SHORT).show();
            onResume();

        } else{
            long result = dataBase.insertData(
                    new Model(name,code,cost,sell,date,quantity));
            if (result != -1) {
                Toast.makeText(AddActivity.this, "تمت الاضافة", Toast.LENGTH_SHORT).show();
                onResume();
            } else {
                Toast.makeText(AddActivity.this, "فشلت الاضافة", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void openCamera(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null,false);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        scannerView = v.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this,scannerView);
//        codeScanner.getAutoFocusMode();
        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        codeEt.setText(result.getText());
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
//        barcodeView.decodeSingle(new BarcodeCallback() {
//            @Override
//            public void barcodeResult(BarcodeResult result) {
//                if (result.getText() != null) {
//                    codeEt.setText(result.getText());
//                    barcodeView.pause();
//                    dialog.dismiss();
//                    isFlash =false;
//                }
//            }
//        });
//        AppCompatButton flash = v.findViewById(R.id.flash);
//        AppCompatButton Close = v.findViewById(R.id.close);
//        Close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                barcodeView.pause();
//                dialog.dismiss();
//            }
//        });
//        flash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isFlash){
//                    barcodeView.setTorchOn();
//                    isFlash = true;
//                }else{
//                    barcodeView.setTorchOff();
//                    isFlash = false;
//                }
//            }
//        });
        dialog.setView(v);
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ide = item.getItemId();
        if (ide == R.id.OpenCamera)
        {
            openCamera();
        }
        else if (ide == R.id.addItem)
        {
            insertData();
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermissions(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                boolean cameraAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean storageAccepted =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && storageAccepted)
                {
                    checkCameraPermissions();
                    checkStoragePermission();
                }else {
                    requestCameraPermission();
                    requestStoragePermission();
                }
            }
        }
    }
}