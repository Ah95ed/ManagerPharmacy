package com.Ahmed.PharmacistAssistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.model.Model;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class AddActivity extends AppCompatActivity {
    private EditText nameEt,codeEt,CostPriceEt,sellPriceEt;
    public static DecoratedBarcodeView barcodeView;
    public static CameraSettings cameraSettings;

    private static final byte CAMERA_REQUEST_CODE=100;
    private static final byte STORAGE_REQUEST_CODE=102;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    public static String ID,name,code,cost,sell;
    private DBSqlite dataBase;
    private boolean isEditMode = false;
    private boolean isFlash = false ;
    private ImageButton save,openCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEt = findViewById(R.id.nameEt);
        codeEt = findViewById(R.id.barCodeEt);
        save = findViewById(R.id.add);
        openCamera = findViewById(R.id.camera);
        save.setOnClickListener(v ->{
            insertData();
                });

        openCamera.setOnClickListener(v -> {
            openCamera();
        });

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        CostPriceEt = findViewById(R.id.CostPrice);
        sellPriceEt = findViewById(R.id.sellPrice);
        dataBase = new DBSqlite(this);
        Intent intent =getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);
        if (isEditMode){

        ID = intent.getStringExtra("ID");
        name = intent.getStringExtra("NAME");
        code = intent.getStringExtra("CODE");
        cost = intent.getStringExtra("COST");
        sell = intent.getStringExtra("SELL");


        nameEt.setText(name);
        codeEt.setText(code);
        CostPriceEt.setText(cost);
        sellPriceEt.setText(sell);

        }
    }
    private void insertData() {
        name = nameEt.getText().toString();
        code = codeEt.getText().toString();
        cost = CostPriceEt.getText().toString();
        sell = sellPriceEt.getText().toString();

        if (name == "" ){
            Toast.makeText(AddActivity.this, "حقل الاسم فارغ", Toast.LENGTH_SHORT).show();
        }
        if (isEditMode) {
            dataBase.updateData( name, code, cost, sell,ID);
            Toast.makeText(AddActivity.this, "تم تحديث المعلومات", Toast.LENGTH_SHORT).show();
            onResume();

        } else{
            long result = dataBase.insertData(
                    new Model(name,code,cost,sell));
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
        barcodeView = v.findViewById(R.id.barcode_scanner);
        cameraSettings =new CameraSettings();
        cameraSettings.setRequestedCameraId(0);
        cameraSettings.setAutoFocusEnabled(true);
        cameraSettings.setAutoFocusEnabled(true);
        barcodeView.getBarcodeView().setCameraSettings(cameraSettings);
        barcodeView.resume();
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    codeEt.setText(result.getText());
                    barcodeView.pause();
                    dialog.dismiss();
                    isFlash =false;
                }
            }
        });
        AppCompatButton flash = v.findViewById(R.id.flash);
        AppCompatButton Close = v.findViewById(R.id.close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeView.pause();
                dialog.dismiss();
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFlash){
                    barcodeView.setTorchOn();
                    isFlash = true;
                }else{
                    barcodeView.setTorchOff();
                    isFlash = false;
                }
            }
        });
        dialog.setView(v);
        dialog.show();
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