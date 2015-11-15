package com.example.kang_won.widgetex;

import java.util.ArrayList;

/**
 * Created by Kang-won on 2015-10-25.
 */
public class ItemList {

    private ArrayList<RssInfo> rssList;

    private int itemType;

    public ItemList(int itemType) {
        this.itemType = itemType;
        rssList = new ArrayList<RssInfo>();
    }

    public void addItem(RssInfo rssInfo) {
        rssList.add(rssInfo);
    }

    public String getTitle(int index) {
        return rssList.get(index).getItemTitle();
    }

    public String getUrl(int index) {
        return rssList.get(index).getItemURL();
    }

    public int getNumberOfItem() {
        return rssList.size();
    }

    public String getThumnailURL(int index) {
        return rssList.get(index).getItemThumnailURL();
    }

    public String getList() {
        String str = "";

        for (int i = 0; i < rssList.size(); i++) {
            str += rssList.get(i).getItemTitle();
            str += "-> Title \n";
            str += rssList.get(i).getItemURL();
            str += "-> URL \n";
            str += rssList.get(i).getItemDescription();
            str += "-> Description \n";
            str += rssList.get(i).getItemDate();
            str += "-> Date \n";
            str += rssList.get(i).getItemThumnailURL();
            str += "-> Thumnail \n";
        }

        return str;
    }


}
