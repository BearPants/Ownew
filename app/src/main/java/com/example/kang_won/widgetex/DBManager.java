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
    private static final String tableName = "Bookmark";
    private static final String tableRSS = "RSS";
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
            String createBookmarkSQL = "CREATE TABLE " + tableName + " ("
                    + "widgetId integer primary key autoincrement, " + "url text)";

            String createRssSQL = "CREATE TABLE " + tableRSS + " ("
                    + "widgetId integer primary key autoincrement, " + " RSS_feed text, RSS_name text)";

            arg0.execSQL(createBookmarkSQL);
            arg0.execSQL(createRssSQL);
        }

        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

        }
    }

    public void insertDataAtRSS(int widgetId, String RSSFeed, String RSSName){
        String insertQuery = "INSERT INTO " + tableRSS + " VALUES(" + widgetId + ", '"
                + RSSFeed + "', '" + RSSName + "')";

        db.execSQL(insertQuery);
    }

    public void insertDataAtBookmark(int widgetId, String url){
        String insertQuery = "INSERT INTO " + tableName + " VALUES(" + widgetId + ", '"
                + url + "')";

        db.execSQL(insertQuery);
    }

    public void updateData(int widgetId, String url){
        String updateQuery = "UPDATE " + tableName + " SET url='" + url + "' WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }

    public void updateDataAtRSS(int widgetId, String RSSFeed, String RSSName){
        String updateQuery = "UPDATE " + tableRSS + " SET RSS_feed='" + RSSFeed + "',RSS_name='" + RSSName + "' WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }


    public void deleteData(int widgetId){
        String deleteQuery = "DELETE FROM " + tableName + " WHERE widgetId=" + widgetId + ";";
        db.execSQL(deleteQuery);
    }

    public String selectDataAtRSS(int widgetId, String targetColumnName){
        String selectQuery = "SELECT * FROM " + tableRSS + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        String resultURL;

        if(cursor.moveToFirst()){

            switch(targetColumnName){
                case "RSS_feed":
                    resultURL = cursor.getString(cursor.getColumnIndex("RSS_feed"));
                    return resultURL;
                case "RSS_name":
                    resultURL = cursor.getString(cursor.getColumnIndex("RSS_name"));
                    return resultURL;
            }

            cursor.close();
        }

        cursor.close();
        return null;
    }

    public String selectDataAtBookmark(int widgetId){
        String selectQuery = "SELECT * FROM " + tableName + " WHERE widgetId=" + widgetId + ";";
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

    public int getRecordCountAtBookmark(int widgetId){
        String selectQuery = "SELECT * FROM " + tableName + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public int getRecordCountAtRSS(int widgetId){
        String selectQuery = "SELECT * FROM " + tableRSS + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public void close(){
        db.close();
    }
}
