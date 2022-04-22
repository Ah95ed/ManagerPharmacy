package com.Ahmed.PharmacistAssistant;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CalculateActivity extends AppCompatActivity {
    private EditText et_Dosage,et_weight,et_dose,et_per;
    private String Dosage,weight,dose,per;
    private TextView re_dosage,resultF;
    private FloatingActionButton Button;
    private double result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        et_Dosage = findViewById(R.id.dosage);
        Button = findViewById(R.id.calc_float);
        et_weight = findViewById(R.id.weight);
        et_dose = findViewById(R.id.dose);
        et_per = findViewById(R.id.per_volume);
        resultF = findViewById(R.id.resultFinal);
        re_dosage = findViewById(R.id.re_Dosage);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Calculate();
            }
        });
    }
    private void Calculate() {
        Dosage = "" + et_Dosage.getText().toString().trim();
        weight = "" + et_weight.getText().toString().trim();
        dose = "" + et_dose.getText().toString().trim();
        per = "" + et_per.getText().toString().trim();
        try { result =
                Double.parseDouble(Dosage)
                        * Double.parseDouble(weight);
            re_dosage.setText(String.valueOf(result));
            if (dose !="" && per != ""&& !per.isEmpty()){
                double result2 = Double.parseDouble(per)/Double.parseDouble(dose);
                double resultFinal = result2 * result;
                resultF.setText(String.valueOf(resultFinal));
            }
        }catch (Exception e){
            Log.d("CalculateActivity",e.getMessage());
        }

    }
}