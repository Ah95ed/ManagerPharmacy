package com.Ahmed.PharmacistAssistant;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class RecordDetailActivity extends AppCompatActivity {
    private TextView tv_name,tv_code,tv_cost,tv_sell,tv_dose,tv_drug,tv_most,tv_mechanism,tv_pregnancy;
    DBSqlite db;
    private String ID,nameItem,code,cost,sell,dose,drug,most,mechanism,pregnancy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        db = new DBSqlite(this);
        tv_name = findViewById(R.id.name);
        tv_code = findViewById(R.id.code);
        tv_cost = findViewById(R.id.cost);
        tv_sell = findViewById(R.id.sell);
        tv_dose = findViewById(R.id.dose);
        tv_drug = findViewById(R.id.drug);
        tv_most = findViewById(R.id.most);
        tv_mechanism = findViewById(R.id.mechanism);
        tv_pregnancy = findViewById(R.id.Pregnancy);
        showRecordDetails();
    }
    private void showRecordDetails() {
        String selectQuery = "SELECT * FROM " +DBSqlite.DB_TABLE + " WHERE " +
                DBSqlite.C_ID + " =\""+ ID +"\"";
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                 nameItem = ""+cursor.getString(0);
                 code = ""+cursor.getString(1);
                 cost =""+cursor.getString(2);
                 sell = ""+cursor.getString(3);
                 dose = ""+cursor.getString(5);
                 drug = ""+cursor.getString(6);
                 most = ""+cursor.getString(7);
                 mechanism = ""+cursor.getString(8);
                 pregnancy = ""+cursor.getString(9);
                tv_name.setText(nameItem);
                tv_code.setText(code);
                tv_cost.setText(cost);
                tv_sell.setText(sell);
                tv_dose.setText(dose);
                tv_drug.setText(drug);
                tv_most.setText(most);
                tv_mechanism.setText(mechanism);
                tv_pregnancy.setText(pregnancy);

            }while (cursor.moveToNext());
        }
        database.close();
    }
}