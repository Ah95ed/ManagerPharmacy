package com.Ahmed.PharmacistAssistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import androidx.annotation.Nullable;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DBSqlite extends SQLiteAssetHelper {
    public static final String DB_NAME = "pharmacy.db";
    public static final byte VERSION = 1;
    public static final String DB_TABLE = "Ahmed";
    public static final String C_ID = "ID";
    public static final String C_NAME = "Name";
    public static final String C_CODE = "Barcode";
    public static final String C_COST = "Cost";
    public static final String C_PRICE = "Sell";
    public static final String C_DOSE = "dose";
    public static final String C_DRUG = "drugName";
    public static final String C_MOST_SIDE = "mostSide";
    public static final String C_MECHANISM = "mechanism";
    public static final String C_PREGNANCY = "pregnancy";
    private SQLiteDatabase db;
    public DBSqlite(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }

    /*______________أستعادة الداتا من ملف csv______________*/
    public long importData(String name, String code, String cost, String price){
//    ,String dose,String drug,String mostSideEffect,String mechanismOfAction,String pregnancy){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_NAME,name);
        cv.put(C_CODE,code);
        cv.put(C_COST,cost);
        cv.put(C_PRICE,price);

//        cv.put(C_DOSE,dose);
//        cv.put(C_DRUG,drug);
//        cv.put(C_MOST_SIDE,mostSideEffect);
//        cv.put(C_MECHANISM,mechanismOfAction);
//        cv.put(C_PREGNANCY,pregnancy);
        long result = db.insert(DB_TABLE,null,cv);
        db.close();
        return result;
    }
    /*______________أدخال البيانات الى الداتا بيز______________*/
    public long insertData(String name,String code,String cost,String price,String dose,String drug,
                           String mostSideEffect,String mechanismOfAction,String pregnancy){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_NAME,name);
        cv.put(C_CODE,code);
        cv.put(C_COST,cost);
        cv.put(C_PRICE,price);
        cv.put(C_DOSE,dose);
        cv.put(C_DRUG,drug);
        cv.put(C_MOST_SIDE,mostSideEffect);
        cv.put(C_MECHANISM,mechanismOfAction);
        cv.put(C_PREGNANCY,pregnancy);
        long result = db.insert(DB_TABLE,null,cv);
        db.close();
        return result;
    }
    /*______________ تـحديث  البيانات______________*/

    public void updateData(String name,String code,String cost,String sell,String ID,String dose,String drug,
                           String mostSideEffect,String mechanismOfAction,String pregnancy){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_NAME,name);
        cv.put(C_CODE,code);
        cv.put(C_COST,cost);
        cv.put(C_PRICE,sell);
        cv.put(C_DOSE,dose);
        cv.put(C_DRUG,drug);
        cv.put(C_MOST_SIDE,mostSideEffect);
        cv.put(C_MECHANISM,mechanismOfAction);
        cv.put(C_PREGNANCY,pregnancy);
        db.update(DB_TABLE,cv, C_ID + " =?",new String[]{ID});
        db.close();
    }
    public Cursor getAllNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {C_NAME, C_CODE, C_COST, C_PRICE, C_ID,C_DOSE,C_DRUG,C_MOST_SIDE,C_MECHANISM,C_PREGNANCY};
        return db.query(DB_TABLE, columns, null, null, null, null, null);

    }
    public boolean updateCostInDatabase (String cost){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DB_TABLE + " SET " + C_COST + " = " + C_COST + cost);
        db.close();
        return true;
}
    public boolean updateSellInDatabase (String sell){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DB_TABLE + " SET " + C_PRICE + " = " + C_PRICE + sell);
        db.close();
        return true;
    }
    /*___________ جلب كل البيانات______________*/
    public ArrayList<Model> getAllRecords(String orderBy){
        ArrayList<Model> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + orderBy;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                Model model = new Model(
                        ""+cursor.getString(0),
                        ""+cursor.getString(1),
                        ""+cursor.getString(2),
                        ""+cursor.getString(3),
                        ""+cursor.getString(4),
                        ""+cursor.getString(5),
                        ""+cursor.getString(6),
                        ""+cursor.getString(7),
                        ""+cursor.getString(8),
                        ""+cursor.getString(9));
                records.add(model);
            }while (cursor.moveToNext());
        }
        db.close();
        return records;
    }
    /*______________البحث عن طريق الاسم______________*/
    public ArrayList<Model> Search(String query){
        ArrayList<Model> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + C_NAME + " LIKE '%" + query + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                Model model = new Model(
                        ""+cursor.getString(0),
                        ""+cursor.getString(1),
                        ""+cursor.getString(2),
                        ""+cursor.getString(3),
                        ""+cursor.getString(4),
                        ""+cursor.getString(5),
                        ""+cursor.getString(6),
                        ""+cursor.getString(7),
                        ""+cursor.getString(8),
                        ""+cursor.getString(9)
                );
                records.add(model);
            }while (cursor.moveToNext());
        }
        db.close();
        return records;
    }
    /*______________العدد الكلي في الداتا ______________*/
    public int getAllCounts(){
        String countQuery = "SELECT * FROM "+ DB_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        return count;
    }
    /*______________ حذف row واحد فقط  ______________*/
    public void deletedItem(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(DB_TABLE,C_ID + " =?",new String[]{id});
        db.close();
    }
    /*______________ حذف الكل ______________**/
    public void deletedAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE);
        db.close();
    }
    /*______________ البحث عن طريق الباركود ______________*/
    public ArrayList<Model> searchCamera(String query){
        ArrayList<Model> recordList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + C_CODE + " LIKE '%" + query + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Model model = new Model(
                        "" + cursor.getString(0),
                        "" + cursor.getString(1),
                        "" + cursor.getString(2),
                        "" + cursor.getString(3),
                        "" + cursor.getString(4),
                        "" + cursor.getString(5),
                        "" + cursor.getString(6),
                        "" + cursor.getString(7),
                        "" + cursor.getString(8),
                        "" + cursor.getString(9));
                recordList.add(model);
            }while (cursor.moveToNext());

            cursor.moveToNext();
        }
        db.close();
        return recordList;
    }
    public ArrayList<Model> SearchAdd(String query){
        ArrayList<Model> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE + " WHERE " + C_NAME + " LIKE '%" + query + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
                Model model = new Model(
                        ""+cursor.getString(0),
                        ""+cursor.getString(1),
                        ""+cursor.getString(2),
                        ""+cursor.getString(3),
                        ""+cursor.getString(4),
                        ""+cursor.getString(5),
                        ""+cursor.getString(6),
                        ""+cursor.getString(7),
                        ""+cursor.getString(8),
                        ""+cursor.getString(9)
                );
                records.add(model);
          cursor.moveToNext();
        }
        db.close();
        return records;
    }

}
