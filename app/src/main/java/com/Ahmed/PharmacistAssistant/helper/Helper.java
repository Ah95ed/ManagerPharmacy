package com.Ahmed.PharmacistAssistant.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import com.Ahmed.PharmacistAssistant.R;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class Helper {
    public static DecoratedBarcodeView barcodeView;
    public static CameraSettings cameraSettings;
    private static boolean isFlash = false ;
    private Context context;
    private EditText text;

    public Helper (Context context,EditText text)
    {
        this.context = context;
        this.text = text;
    }

    public void openCamera(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_qrcode,null,false);
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
                    text.setText(result.getText());
                    barcodeView.pause();
                    dialog.dismiss();
                    isFlash = false;
                }
            }
        });
        AppCompatButton Close = v.findViewById(R.id.close);
        AppCompatButton flash = v.findViewById(R.id.flash);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeView.pause();
                dialog.dismiss();
                isFlash =false;
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
