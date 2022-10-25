package com.Ahmed.PharmacistAssistant.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.Ahmed.PharmacistAssistant.model.Debts;
import com.Ahmed.PharmacistAssistant.model.Favorite;
import com.Ahmed.PharmacistAssistant.model.Model;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    public static final String Dbname="Fav.db";
    public static final String name="name";
    public static final String sell="sell";
    public static final String cost="cost";
    public static final String code="code";
    public static final String id="id";
    public static final String quantity="quantity";
    public static final String DB_TABLE = "Favorite";
    public static final String TB_DEBT = "DEBTS";
    public static final String C_ID = "DEBTS";
    public static final String C = "DEBTS";
//    public static final String TB_DEBT = "DEBTS";
//    public static final String TB_DEBT = "DEBTS";
//    public static final String TB_DEBT = "DEBTS";
//    public static final String TB_DEBT = "DEBTS";
//    public static final String TB_DEBT = "DEBTS";
    public DB(Context context) {
        super(context, Dbname,null, 2);
        SQLiteDatabase db =this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( " create table " + DB_TABLE +
                "(name TEXT NOT NULL," +
                "sell TEXT NOT NULL,code TEXT NOT NULL," +
                "cost TEXT,id INTEGER PRIMARY KEY,quantity TEXT NOT NULL )");


        sqLiteDatabase.execSQL( " create table " + TB_DEBT +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT ,number TEXT,amount TEXT ,BEBTTIME TEXT,desicribtion)" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + DB_TABLE);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TB_DEBT);
        onCreate(sqLiteDatabase);
    }
    public boolean add(Favorite model) {
        ContentValues cv = new ContentValues();
        cv.put(name, model.getName());
        cv.put(code, model.getCode());
        cv.put(cost, model.getCost());
        cv.put(sell, model.getSell());
        cv.put(quantity, model.getQuantity());
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(DB_TABLE, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<Favorite> getFav(String Fid)
    {
        ArrayList<Favorite> reFavArray = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + Fid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst())
        {
            do {
                Favorite model = new Favorite(
                        ""+cursor.getString(0),
                        ""+cursor.getString(1),
                        ""+cursor.getString(2),
                        ""+cursor.getString(3),
                        ""+cursor.getString(4),
                        ""+cursor.getString(5)
                );
                reFavArray.add(model);
                }while (cursor.moveToNext());
        }
        db.close();
        return reFavArray;
    }
    public void deletedList(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE);
        db.close();
    }
    public int deleteList(int n) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_TABLE, id + " = " + n, null);
    }
    public boolean updateData(Favorite model, String i)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(name,model.getName());
        cv.put(code,model.getCode());
        cv.put(cost,model.getCost());
        cv.put(sell,model.getSell());
        cv.put(quantity,model.getQuantity());
       int result = db.update(DB_TABLE,cv,id +" =?",new String[]{i});
       if (result != -1)
           return true;
       else
           return false;
    }
    private long insertDEBT(Debts debts){
        ContentValues cv = new ContentValues();
        cv.put(name, debts.getName());
        cv.put(code, debts.getAmount());
        cv.put(cost, debts.getTime());
        cv.put(sell, debts.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.insert(DB_TABLE, null, cv);
    }
}
