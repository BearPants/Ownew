package com.example.kang_won.widgetex;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        Intent webViewIntent;

        dbManager = new DBManager(context);

        int recordCount = dbManager.getRecordCount(1);

        // URL 저장 여부 확인 후
        // URL 있으면 -> 브라우저
        // URL 없으면 -> 웹뷰

        if (recordCount == 0) {
            Log.d("!!!!!!!!!!!First", "" + recordCount);
            webViewIntent = new Intent(context, WebViewActivity.class);
        } else {
            Log.d("!!!!!!!!!!!NotFirst", "fdfsfdsfdsfsdf");
            String url = dbManager.selectData(1);
            webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent webViewPendingIntent = PendingIntent.getActivity(context, 0, webViewIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.widgetLayout, webViewPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.settingButton, pendingIntent);


        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
