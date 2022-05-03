package com.Ahmed.PharmacistAssistant.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DBSqlite;
import com.Ahmed.PharmacistAssistant.helper.Helper;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class AddActivity extends AppCompatActivity {
    private EditText nameEt,codeEt,CostPriceEt,sellPriceEt,doseEt,drugEt,mostEt,mechanismEt,pregnancyEt;
    public static DecoratedBarcodeView barcodeView;
    public static CameraSettings cameraSettings;
    private ActionBar actionBar;
    private static final byte CAMERA_REQUEST_CODE=100;
    private static final byte STORAGE_REQUEST_CODE=102;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    public static String ID,name,code,cost,sell,dose,drug,most,mechanism,pregnancy;
    private DBSqlite dataBase;
    private boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        actionBar = getSupportActionBar();
        actionBar.setTitle("@string/addItem");
        nameEt = findViewById(R.id.nameEt);
        codeEt = findViewById(R.id.barCodeEt);
        doseEt = findViewById(R.id.dose);
        drugEt = findViewById(R.id.drug);
        mostEt = findViewById(R.id.mostSide);
        mechanismEt = findViewById(R.id.mechanism);
        pregnancyEt = findViewById(R.id.pregnancy);
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        CostPriceEt = findViewById(R.id.CostPrice);
        sellPriceEt = findViewById(R.id.sellPrice);
        dataBase = new DBSqlite(this);
        Intent intent =getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);
        if (isEditMode){
        actionBar.setTitle("تحديث المعلومات");
        ID = intent.getStringExtra("ID");
        name = intent.getStringExtra("NAME");
        code = intent.getStringExtra("CODE");
        cost = intent.getStringExtra("COST");
        sell = intent.getStringExtra("SELL");/*dose,drugName,mostSideEffect,mechanismOfAction,pregnancy*/
        dose = intent.getStringExtra("dose");
        drug = intent.getStringExtra("drug");
        most = intent.getStringExtra("mostSide");
        mechanism = intent.getStringExtra("mechanism");
        pregnancy = intent.getStringExtra("pregnancy");
        nameEt.setText(name);
        codeEt.setText(code);
        CostPriceEt.setText(cost);
        sellPriceEt.setText(sell);
        doseEt.setText(dose);
        drugEt.setText(drug);
        mostEt.setText(most);
        mechanismEt.setText(mechanism);
        pregnancyEt.setText(pregnancy);
        }else {
            actionBar.setTitle("أضافة علاج");
        }
    }
    private void insertData() {
        name = nameEt.getText().toString();
        code = codeEt.getText().toString();
        cost = CostPriceEt.getText().toString();
        sell = sellPriceEt.getText().toString();
        dose = doseEt.getText().toString();
        drug = drugEt.getText().toString();
        most = mostEt.getText().toString();
        mechanism = mechanismEt.getText().toString();
        pregnancy = pregnancyEt.getText().toString();
        if (name == "" ){
            Toast.makeText(AddActivity.this, "حقل الاسم فارغ", Toast.LENGTH_SHORT).show();
        }
        if (isEditMode) {
            dataBase.updateData( ""+name, ""+code, ""+cost, ""+sell,""+ID,""+dose,""+drug,""+most,
                    ""+mechanism,""+pregnancy);
            Toast.makeText(AddActivity.this, "تم تحديث المعلومات", Toast.LENGTH_SHORT).show();
            onResume();

        } else{
            long result = dataBase.insertData("" + name, "" + code, "" + cost, "" + sell,
                    ""+dose,""+drug,""+most,""+mechanism,""+pregnancy);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        MenuItem item = menu.findItem(R.id.OpenCamera);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.OpenCamera){
            Helper.openCamera(AddActivity.this,codeEt);
        }
        else if (id == R.id.addItem){
            insertData();
        }
        return super.onOptionsItemSelected(item);
    }
//    public void openCamera(AddActivity.this,EditText text){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View v = LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null,false);
//        AlertDialog dialog=builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        barcodeView = v.findViewById(R.id.barcode_scanner);
//        cameraSettings =new CameraSettings();
//        cameraSettings.setRequestedCameraId(0);
//        cameraSettings.setAutoFocusEnabled(true);
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
//                }
//            }
//        });
//        Button Close = v.findViewById(R.id.close);
//        Close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                barcodeView.pause();
//                dialog.dismiss();
//            }
//        });
//        dialog.setView(v);
//        dialog.show();
//    }
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
                }else
                    requestCameraPermission();
                requestStoragePermission();
            }
        }
    }
}