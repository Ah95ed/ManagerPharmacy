package com.Ahmed.PharmacistAssistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTwo extends RecyclerView.Adapter<AdapterTwo.HolderTwo> {
    private ArrayList<Model> array;
    private Context context;
    private DB db;

    public AdapterTwo(ArrayList<Model> array, Context context) {
        this.array = array;
        this.context = context;
        db = new DB(context);
    }

    /**
     *   @SuppressLint("NotifyDataSetChanged")
     *     public void updateItems(ArrayList<Model> newList) {
     *         array = newList;
     *         notifyDataSetChanged();
     *         this.notifyDataSetChanged();
     *     }
     * the method updateItem to refresh recyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(ArrayList<Model> newList) {
        array = newList;
        notifyDataSetChanged();
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public HolderTwo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler,parent,false);
        return new HolderTwo(v);
    }
    @Override
    public void onBindViewHolder(@NonNull HolderTwo holder, @SuppressLint("RecyclerView") int position) {
        Model model = array.get(position);
        String name = model.getName();
        String sell = model.getSell();
        String cost = model.getCost();
        String code = model.getCode();
        String quantity = model.getQuantity();
        String id = model.getId();
        holder.nameTv.setText(name);
        holder.sellTv.setText(sell);
        holder.tv_cost.setText(cost);
        holder.getAdapterPosition();
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreSelected(position,name,sell,cost,id,code,quantity);
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void moreSelected(int position, String n, String sell, String cost,String id,String code,String quantity) {
        String[] options = {"حذف", "تعديل"};
        AlertDialog.Builder builderParent = new AlertDialog.Builder(context);
        builderParent.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    db = new DB(context);
                    int delete = db.deleteList(Integer.parseInt(id));
                    array.remove(array.get(position));
                    AdapterTwo.this.notifyDataSetChanged();
                    if (delete > 0) {
                        db.close();
                        ((CameraOpenActivity) context).onStart();
                        Toast.makeText(context, "تم الحذف" + n, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else if (i == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.custom_layout_dialog, null, false);
                    EditText nametext = v.findViewById(R.id.name);
                    EditText costtext = v.findViewById(R.id.cost);
                    EditText selltext = v.findViewById(R.id.sell);
                    EditText quantitytext = v.findViewById(R.id.quantity);
                    Button update = v.findViewById(R.id.update);
                    Button cancel = v.findViewById(R.id.cancel);
                    builder.setView(v);
                    nametext.setText(n);
                    costtext.setText(cost);
                    selltext.setText(sell);
                    quantitytext.setText(quantity);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String nameE = nametext.getText().toString();
                            String costE = costtext.getText().toString();
                            String sellE = selltext.getText().toString();
                            String quantityE = quantitytext.getText().toString();
                            Model model = new Model(nameE, costE, sellE, id, code, quantityE);

                            boolean result = db.updateData(model, id);
                            if (result) {
                                Toast.makeText(context, "تم التعديل", Toast.LENGTH_SHORT).show();
                                ((CameraOpenActivity) context).onStart();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "فشل التعديل", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        }).create().show();
    }
    @Override
    public int getItemCount() {
        return array.size();
    }
    class HolderTwo extends RecyclerView.ViewHolder {
        TextView nameTv,sellTv,tv_cost;
        ImageButton more;
        public HolderTwo(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_name);
            sellTv = itemView.findViewById(R.id.tv_sell);
            tv_cost = itemView.findViewById(R.id.tv_cost);
            more = itemView.findViewById(R.id.moreBtn);
        }
    }
}
