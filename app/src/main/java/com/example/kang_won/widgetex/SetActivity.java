package com.example.kang_won.widgetex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;

public class SetActivity extends Activity {

    private final static int SELECT_FILE = 1;
    private int widgetID;

    public Context getContext() {
        return this;
    }

    public void activityFinish() {

        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);

        chooseSetting();
    }

    public void chooseSetting() {
        final CharSequence[] items = {"Browser", "이미지", "색상 설정", "RSS"};

        Intent receivedIntent = getIntent();
        widgetID = receivedIntent.getIntExtra("WidgetID", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("이미지")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                } else if (items[item].equals("색상 설정")) {
                    Intent intent = new Intent(SetActivity.this, ColorPickerViewActivity.class);
                    intent.putExtra("WidgetID", widgetID);
                    startActivity(intent);
                    finish();
                } else if (items[item].equals("RSS")) {
                    Intent intent = new Intent(SetActivity.this, SelectRSSFeedActivity.class);
                    intent.putExtra("WidgetID", widgetID);
                    startActivity(intent);
                    finish();
                } else if (items[item].equals("Browser")) {
                    Intent intent = new Intent(SetActivity.this, WebViewActivity.class);
                    intent.putExtra("WidgetID", widgetID);
                    startActivity(intent);
                    finish();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView image = new ImageView(this);

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                String selectedImagePath = getSelectedImagePath(data);
                Context mContext = getContext();

                Intent intent = new Intent(SetActivity.this, WidgetReceiver.class);
                intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.IMAGE_PATH);
                intent.putExtra(WidgetReceiver.IMAGE_PATH_KEY, selectedImagePath);
                intent.putExtra("WidgetID", widgetID);

                mContext.sendBroadcast(intent);
                finish();

                this.activityFinish();
            }
        }
    }

    public String getSelectedImagePath(Intent data) {

        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }


}
