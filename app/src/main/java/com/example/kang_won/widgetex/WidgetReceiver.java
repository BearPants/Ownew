package com.example.kang_won.widgetex;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by 김재관 on 2015-10-04.
 */
public class WidgetReceiver extends BroadcastReceiver {
    public final static String STATE = "STATE";
    public final static String IMAGE_PATH_KEY = "IMAGE_PATH";
    public final static String COLOR_KEY = "COLOR";
    public final static String SCREENSHOT_KEY = "SCREENSHOT_PATH";
    public final static String FEED_KEY = "FEED";

    public final static int IMAGE_PATH = 1000;
    public final static int COLOR = 1001;
    public final static int GET_WEBVIEW_URL = 1002;
    public final static int SCREENSHOT = 1003;
    public final static int FEED = 1004;


    public static boolean isLightOn = false;
    public static Camera camera;
    RSSHandler myRSSHandler;
    Context rssContext;
    ItemList itemList;
    ItemList tempItemList;

    @Override
    public void onReceive(Context context, Intent intent) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        rssContext = context;
        int state = intent.getIntExtra(STATE, -1);
        int widgetID = intent.getIntExtra("WidgetID", 0);
        Log.d("WidgetID-Receiver", widgetID + "");
        //views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);

        if (state == -1) {

        } else if (state == IMAGE_PATH) {
            Bitmap bitmap = createBitmapImage(intent.getStringExtra(IMAGE_PATH_KEY));
            views.setImageViewBitmap(R.id.imageView, bitmap);
            changeViewVisibility(views, IMAGE_PATH);
        } else if (state == COLOR) {
            int colorCode = intent.getIntExtra(COLOR_KEY, -1);
            views.setInt(R.id.colorView, "setBackgroundColor", colorCode);
            changeViewVisibility(views, COLOR);

        } else if (state == SCREENSHOT) {
            Log.d("ScreenshotBroadCast", "GET BITMAP");
            String location = intent.getStringExtra(SCREENSHOT_KEY);
            Bitmap bitmap = BitmapFactory.decodeFile(location);
            views.setImageViewBitmap(R.id.imageView, bitmap);
            changeViewVisibility(views, IMAGE_PATH);
            File temp = new File(location);
            temp.delete();
        } else if (state == FEED) {
            views.setInt(R.id.news1, "setVisibility", View.VISIBLE);
            myRSSHandler = new RSSHandler();
            String feedUrl = intent.getStringExtra(FEED_KEY);

            ProcessXmlTask xmlTask = new ProcessXmlTask();
            xmlTask.execute(feedUrl);

            views.setTextViewText(R.id.news1, "dddd");
            appWidgetManager.updateAppWidget(widgetID, views);
        }
        //appWidgetManager.updateAppWidget(new ComponentName(context, WidgetEx.class), views);

        appWidgetManager.updateAppWidget(widgetID, views);

    }

    void changeViewVisibility(RemoteViews views, int state) {
        switch (state) {
            case IMAGE_PATH:
                views.setInt(R.id.imageView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);
                break;
            case COLOR:
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);
                break;
            case GET_WEBVIEW_URL:
                views.setInt(R.id.newsView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                break;
            case FEED:
                views.setInt(R.id.newsView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
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

    private class ProcessXmlTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            try {
                String htmlCode = "";

                htmlCode = XMLparsing.getHTML(urls[0]);
                htmlCode.replaceAll("&", "&amp;");

                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource();
                myInputSource.setCharacterStream(new StringReader(htmlCode));
                myXMLReader.parse(myInputSource);

                itemList = myRSSHandler.getRssInfoList();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (SAXException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (IOException e) {
                e.printStackTrace();
                // mResult.setText("Cannot connect RSS!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            RemoteViews views = new RemoteViews(rssContext.getPackageName(), R.layout.widget_layout);


            views.setTextViewText(R.id.news1, itemList.getTitle(1));
            views.setTextViewText(R.id.news2, itemList.getTitle(2));
            views.setTextViewText(R.id.news3, itemList.getTitle(3));
            views.setTextViewText(R.id.news4, itemList.getTitle(4));
            views.setTextViewText(R.id.news5, itemList.getTitle(5));
            changeViewVisibility(views, FEED);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(rssContext);
            appWidgetManager.updateAppWidget(new ComponentName(rssContext, WidgetEx.class), views);
            super.onPostExecute(result);
        }
    }
}


