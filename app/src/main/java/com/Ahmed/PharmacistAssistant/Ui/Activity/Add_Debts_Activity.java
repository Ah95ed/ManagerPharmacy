package com.Ahmed.PharmacistAssistant.Ui.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.MotionButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DB;
import com.Ahmed.PharmacistAssistant.model.Debts;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class Add_Debts_Activity extends AppCompatActivity {

    private MotionButton save;
    private TextInputEditText fullName,amount,time,desc;
    private DB db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debts);
        defineVar();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDebts();
            }
        });

    }

    private void defineVar() {
        save = findViewById(R.id.save);
        fullName = findViewById(R.id.fullName);
        amount = findViewById(R.id.amounts);
        time = findViewById(R.id.time);
        desc = findViewById(R.id.description);
        db = new DB(this);
    }

    private void saveDebts() {
        String name = Objects.requireNonNull(fullName.getText()).toString();
        String amount_ = amount.getText().toString();
        String time_ = time.getText().toString();
        String desc_ = desc.getText().toString();
        if (name.isEmpty() || amount_.isEmpty() ){
            Toast.makeText(this, "الحقول فارغة", Toast.LENGTH_SHORT).show();
        }else {
        boolean isInsert = db.insertDebt(new Debts(name,amount_,time_,desc_));
        if (isInsert)
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Field", Toast.LENGTH_SHORT).show();
      }
    }
}