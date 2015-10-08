package com.example.kang_won.widgetex;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by 김재관 on 2015-10-04.
 */
public class WidgetReceiver extends BroadcastReceiver {
    public final static String STATE = "STATE";
    public final static String IMAGE_PATH_KEY = "IMAGE_PATH";
    public final static String COLOR_KEY = "COLOR";
    public final static String URL_KEY = "URL";
    public final static String CURURL_KEY = "CURURL";

    public final static int IMAGE_PATH = 1000;
    public final static int COLOR = 1001;
    public final static int GET_WEBVIEW_URL = 1002;

    public static boolean isLightOn = false;
    public static Camera camera;

    @Override
    public void onReceive(Context context, Intent intent) {

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);


        int state = intent.getIntExtra(STATE, -1);
        if (state == -1) {

        } else if (state == IMAGE_PATH) {
            Bitmap bitmap = createBitmapImage(intent.getStringExtra(IMAGE_PATH_KEY));
            views.setImageViewBitmap(R.id.imageView, bitmap);
            changeViewVisibility(views, IMAGE_PATH);
        } else if (state == COLOR) {
            int colorCode = intent.getIntExtra(COLOR_KEY, -1);
            views.setInt(R.id.colorView, "setBackgroundColor", colorCode);
            changeViewVisibility(views, COLOR);

        } else if (state == GET_WEBVIEW_URL) {

        }

        appWidgetManager.updateAppWidget(new ComponentName(context,
                WidgetEx.class), views);

    }

    void changeViewVisibility(RemoteViews views, int state) {
        switch (state) {
            case IMAGE_PATH:
                views.setInt(R.id.imageView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.widgetTime, "setVisibility", View.INVISIBLE);
                break;
            case COLOR:
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.widgetTime, "setVisibility", View.INVISIBLE);
                break;
            case GET_WEBVIEW_URL:
                views.setInt(R.id.widgetTime, "setVisibility", View.VISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public Bitmap createBitmapImage(String selectedImagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        return bm;
    }


}


