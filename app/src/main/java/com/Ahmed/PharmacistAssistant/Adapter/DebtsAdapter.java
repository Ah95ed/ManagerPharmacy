package com.Ahmed.PharmacistAssistant.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.Ui.DebtsActivity;
import com.Ahmed.PharmacistAssistant.database.DB;
import com.Ahmed.PharmacistAssistant.model.Debts;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.Holders> {
    private ArrayList<Debts> debts;
    private Context context;
    private DB db;
    @SuppressLint("NotifyDataSetChanged")
    public DebtsAdapter (ArrayList<Debts> debts, Context context){
        this.context = context;
        this.debts = debts;
        this.notifyDataSetChanged();
        db = new DB(context);
    }
    @NonNull
    @Override
    public Holders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.card_debts,parent,false);
        return new Holders(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holders holder, int position) {

        Debts debt = debts.get(position);
        String name  = debt.getName();
        String debtsCoin = debt.getAmount();
        holder.tv_name.setText(name);
        holder.tv_debts.setText(debtsCoin);
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(debt.getId());
            }
        });
        holder.getAdapterPosition();
    }

    private void showDialog(String id) {

        String[] option = {"حذف","تعديل"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        db.deletedItem(id);
                        ((DebtsActivity) context).onResume();
                        break;
                    case 1:

                        break;
                }
            }
        }).create().show();
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    public class Holders extends RecyclerView.ViewHolder {

        private MaterialTextView tv_name , tv_debts;
        private ImageButton moreBtn;

        public Holders(@NonNull View itemView) {
            super(itemView);
            tv_name =itemView.findViewById(R.id.name);
            tv_debts = itemView.findViewById(R.id.debt);
            moreBtn = itemView.findViewById(R.id.more);
        }
    }
}
