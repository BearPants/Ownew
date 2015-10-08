package com.example.kang_won.widgetex;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 서강원 on 2015-10-05.
 */
public class DBManager {

    private static final String dbName = "Ownew.db";
    private static final String tableName = "Ownew";
    public static final int dbVersion = 1;

    private OpenHelper opener;
    private SQLiteDatabase db;

    private Context context;

    public DBManager(Context context){
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();
    }

    class OpenHelper extends SQLiteOpenHelper{

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, null, version);
        }

        public void onCreate(SQLiteDatabase arg0){
            String createSQL = "CREATE TABLE " + tableName + " ("
                    + "id integer primary key autoincrement, " + "url text)";

            arg0.execSQL(createSQL);
        }

        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

        }
    }

    public void insertData(int id, String url){
        String insertQuery = "INSERT INTO " + tableName + " VALUES(" + id + ", '"
                + url + "')";

        db.execSQL(insertQuery);
    }

    public void updateData(int id, String url){
        String updateQuery = "UPDATE " + tableName + " SET url='" + url + "' WHERE id=" + id + ";";
        db.execSQL(updateQuery);
    }

    public void deleteData(int id){
        String deleteQuery = "DELETE FROM " + tableName + "WHERE id=" + id + ";";
        db.execSQL(deleteQuery);
    }

    public String selectData(int id){
        String selectQuery = "SELECT * FROM " + tableName + " WHERE id=" + id + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String resultURL;

        if(cursor.moveToFirst()){
            resultURL = cursor.getString(cursor.getColumnIndex("url"));
            cursor.close();
            return resultURL;
        }

        cursor.close();
        return null;
    }

    public int getRecordCount(int id){
        String selectQuery = "SELECT * FROM " + tableName + " WHERE id=" + id + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public void close(){
        db.close();
    }
}
