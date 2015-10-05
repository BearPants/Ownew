package com.example.kang_won.widgetex;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.widget.RemoteViews;

/**
 * Created by 김재관 on 2015-10-04.
 */
public class WidgetReceiver extends BroadcastReceiver {
    public final static String STATE = "STATE";
    public final static String IMAGE_PATH_KEY = "IMAGE_PATH";
    public final static String COLOR_KEY = "COLOR";
    public final static String CURURL_KEY = "CURURL";

    public final static int IMAGE_PATH = 1000;
    public final static int COLOR = 1001;
    public final static int GETWEBVIEWURL = 1002;

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
        } else if (state == COLOR) {
            int colorCode = intent.getIntExtra(COLOR_KEY,-1);
            views.setInt(R.id.imageView, "setBackgroundColor", colorCode);
        } else if (state == GETWEBVIEWURL){
            String setURL = intent.getStringExtra(CURURL_KEY);

            views.setTextViewText(R.id.widgetTime, setURL);
        }

        appWidgetManager.updateAppWidget(new ComponentName(context,
                WidgetEx.class), views);

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


