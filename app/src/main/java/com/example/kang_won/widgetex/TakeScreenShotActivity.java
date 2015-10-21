package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class TakeScreenShotActivity extends Activity {

    public WebView wv;
    private int specWidth;
    private int widgetID;
    private String html;
    private String url;
    private Point size;
    private Context mContext;
    private Bitmap bitmap;
    private ByteArrayOutputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_screen_shot);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        mContext = this;
        stream = new ByteArrayOutputStream();

        wv = (WebView) findViewById(R.id.webView2);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        Intent receivedIntent = getIntent();
        url = receivedIntent.getStringExtra("URL");
        widgetID = receivedIntent.getIntExtra("WidgetID", 0);
        Log.d("WidgetID - TakeScreen", widgetID + "");

        HttpGetTask httpGetTask = new HttpGetTask();

        try {
            html = httpGetTask.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                Log.d("WebView", "onPageFinished !!!!!!!!!!!");

                int width, height;

                specWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                wv.measure(specWidth, specWidth);

                Log.d("size", String.valueOf(wv.getMeasuredWidth() + " - " + wv.getMeasuredHeight()));
                Log.d("WidgetID - TakeScreen", widgetID + "");

                width = wv.getMeasuredWidth();
                height = wv.getMeasuredHeight();

                if (width == 0 || height == 0) {
                    width = wv.getWidth();
                    height = wv.getHeight();
                }
                if (height > size.y) {
                    height = size.y;
                }
                if (width > size.x) {
                    width = size.x;
                }
                setBitmap(width, height);

            }
        });

        wv.loadDataWithBaseURL("", html, mimeType, encoding, "");
        httpGetTask.cancel(true);
        wv.setVisibility(View.INVISIBLE);
    }

    void setBitmap(final int width, final int height) {

        wv.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                final Canvas c = new Canvas(bitmap);
                wv.draw(c);

                Intent intent = new Intent(TakeScreenShotActivity.this, WidgetReceiver.class);
                intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.SCREENSHOT);
                intent.putExtra("WidgetID", widgetID);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.d("size", String.valueOf(byteArray.length));

                intent.putExtra(WidgetReceiver.SCREENSHOT_KEY, byteArray);

                mContext.sendBroadcast(intent);
                Log.d("BroadCast", "SendBroadCast !!!!!!!!!!!");

                wv.clearCache(true);
                finish();
            }
        }, 2000);

    }

    class HttpGetTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            html = HTMLCrawling.getHTML(params[0]);
            return html;
        }


    }

}



