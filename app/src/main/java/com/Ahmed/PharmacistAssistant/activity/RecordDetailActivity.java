package com.Ahmed.PharmacistAssistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DBSqlite;

public class RecordDetailActivity extends AppCompatActivity {
    private TextView tv_name,tv_code,tv_cost,tv_sell;
    DBSqlite db;
    private String ID,nameItem,code,cost,sell;
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
                    tv_name.setText(nameItem);
                    tv_code.setText(code);
                    tv_cost.setText(cost);
                    tv_sell.setText(sell);

            }while (cursor.moveToNext());
        }
        database.close();
    }
}