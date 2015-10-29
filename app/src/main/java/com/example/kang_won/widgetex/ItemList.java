package com.example.kang_won.widgetex;

import java.util.ArrayList;

/**
 * Created by Kang-won on 2015-10-25.
 */
public class ItemList {

    private ArrayList<RssInfo> rssList;

    public ItemList(){
        rssList = new ArrayList<RssInfo>();
    }

    public void addItem(RssInfo rssInfo) {
        rssList.add(rssInfo);
    }

    public String getList(){
        String str = "";

        for(int i=0;i<rssList.size();i++){
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
