package com.Ahmed.PharmacistAssistant.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Ahmed.PharmacistAssistant.R;
import com.Ahmed.PharmacistAssistant.model.Model;

import java.util.ArrayList;

public class AdapterSearchDialog extends RecyclerView.Adapter<AdapterSearchDialog.AdapterSearch> {

    private ArrayList<Model> arrayList;
    private Context context;
    @SuppressLint("NotifyDataSetChanged")
    public AdapterSearchDialog (ArrayList<Model> arrayList , Context context)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AdapterSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_result_search,parent,false);
        return new AdapterSearch(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearch holder, int position) {

        Model m = arrayList.get(position);
        String name = m.getName();
//        holder.tv_name.setText("name");
        System.out.println(name);
        holder.getAdapterPosition();

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class AdapterSearch extends RecyclerView.ViewHolder{
        private TextView tv_name ;
        public AdapterSearch(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.search_name);
        }
    }
}
