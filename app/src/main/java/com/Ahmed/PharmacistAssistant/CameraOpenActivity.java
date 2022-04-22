package com.Ahmed.PharmacistAssistant;




import static com.Ahmed.PharmacistAssistant.DBSqlite.C_CODE;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class CameraOpenActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private CameraSettings cameraSettings;
    private DBSqlite db;
    private TextView result;
    private EditText et_text;
    private String txt,id,named,selles,cost,code;
    private RecyclerView recyclerview;
    private double results;
    private String[] cameraPermissions;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=102;;
    private DB d ;
    private double res,calc;
    private byte numberPage =1;
    private AdapterTwo adapterRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_open);
        d = new DB(this);
        recyclerview = findViewById(R.id.recordR);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        barcodeView = findViewById(R.id.barcode_scanner);
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        builders.detectFileUriExposure();
        result = findViewById(R.id.tv_total);
        et_text = findViewById(R.id.et_result);
        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    getData(charSequence.toString());

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cameraSettings = new CameraSettings();
        cameraSettings.setRequestedCameraId(0);
        barcodeView.getBarcodeView().setCameraSettings(cameraSettings);
        barcodeView.resume();
        barcodeView.pause();
        db = new DBSqlite(this);
    }
    private void openCam() {

        cameraSettings.setRequestedCameraId(0);
        cameraSettings.setAutoFocusEnabled(true);
        barcodeView.getBarcodeView().setCameraSettings(cameraSettings);
        barcodeView.resume();
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                et_text.setText(result.getText());
                txt = et_text.getText().toString();
                }
        });
    }
    private void getData(String C) {
        String selectQuery = "SELECT * FROM " + DBSqlite.DB_TABLE + " WHERE " + C_CODE + " LIKE '%" + C + "%'";
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                named = "" + cursor.getString(0);
                code = ""+cursor.getString(1);
                cost = ""+cursor.getString(2);
                selles = "" + cursor.getString(3);
                id = "" + cursor.getString(4);
            }while (cursor.moveToNext());
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            dialogNum();
        }else {
            Toast.makeText(this, "Not Found !!", Toast.LENGTH_SHORT).show();
        }
        database.close();
        openCam();
    }
    public void dialogNum(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("الكمية");
            EditText edit = new EditText(this);
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
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    private void addData() {
        Model m = new Model(named, cost, String.valueOf(res), code, id, String.valueOf(calc));
        boolean add = d.add(m);
        if (add) {
            adapterRecord = new AdapterTwo(new ArrayList<Model>(),CameraOpenActivity.this);
            adapterRecord.updateItems(d.getFav(id));
            recyclerview.setAdapter(adapterRecord);
            onResume();
            results += res;
            result.setText(String.valueOf(results));
            Toast.makeText(this, " Done ", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Field", Toast.LENGTH_SHORT).show();
        }
        openCam();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_name,menu);
        MenuItem item = menu.findItem(R.id.searchName);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
        if (ide == R.id.delete)
        {
            d.deletedList();
            result.setText("");
            onStart();
        }
        else if (ide == R.id.print)
        {
            try {
                createPdf();
                printPDF();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                boolean cameraAccepted =grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean storageAccepted =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && storageAccepted){
                    checkCameraPermission();
                }
                requestCameraPermission();
            }
            break;
            case STORAGE_REQUEST_CODE:{
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        openCam();
        adapterRecord = new AdapterTwo(d.getFav(DB.id), CameraOpenActivity.this);
        adapterRecord.updateItems(d.getFav(DB.id));
        recyclerview.setLayoutManager(new LinearLayoutManager(CameraOpenActivity.this));
        recyclerview.hasFixedSize();
        recyclerview.setAdapter(adapterRecord);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCam();
        getPermission();

    }
    private void getPermission()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED){
                requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
            }
        }else{
            checkSelfPermission(Manifest.permission.CAMERA);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void createPdf() throws IOException {
        ArrayList<Model> arrayList = d.getFav(DB.code);

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400,
                600,numberPage)
                .create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        File file = new File(Environment.getExternalStorageDirectory(),"/First.pdf");
        paint.setTextSize(12);
        canvas.drawText("By Developer Ah3iq",140,12,paint);
        canvas.drawText(DB.name,20,30,paint);
        canvas.drawText("الكمية",235,30,paint);
        canvas.drawText(DB.sell,pageInfo.getPageWidth() - 60,30,paint);
         int StartY = 60;
         byte num =1;
        for (int i = 0; i < arrayList.size(); i++)
        {
            canvas.drawText(String.valueOf(num),5,StartY,paint);
            canvas.drawText(arrayList.get(i).getName(),22,StartY,paint);
            canvas.drawText(arrayList.get(i).getQuantity(),235,StartY,paint);
            canvas.drawText(arrayList.get(i).getSell(),pageInfo.getPageWidth() - 75,StartY,paint);
            canvas.drawLine(10,StartY+4,pageInfo.getPageWidth() - 10,StartY+4,paint);
            StartY +=20;
            num ++;
            if (num == 30){
                numberPage +=1;
                num =0;
            }
        }
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(16f);
        canvas.drawText("Total Price: "+results,180,pageInfo.getPageHeight()-12,paint);
        pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
    }
    private void sendData()
    {
        File fileWithinMyDir = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath()+"/First.pdf");
        if(fileWithinMyDir.exists()) {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileWithinMyDir));
            startActivity(Intent.createChooser(intentShareFile,"Share File pdf"));
        }
        else {
            Toast.makeText(this,
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/not found",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void printPDF (){
        PrintManager printManager=(PrintManager) getSystemService(Context.PRINT_SERVICE);
        try
        {
            PrintDocumentAdapter printAdapter = new
                    PdfDocumentAdapter(this,
                    Environment.getExternalStorageDirectory().getAbsolutePath()+"/First.pdf");
            printManager.print("Document", printAdapter,new PrintAttributes.Builder().build());
        }  catch (Exception e)
        {
            Log.d("PDF" , e.getMessage());
        }
    }
}