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
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    public final static String RSS_URL = "";
    public final static String RSS_TYPE = "TYPE";

    public final static int IMAGE_PATH = 1000;
    public final static int COLOR = 1001;
    public final static int GET_WEBVIEW_URL = 1002;
    public final static int SCREENSHOT = 1003;
    public final static int FEED = 1004;
    public final static int RSSLIST = 1005;

    public final static int YOUTUBE_RSS = 10;
    public final static int NEWS_RSS = 11;


    public static boolean isLightOn = false;
    public static Camera camera;
    RSSHandler myRSSHandler;
    Context rssContext;
    ItemList itemList;
    ItemList tempItemList;
    int widgetID;
    private DBManager dbManager;

    ArrayList<Bitmap> bitmaps;
    private int type;
    private int count;
    private ArrayList<Integer> ivList;
    private ArrayList<Integer> tvList;

    @Override
    public void onReceive(Context context, Intent intent) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        rssContext = context;
        int state = intent.getIntExtra(STATE, -1);
        widgetID = intent.getIntExtra("WidgetID", 0);
        Log.d("WidgetID-Receiver", widgetID + "");
        //views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);

        dbManager = new DBManager(context);


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

            type = intent.getIntExtra(RSS_TYPE, 0);


            views.setInt(R.id.news1, "setVisibility", View.VISIBLE);
            myRSSHandler = new RSSHandler();
            myRSSHandler.setItemType(type);
            String feedUrl = intent.getStringExtra(FEED_KEY);

            ProcessXmlTask xmlTask = new ProcessXmlTask();
            xmlTask.execute(feedUrl);

            views.setTextViewText(R.id.news1, "dddd");
            appWidgetManager.updateAppWidget(widgetID, views);
        } else if (state == RSSLIST) {
            ProcessXmlTask xmlTask = new ProcessXmlTask();
            xmlTask.execute(intent.getStringExtra(RSS_URL));
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
                views.setInt(R.id.rss_list, "setVisibility", View.INVISIBLE);

                break;
            case COLOR:
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.rss_list, "setVisibility", View.INVISIBLE);

                break;
            case GET_WEBVIEW_URL:
                views.setInt(R.id.newsView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.rss_list, "setVisibility", View.INVISIBLE);

                break;
            case FEED:
                views.setInt(R.id.newsView, "setVisibility", View.VISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.rss_list, "setVisibility", View.INVISIBLE);
                break;
            case RSSLIST:
                views.setInt(R.id.newsView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.imageView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.colorView, "setVisibility", View.INVISIBLE);
                views.setInt(R.id.rss_list, "setVisibility", View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public Bitmap createBitmapImage(String selectedImagePath) {
        Bitmap temp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 4;
        temp = BitmapFactory.decodeFile(selectedImagePath, options);

        return temp;
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

            count = 0;
            switch (type) {
                case YOUTUBE_RSS:
                    bitmaps = new ArrayList<Bitmap>();
                    ivList = new ArrayList<Integer>();
                    tvList = new ArrayList<Integer>();
                    ivList.add(R.id.iv1);
                    ivList.add(R.id.iv2);
                    ivList.add(R.id.iv3);
                    ivList.add(R.id.iv4);
                    tvList.add(R.id.tv1);
                    tvList.add(R.id.tv2);
                    tvList.add(R.id.tv3);
                    tvList.add(R.id.tv4);

                    new LoadImage().execute(itemList.getThumnailURL(0));
                    new LoadImage().execute(itemList.getThumnailURL(1));
                    new LoadImage().execute(itemList.getThumnailURL(2));
                    new LoadImage().execute(itemList.getThumnailURL(3));

                    break;
                case NEWS_RSS:
                    RemoteViews views = new RemoteViews(rssContext.getPackageName(), R.layout.widget_layout);
                    String[] newsUrls = new String[5];
                    for (int i = 1; i <= 5; i++) {
                         dbManager.insertDataAtRSSItem(widgetID, itemList.getUrl(i));
                    }

                    views.setTextViewText(R.id.news1, itemList.getTitle(1));
                    views.setTextViewText(R.id.news2, itemList.getTitle(2));
                    views.setTextViewText(R.id.news3, itemList.getTitle(3));
                    views.setTextViewText(R.id.news4, itemList.getTitle(4));
                    views.setTextViewText(R.id.news5, itemList.getTitle(5));
                    changeViewVisibility(views, FEED);
                    sendBroadCastForOnUpdate();
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(rssContext);
                    appWidgetManager.updateAppWidget(widgetID, views);
                    break;
                default:
                    break;
            }

            super.onPostExecute(result);
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            bitmaps.add(bitmap);
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            RemoteViews youtube_views = new RemoteViews(rssContext.getPackageName(), R.layout.widget_layout);

            youtube_views.setTextViewText(tvList.get(count), itemList.getTitle(count));
            youtube_views.setImageViewBitmap(ivList.get(count), bitmaps.get(count));
            Log.e("dlksafjlksdjflkasdjf", "아 쒸발: " + count);

            changeViewVisibility(youtube_views, RSSLIST);
            AppWidgetManager youtube_appWidgetManager = AppWidgetManager.getInstance(rssContext);
            youtube_appWidgetManager.updateAppWidget(new ComponentName(rssContext, WidgetEx.class), youtube_views);
            count++;
        }
    }

    public void sendBroadCastForOnUpdate() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

        rssContext.sendBroadcast(alarmIntent);
    }
}


