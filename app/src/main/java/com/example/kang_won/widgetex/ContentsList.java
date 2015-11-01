package com.example.kang_won.widgetex;

/**
 * Created by 김재관 on 2015-10-31.
 */
public class ContentsList {
    private String name;
    private String url;
    private int imageSource = R.mipmap.nonimage;

    public ContentsList(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public ContentsList(String name, String url, int imageSource) {
        this.name = name;
        this.url = url;
        this.imageSource = imageSource;
    }


    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public int getImageSource() {
        return this.imageSource;
    }
}
