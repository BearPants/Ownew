package com.example.kang_won.widgetex;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetEx extends AppWidgetProvider {

    private static DBManager dbManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            Log.d("RECEIVE!!!!!!!!!!!!!!!!", "android.appwidget.action.APPWIDGET_UPDATE");

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
        } else if (action.equals("android.appwidget.action.APPWIDGET_DELETED")) {
            Log.d("DELETE!!!!!!!!!!!!!!!!", "android.appwidget.action.APPWIDGET_DELETE");

            Bundle extras = intent.getExtras();
            int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

            Log.d("WIDGET _DELETE!!!", appWidgetId + "");

            dbManager = new DBManager(context);

            int recordCount = dbManager.getRecordCountAtBookmark(appWidgetId);

            if (recordCount != 0) {
                Log.d("!!!!!!!!!!!DELETE", "" + recordCount);
                dbManager.deleteData(appWidgetId);
            }
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

        for (int i = 0; i < appWidgetIds.length; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent intent = new Intent(context, SetActivity.class);
        intent.putExtra("WidgetID", appWidgetId);
        Intent webViewIntent;
        PendingIntent[] newsIntents = new PendingIntent[5];

        dbManager = new DBManager(context);

        int recordCount = dbManager.getRecordCountAtBookmark(appWidgetId);

        // URL 저장 여부 확인 후
        // URL 있으면 -> 브라우저
        // URL 없으면 -> 웹뷰

        if (recordCount == 0) {
            Log.d("!!!!!!!!!!!First", "" + recordCount);
            webViewIntent = new Intent(context, WebViewActivity.class);
            webViewIntent.putExtra("WidgetID", appWidgetId);
            Log.d("WidgetID!!!!!!!!", appWidgetId + "");
        } else {
            Log.d("!!!!!!!!!!!NotFirst", "fdfsfdsfdsfsdf");
            String url = dbManager.selectDataAtBookmark(appWidgetId);
            webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        PendingIntent webViewPendingIntent = PendingIntent.getActivity(context, appWidgetId, webViewIntent, 0);

        // remoteViews.setOnClickPendingIntent(R.id.widgetLayout, webViewPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.settingButton, pendingIntent);

        /**********새로 추가 각 textview에 설정**********/
        for (int i = 0; i < 5; i++) {
            //url 에 각 textView에 설정된 url 할당하기
            String url = "http://www.naver.com";
            Intent tempIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            tempIntent.putExtra("WidgetID", appWidgetId);
            newsIntents[i] = PendingIntent.getActivity(context, appWidgetId, tempIntent, 0);
        }
        remoteViews.setOnClickPendingIntent(R.id.news1, newsIntents[0]);
        remoteViews.setOnClickPendingIntent(R.id.news2, newsIntents[1]);
        remoteViews.setOnClickPendingIntent(R.id.news3, newsIntents[2]);
        remoteViews.setOnClickPendingIntent(R.id.news4, newsIntents[3]);
        remoteViews.setOnClickPendingIntent(R.id.news5, newsIntents[4]);
/***********************************************************************/
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
