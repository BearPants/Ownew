package com.example.kang_won.widgetex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectRSSFeedActivity extends Activity {

    private Context mContext;
    private ArrayList<IndexList> indexList;
    private AlertDialog tempDialog;
    private String tempUrl;
    private String tempName;
    private ExpandableListView indexListView;
    private Button okButton;
    private EditText userFeedText;
    private EditText tempEditText;
    private int widgetID;
    private DBManager dbManager;
    private int itemType;

    private final int YOUTUBE_RSS = 10;
    private final int NEWS_RSS = 11;
    private final int UNKNOWN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_rssfeed);

        Intent receivedIntent = getIntent();
        widgetID = receivedIntent.getIntExtra("WidgetID", 0);

        mContext = (Context) this;
        okButton = (Button) findViewById(R.id.OKButton);
        userFeedText = (EditText) findViewById(R.id.setUserFeed);
        indexListView = (ExpandableListView) findViewById(R.id.IndexListView);

        updateListView();
    }


    private ArrayList<IndexList> getIndexList() {
        ArrayList<IndexList> temp = new ArrayList<IndexList>();

        temp.add(new IndexList("뉴스"));
        temp.add(new IndexList("날씨"));
        temp.add(new IndexList("스포츠"));
        temp.add(new IndexList("연예"));
        temp.add(new IndexList("IT"));
        temp.add(new IndexList("YOUTUBE"));

        dbManager = new DBManager(getApplicationContext());

        int feedCount = dbManager.getRecordCountAtMyfeed(widgetID);
        String[] RSSFeed = dbManager.selectDataByWidgetIdAtMyfeed(widgetID, "RSS_feed");
        String[] RSSName = dbManager.selectDataByWidgetIdAtMyfeed(widgetID, "RSS_name");

        Log.d("WidgetID", widgetID + "");
        Log.d("FeedCount", feedCount + "");

        if (feedCount != 0) {
            temp.add(new IndexList("나의 FEED", RSSFeed, RSSName));
        } else {
            temp.add(new IndexList("나의 FEED"));
        }

        return temp;
    }

    private void setButtonFunction() {
        okButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v1) {
                String temp = userFeedText.getText().toString();
                if (temp != null) {
                    tempUrl = temp;
                    tempDialog = addDialog();
                    tempDialog.show();
                }
            }
        });
    }

    private void setListViewFunction() {
        indexListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                tempUrl = indexList.get(groupPosition).getContentsList().get(childPosition).getUrl();
                tempName = indexList.get(groupPosition).getContentsList().get(childPosition).getName();
                if (groupPosition == 5)
                    itemType = YOUTUBE_RSS;
                else
                    itemType = NEWS_RSS;
                tempDialog = createDialog(tempName);
                tempDialog.show();
                return false;
            }
        });

    }

    private void updateListView() {
        indexList = getIndexList();
        indexListView.setAdapter(new BaseExpandableAdapter(this, indexList));

        setListViewFunction();
        setButtonFunction();

    }

    private AlertDialog addDialog() {
        AlertDialog.Builder temp = new AlertDialog.Builder(this); // 빌더를 얻고
        tempEditText = new EditText(this);
        TextView tempText = new TextView(this);
        tempEditText.setHint("입력하신 Feed의 이름을 입력하세요");
        tempText.setText("\n 잘못된 Feed일 경우 동작하지 않을 수 있습니다.\n");
        tempText.setTextSize(13);
        temp.setTitle("나의FEED 추가")
                .setView(tempText)
                .setView(tempEditText)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       /*tempUrl과 tempEditText.getText().toString()을 이름으로 디비에 추가하기*/
                        Log.d("RSSFeed!!!!", tempUrl);
                        Log.d("RSSName!!!!", tempEditText.getText().toString());

                        int count = dbManager.getRecordCountAtMyfeed(widgetID);
                        Log.d("WIDGETID !!!!", widgetID + "");
                        Log.d("RSSCOUNT !!!!", count + "");

                        dbManager.insertDataAtMyfeed(widgetID, tempUrl, tempEditText.getText().toString());

                        tempUrl = null;
                        indexListView.deferNotifyDataSetChanged();

                        updateListView();
                        userFeedText.setText("");

                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempUrl = null;
                        tempName = null;
                    }
                });

        return temp.create();
    }

    private AlertDialog createDialog(String name) {
        AlertDialog.Builder temp = new AlertDialog.Builder(this); // 빌더를 얻고

        TextView tempText = new TextView(this);
        tempText.setText("\n위젯에 " + name + "Feed를 설정하시겠습니까?\n");
        tempText.setTextSize(15);
        temp.setTitle("FEED 설정")
                .setView(tempText)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(SelectRSSFeedActivity.this, WidgetReceiver.class);
                        intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.FEED);
                        intent.putExtra("WidgetID", widgetID);
                        intent.putExtra(WidgetReceiver.FEED_NAME,tempName);
                        intent.putExtra(WidgetReceiver.FEED_KEY, tempUrl);
                        intent.putExtra(WidgetReceiver.RSS_TYPE, itemType);
                        mContext.sendBroadcast(intent);
                        finish();
                         /*tempUrl넘기기*/
                        tempName=null;
                        tempUrl = null;
                        itemType = UNKNOWN;
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempUrl = null;
                        tempName = null;
                        itemType = UNKNOWN;
                    }
                });

        return temp.create();
    }
}