package com.Ahmed.PharmacistAssistant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public DB(Context context) {
        super(context, Dbname,null, 2);
        SQLiteDatabase db =this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( " create table " + DB_TABLE +
                "(name TEXT NOT NULL," + "sell TEXT NOT NULL,code TEXT NOT NULL,cost TEXT,id INTEGER PRIMARY KEY,quantity TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(sqLiteDatabase);
    }
    public boolean add(Model model) {
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
    public ArrayList<Model> getFav(String Fid)
    {
        ArrayList<Model> reFavArray = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + Fid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst())
        {
            do {
                Model model = new Model(
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
    public boolean updateData(Model model,String i)
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
}
