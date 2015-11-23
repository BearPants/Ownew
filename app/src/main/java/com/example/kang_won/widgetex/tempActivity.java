package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class tempActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent receivedIntent = getIntent();
        int widgetID = receivedIntent.getIntExtra("WidgetID", 0);
        Intent intent = new Intent(tempActivity.this, WidgetReceiver.class);
        intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.SYNCHRONIZATION);
        intent.putExtra("WidgetID", widgetID);
        this.sendBroadcast(intent);
        finish();
    }


}
