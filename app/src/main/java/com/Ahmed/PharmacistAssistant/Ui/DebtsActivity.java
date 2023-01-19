package com.Ahmed.PharmacistAssistant.Ui;

import static com.Ahmed.PharmacistAssistant.database.DB.C_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Ahmed.PharmacistAssistant.Adapter.DebtsAdapter;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.database.DB;
import com.Ahmed.PharmacistAssistant.model.Debts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DebtsActivity extends AppCompatActivity {
    private FloatingActionButton addDebt;
    private RecyclerView recyclerView;
    private DebtsAdapter adapter ;
    private DB db;
    private ArrayList<Debts> debts;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debts);
        db = new DB(this);








        addDebt = findViewById(R.id.addDebt);
        addDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DebtsActivity.this,Add_Debts_Activity.class));
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        getAllData();
//        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getAllData();
//        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllData();
//        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }

    private void getAllData(){
        recyclerView =findViewById(R.id.debtRV);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        debts = db.getDebts(C_ID);
        adapter = new DebtsAdapter(debts,this);
        recyclerView.setAdapter(adapter);
    }


}