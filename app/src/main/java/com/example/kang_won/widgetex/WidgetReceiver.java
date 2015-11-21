package com.example.kang_won.widgetex;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.PowerManager;
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
    public final static String FEED_NAME = "FEEDNAME";
    public final static String SYNCHRONIZATION_KEY = "SYNC";

    public final static int IMAGE_PATH = 1000;
    public final static int COLOR = 1001;
    public final static int GET_WEBVIEW_URL = 1002;
    public final static int SCREENSHOT = 1003;
    public final static int FEED = 1004;
    public final static int RSSLIST = 1005;
    public final static int SYNCHRONIZATION = 1006;

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

        dbManager = new DBManager(context);


        if (state == -1) {

        } else if (state == IMAGE_PATH) {
            Bitmap bitmap = createBitmapImage(intent.getStringExtra(IMAGE_PATH_KEY));
            views.setImageViewBitmap(R.id.imageView, bitmap);
            changeViewVisibility(views, IMAGE_PATH);

            if (dbManager.getRecordCountAtWidgetState(widgetID) != 0) {
                dbManager.updateDataAtWidgetState(widgetID, 0);
            } else {
                dbManager.insertDataAtWidgetState(widgetID, 0);
            }


        } else if (state == COLOR) {
            int colorCode = intent.getIntExtra(COLOR_KEY, -1);
            views.setInt(R.id.colorView, "setBackgroundColor", colorCode);
            changeViewVisibility(views, COLOR);

            if (dbManager.getRecordCountAtWidgetState(widgetID) != 0) {
                dbManager.updateDataAtWidgetState(widgetID, 1);
            } else {
                dbManager.insertDataAtWidgetState(widgetID, 1);
            }


        } else if (state == SCREENSHOT) {
            Log.d("ScreenshotBroadCast", "GET BITMAP");
            String location = intent.getStringExtra(SCREENSHOT_KEY);
            Bitmap bitmap = BitmapFactory.decodeFile(location);
            views.setImageViewBitmap(R.id.imageView, bitmap);
            changeViewVisibility(views, IMAGE_PATH);
            File temp = new File(location);
            temp.delete();

            if (dbManager.getRecordCountAtWidgetState(widgetID) != 0) {
                dbManager.updateDataAtWidgetState(widgetID, 2);
            } else {
                dbManager.insertDataAtWidgetState(widgetID, 2);
            }

        } else if (state == FEED) {
            type = intent.getIntExtra(RSS_TYPE, 0);
            myRSSHandler = new RSSHandler();
            myRSSHandler.setItemType(type);
            String feedUrl = intent.getStringExtra(FEED_KEY);
            String feedName = intent.getStringExtra(FEED_NAME);
            Log.d("FEEDNAME",feedName);
            if (dbManager.getRecordCountAtRSS(widgetID) == 0) {
                dbManager.insertDataAtRSS(widgetID, feedUrl,feedName);
            } else {
                dbManager.updateDataAtRSS(widgetID, feedUrl,feedName);
            }
            ProcessXmlTask xmlTask = new ProcessXmlTask();
            xmlTask.execute(feedUrl);
            appWidgetManager.updateAppWidget(widgetID, views);

        } else if (state == SYNCHRONIZATION) {
            int widgetIds[] = dbManager.selectAllWidgetIdAtWidgetState();
            for (int i = 0; i < widgetIds.length; i++) {
                if (dbManager.getRecordCountAtWidgetState(widgetIds[i]) == 0) {
                    continue;
                }
                int currentState = dbManager.selectDataAtWidgetState(widgetIds[i]);
                if (currentState == 2) {
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    if (pm.isScreenOn()) {
                        Intent tempIntent = new Intent(context, TakeScreenShotActivity.class);
                        tempIntent.putExtra("URL", dbManager.selectDataAtBookmark(widgetIds[i]));
                        tempIntent.putExtra("WidgetID", widgetIds[i]);
                        tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(tempIntent);
                    }

                }
                if (currentState == 3) {

                    myRSSHandler = new RSSHandler();
                    myRSSHandler.setItemType(11);
                    String feedUrl = dbManager.selectDataAtRSS(widgetIds[i], "RSS");
                    Log.d("RSSFEEDURL", feedUrl);
                    ProcessXmlTask xmlTask = new ProcessXmlTask();
                    xmlTask.execute(feedUrl);
                    appWidgetManager.updateAppWidget(widgetIds[i], views);
                }
                if (currentState == 4) {
                    Log.d("YOUTUBE", "들어와");

                    myRSSHandler = new RSSHandler();
                    myRSSHandler.setItemType(10);
                    String feedUrl = dbManager.selectDataAtRSS(widgetIds[i], "RSS");
                    ProcessXmlTask xmlTask = new ProcessXmlTask();
                    xmlTask.execute(feedUrl);
                    appWidgetManager.updateAppWidget(widgetIds[i], views);
                }

            }
        }

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
                    if (dbManager.getRecordCountAtWidgetState(widgetID) != 0) {
                        dbManager.updateDataAtWidgetState(widgetID, 4);
                    } else {
                        dbManager.insertDataAtWidgetState(widgetID, 4);
                    }


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

                    int count2 = 4;
                    int lastItemCount = dbManager.getRecordCountAtRSSItem(widgetID);
                    if (count2 > itemList.getNumberOfItem()) {
                        count2 = itemList.getNumberOfItem();
                    }
                    for (int i = 0; i < lastItemCount; i++) {
                        dbManager.deleteDataAtRSSItem(i, widgetID);
                    }

                    for (int i = 0; i < count2; i++) {
                        dbManager.insertDataAtRSSItem(i, widgetID, itemList.getUrl(i));
                        new LoadImage().execute(itemList.getThumnailURL(i));
                    }
                    sendBroadCastForOnUpdate();


                    break;
                case NEWS_RSS:

                    if (dbManager.getRecordCountAtWidgetState(widgetID) != 0) {
                        dbManager.updateDataAtWidgetState(widgetID, 3);
                    } else {
                        dbManager.insertDataAtWidgetState(widgetID, 3);
                    }


                    RemoteViews views = new RemoteViews(rssContext.getPackageName(), R.layout.widget_layout);
                    int count3 = 5;
                    int lastItemCount2 = dbManager.getRecordCountAtRSSItem(widgetID);
                    int[] viewIds = {R.id.news1, R.id.news2, R.id.news3, R.id.news4, R.id.news5};

                    if (count3 > itemList.getNumberOfItem()) {
                        count3 = itemList.getNumberOfItem();
                    }
                    for (int i = 0; i < lastItemCount2; i++) {
                        dbManager.deleteDataAtRSSItem(i, widgetID);
                    }
                    for (int i = 0; i < count3; i++) {
                        dbManager.insertDataAtRSSItem(i, widgetID, itemList.getUrl(i));
                        views.setTextViewText(viewIds[i], itemList.getTitle(i));
                    }

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
            changeViewVisibility(youtube_views, RSSLIST);
            AppWidgetManager youtube_appWidgetManager = AppWidgetManager.getInstance(rssContext);
            youtube_appWidgetManager.updateAppWidget(widgetID, youtube_views);
            count++;
        }
    }

    public void sendBroadCastForOnUpdate() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

        rssContext.sendBroadcast(alarmIntent);
    }
}


