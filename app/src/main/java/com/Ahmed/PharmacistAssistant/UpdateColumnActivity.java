package com.Ahmed.PharmacistAssistant;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class UpdateColumnActivity extends AppCompatActivity {
    private Button upBtn,updateSell;
    private EditText textUpdate ,Et_update;
    private DBSqlite db;
    private String text,price;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_column);
        actionBar = getSupportActionBar();
        actionBar.setTitle("تحديث اسعار البيع والشراء");
        db = new DBSqlite(this);
        upBtn = findViewById(R.id.updateBtn);
        Et_update = findViewById(R.id.Et_update1);
        updateSell = findViewById(R.id.updateSell);
        textUpdate = findViewById(R.id.Et_update);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateAllRow();
            }
        });

        updateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSellMethod();
            }
        });

    }

    private void updateSellMethod() {
        price = Et_update.getText().toString();
        Cursor cursor = db.getAllNames();
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                cursor.moveToLast();
                cursor.getInt(3);
                boolean updatesell= db.updateSellInDatabase(price);
                if (updatesell)
                    Toast.makeText(this, "تم تحديث البيع", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "فشل", Toast.LENGTH_SHORT).show();
                cursor.moveToNext();
            }
        }
    }
    private void updateAllRow() {
        text =textUpdate.getText().toString();
        Cursor cursor = db.getAllNames();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    cursor.moveToLast();
                    cursor.getInt(2);
                    boolean isUpdate = db.updateCostInDatabase(text);
                    if (isUpdate == true) {
                        Toast.makeText(UpdateColumnActivity.this, "تمت زيادة الاسعار", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UpdateColumnActivity.this, "فشل زيادة الاسعار", Toast.LENGTH_LONG).show();
                    }
                    cursor.moveToNext();
                }
            }
      }
}