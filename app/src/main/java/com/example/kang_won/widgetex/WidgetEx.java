package com.example.kang_won.widgetex;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Kang-won on 2015-09-22.
 */
public class WidgetEx extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        for (int i = 0; i < appWidgetIds.length; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent intent = new Intent(context, SetActivity.class);
        Intent webViewIntent = new Intent(context, WebViewActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent webViewPendingIntent = PendingIntent.getActivity(context, 0, webViewIntent, 0);

        //remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widgetTime, webViewPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
