package com.example.kang_won.widgetex;

/**
 * Created by Kang-won on 2015-10-25.
 */
public class RssInfo {

    private String itemTitle;
    private String itemURL;
    private String itemDescription;
    private String itemDate;
    private String itemThumnailURL;
    private int itemType;

    public RssInfo() {

    }

    public RssInfo(String itemTitle, String itemURL, String itemDescription, String itemDate, String itemThumnailURL, int itemType) {
        this.itemTitle = itemTitle;
        this.itemURL = itemURL;
        this.itemDescription = itemDescription;
        this.itemDate = itemDate;
        this.itemThumnailURL = itemThumnailURL;
        this.itemType = itemType;
    }


    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public void setItemThumnailURL(String itemThumnailURL) {
        this.itemThumnailURL = itemThumnailURL;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemURL() {
        return itemURL;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemDate() {
        return itemDate;
    }

    public String getItemThumnailURL() {
        return itemThumnailURL;
    }

}
