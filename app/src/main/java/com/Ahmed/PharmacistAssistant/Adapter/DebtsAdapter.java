package com.Ahmed.PharmacistAssistant.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.model.Debts;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.Holders> {
    private ArrayList<Debts> debts;
    private Context context;
    @SuppressLint("NotifyDataSetChanged")
    public DebtsAdapter (ArrayList<Debts> debts, Context context){
        this.context = context;
        this.debts = debts;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_debts,parent,false);
        return new Holders(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holders holder, int position) {

        Debts debt = debts.get(position);


    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    public class Holders extends RecyclerView.ViewHolder {

        private MaterialTextView tv_name , tv_debts;

        public Holders(@NonNull View itemView) {
            super(itemView);
            tv_name =itemView.findViewById(R.id.name);
            tv_debts = itemView.findViewById(R.id.debt);
        }
    }
}
