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
    private static final String tableBookmark = "Bookmark";
    private static final String tableMyfeed = "Myfeed";
    // private static final String tableRSS = "RSS";
    private static final String tableRSSItem = "RSSItem";
    private static final String tableWidgetState = "WidgetState";

    public static final int dbVersion = 1;

    private OpenHelper opener;
    private SQLiteDatabase db;

    private Context context;
    private String[] tableArray = {tableBookmark, tableMyfeed, tableRSSItem, tableWidgetState};

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
            String createBookmarkSQL = "CREATE TABLE " + tableBookmark + " ("
                    + "widgetId integer primary key, url text)";

            String createMyfeedSQL = "CREATE TABLE " + tableMyfeed + " ("
                    + "num integer primary key, widgetId integer, RSS_feed text, RSS_name text)";

            String createRSSItemSQL = "CREATE TABLE " + tableRSSItem + " ("
                    + "num integer, widgetId integer, itemURL text, primary key(num, widgetId))";

            String createWidgetState = "CREATE TABLE " + tableWidgetState + " ("
                    + "num integer primary key, widgetId integer, widgetState integer)";

            arg0.execSQL(createBookmarkSQL);
            arg0.execSQL(createMyfeedSQL);
            arg0.execSQL(createRSSItemSQL);
            arg0.execSQL(createWidgetState);
        }

        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

        }
    }

    public void insertDataAtRSSItem(int num, int widgetId, String itemURL){
        String insertQuery = "INSERT INTO " + tableRSSItem + "(num, widgetId, itemURL) VALUES("+ num + ", " + widgetId + ", '"
                + itemURL + "')";

        db.execSQL(insertQuery);
    }

    public void insertDataAtMyfeed(int widgetId, String RSSFeed, String RSSName){
        String insertQuery = "INSERT INTO " + tableMyfeed + "(widgetId, RSS_feed, RSS_name) VALUES(" + widgetId + ", '"
                + RSSFeed + "', '" + RSSName + "')";

        db.execSQL(insertQuery);
    }

    public void insertDataAtBookmark(int widgetId, String url){
        String insertQuery = "INSERT INTO " + tableBookmark + " VALUES(" + widgetId + ", '"
                + url + "')";

        db.execSQL(insertQuery);
    }

    public void insertDataAtWidgetState(int widgetId, int state){
        String insertQuery = "INSERT INTO " + tableWidgetState + "(widgetId, widgetState) VALUES(" + widgetId + ", "
                + state + ")";

        db.execSQL(insertQuery);
    }

    public void updateDataAtBookmark(int widgetId, String url){
        String updateQuery = "UPDATE " + tableBookmark + " SET url='" + url + "' WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }

    public void updateDataAtRSSItem(int num, int widgetId, String itemURL){
        String updateQuery = "UPDATE " + tableRSSItem + " SET itemURL='" + itemURL + "' WHERE num=" + num + " AND widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }

    public void updateDataAtMyfeed(int widgetId, String RSSFeed, String RSSName){
        String updateQuery = "UPDATE " + tableMyfeed + " SET RSS_feed='" + RSSFeed + "',RSS_name='" + RSSName + "' WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }

    public void updateDataAtWidgetState(int widgetId, int state){
        String updateQuery = "UPDATE " + tableWidgetState + " SET widgetState=" + state + " WHERE widgetId=" + widgetId + ";";
        db.execSQL(updateQuery);
    }

    public void deleteDataAtRSSItem(int num, int widgetId){
        String deleteQuery = "DELETE FROM " + tableRSSItem + " WHERE num=" + num + " AND widgetId=" + widgetId + ";";
        db.execSQL(deleteQuery);
    }

    public void deleteDataAtAllTable(int widgetId){

        for(int i=0;i<tableArray.length;i++){
            String deleteQuery = "DELETE FROM " + tableArray[i] + " WHERE widgetId=" + widgetId + ";";
            db.execSQL(deleteQuery);
        }
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
        String selectQuery = "SELECT * FROM " + tableBookmark + " WHERE widgetId=" + widgetId + ";";
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

    public int selectDataAtWidgetState(int widgetId){
        String selectQuery = "SELECT * FROM " + tableWidgetState + " WHERE widgetId=" + widgetId + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int state = -1;

        if(cursor.moveToFirst()){
            state = cursor.getInt(cursor.getColumnIndex("widgetState"));
            cursor.close();
            return state;
        }

        cursor.close();
        return state;
    }

    public int getRecordCountAtBookmark(int widgetId){
        String selectQuery = "SELECT * FROM " + tableBookmark + " WHERE widgetId=" + widgetId + ";";

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

    public int getRecordCountAtRSSItem(int widgetId){
        String selectQuery = "SELECT * FROM " + tableRSSItem + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public int getRecordCountAtWidgetState(int widgetId){
        String selectQuery = "SELECT * FROM " + tableWidgetState + " WHERE widgetId=" + widgetId + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int resultCount = cursor.getCount();

        cursor.close();

        return resultCount;
    }

    public void close(){
        db.close();
    }
}