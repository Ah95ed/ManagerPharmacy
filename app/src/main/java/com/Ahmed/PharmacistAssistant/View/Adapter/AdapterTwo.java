package com.Ahmed.PharmacistAssistant.View.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.Ahmed.PharmacistAssistant.View.Activity.CameraOpenActivity;
import com.Ahmed.PharmacistAssistant.Controler.database.DB;
import com.Ahmed.PharmacistAssistant.model.Favorite;
import com.Ahmed.PharmacistAssistant.R;

import java.util.ArrayList;

public class AdapterTwo extends RecyclerView.Adapter<AdapterTwo.HolderTwo> {
    private ArrayList<Favorite> array;
    private Context context;
    private DB db;
    @SuppressLint("StaticFieldLeak")
    public TextView textView;
    public  String result;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public AdapterTwo(ArrayList<Favorite> array, Context context) {
        this.array = array;
        this.context = context;
        db = new DB(context);
    }

    /**
     * Notion
     *   @SuppressLint("NotifyDataSetChanged")
     *     public void updateItems(ArrayList<Model> newList) {
     *         array = newList;
     *         notifyDataSetChanged();
     *         this.notifyDataSetChanged();
     *     }
     * the method updateItem to refresh recyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(ArrayList<Favorite> newList) {
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
        Favorite model = array.get(position);
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
        holder.more.setOnClickListener(view -> moreSelected(position,name,sell,cost,id,code,quantity));
    }
    @SuppressLint("NotifyDataSetChanged")
    private void moreSelected(int position, String n, String sell, String cost,String id,String code,String quantity) {
        String[] options = {"حذف", "تعديل"};
        AlertDialog.Builder builderParent = new AlertDialog.Builder(context);
        builderParent.setItems(options, new DialogInterface.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    db = new DB(context);
                    String getSell = sellDeleted(id);
                    double minSell =  Double.parseDouble(getSell);
                    textView = ((CameraOpenActivity)context).result;
                    if (textView.getText().toString().equals("المجموع")){
                         db.deleteList(Integer.parseInt(id));
//                         deleteShared();

                        ((CameraOpenActivity) context).onStart();
                        return;
                    }else {
                        double resultDeleted = Double.parseDouble(textView.getText().toString()) - minSell;
                        textView.setText(String.valueOf(resultDeleted));
                        ((CameraOpenActivity) context).onStart();
                    }
                    int delete = db.deleteList(Integer.parseInt(id));
                    array.remove(array.get(position));
                    AdapterTwo.this.notifyDataSetChanged();
                    if (delete > 0) {

                        db.close();
                        ((CameraOpenActivity) context).onStart();
                        Toast.makeText(context, " تم حذف " + n, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else if (i == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.custom_layout_dialog, null, false);
                    EditText nametext = v.findViewById(R.id.name);
                    EditText costtext = v.findViewById(R.id.cost);
                    EditText sellText = v.findViewById(R.id.sell);
                    EditText quantities = v.findViewById(R.id.quantity);
                    EditText theResult = v.findViewById(R.id.results);
                    Button update = v.findViewById(R.id.update);
                    Button cancel = v.findViewById(R.id.cancel);
//                    String getResult = textView.getText().toString();
                    builder.setView(v);
//                    builder.setView(view);
                    textView = ((CameraOpenActivity)context).result;
                    nametext.setText(n);
                    costtext.setText(cost);
                    sellText.setText(sell);
                    theResult.setText(textView.getText().toString());
                    quantities.setText(quantity);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String nameE = nametext.getText().toString();
                            String costE = costtext.getText().toString();
                            String sellE = sellText.getText().toString();
                            String quantityE = quantities.getText().toString();
                            Favorite model = new Favorite(nameE, costE, sellE, id, code, quantityE);
                            result = theResult.getText().toString();
                            boolean result = db.updateData(model, id);
                            if (result) {
                                Toast.makeText(context, "تم التعديل", Toast.LENGTH_SHORT).show();
                                updateItems(db.getFav(id));
                                /**
                                 *
                                 * أخذ القيمة من الاكتفتي
                                 * وارجاعها بعد التعديل
                                 *
                                 * */
                                textView.setText(theResult.getText().toString());
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

    private void deleteShared() {
        preferences =context.getSharedPreferences("My AllPrice",MODE_PRIVATE);
        editor = preferences.edit();
        if (preferences.contains("AllPrice")){
            String x = preferences.getString("AllPrice","");
            if (x.isEmpty() || x.equals("المجموع"))
                return;
            }
        editor.clear();
        editor.remove("AllPrice");
        editor.commit();
    }

    private String sellDeleted(String id) {
        String getSell = null;
        String selectQuery = "SELECT * FROM " + DB.DB_TABLE + " WHERE " +
                DB.id + " =\""+ id +"\"";
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                getSell = ""+cursor.getString(3);
            }while (cursor.moveToNext());
        }
        database.close();
        return getSell;
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
