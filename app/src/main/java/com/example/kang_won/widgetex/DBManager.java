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
    private static final String tableMyfeed = "Myfeed";
    private static final String tableRSS = "RSS";
    private static final String tableRSSItem = "RSSItem";
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
                    + "widgetId integer primary key, url text)";

            String createMyfeedSQL = "CREATE TABLE " + tableMyfeed + " ("
                    + "num integer primary key, widgetId integer, RSS_feed text, RSS_name text)";

            String createRSSItemSQL = "CREATE TABLE " + tableRSSItem + " ("
                    + "num integer primary key, widgetId integer, itemURL text)";

            arg0.execSQL(createBookmarkSQL);
            arg0.execSQL(createMyfeedSQL);
            arg0.execSQL(createRSSItemSQL);
        }

        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

        }
    }

    public void insertDataAtRSSItem(int widgetId, String itemURL){
        String insertQuery = "INSERT INTO " + tableRSSItem + "(widgetId, itemURL) VALUES(" + widgetId + ", '"
                + itemURL + "')";

        db.execSQL(insertQuery);
    }

    public void insertDataAtMyfeed(int widgetId, String RSSFeed, String RSSName){
        String insertQuery = "INSERT INTO " + tableMyfeed + "(widgetId, RSS_feed, RSS_name) VALUES(" + widgetId + ", '"
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

    public void updateDataAtMyfeed(int widgetId, String RSSFeed, String RSSName){
        String updateQuery = "UPDATE " + tableMyfeed + " SET RSS_feed='" + RSSFeed + "',RSS_name='" + RSSName + "' WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }


    public void deleteData(int widgetId){
        String deleteQuery = "DELETE FROM " + tableName + " WHERE widgetId=" + widgetId + ";";
        db.execSQL(deleteQuery);
    }

    public String[] selectDataByWidgetIdAtRSSItem(int widgetId, String targetColumnName){
        String selectQuery = "SELECT * FROM " + tableRSSItem + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        int i = 0 ;

        String[] result = new String[count];

        if(cursor != null){

            while(cursor.moveToNext()){

                if(targetColumnName.equals("itemURL")){
                    result[i++] = cursor.getString(cursor.getColumnIndex("itemURL"));
                }
            }
        }
        else {
            return null;
        }

        cursor.close();

        return result;
    }

    public String[] selectDataByWidgetIdAtMyfeed(int widgetId, String targetColumnName){
        String selectQuery = "SELECT * FROM " + tableMyfeed + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        int i = 0 ;

        String[] result = new String[count];

        if(cursor != null){
            while(cursor.moveToNext()){

                if(targetColumnName.equals("RSS_feed")){
                    result[i++] = cursor.getString(cursor.getColumnIndex("RSS_feed"));
                }
                else if(targetColumnName.equals("RSS_name")) {
                    result[i++] = cursor.getString(cursor.getColumnIndex("RSS_name"));
                }
            }
        }
        else {
            return null;
        }

        cursor.close();
        return result;
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

    public int getRecordCountAtMyfeed(int widgetId){
        String selectQuery = "SELECT * FROM " + tableMyfeed + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public void close(){
        db.close();
    }
}