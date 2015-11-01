package com.example.kang_won.widgetex;

import java.util.ArrayList;

/**
 * Created by 김재관 on 2015-10-31.
 */
public class IndexList {
    private String title;
    private ArrayList<ContentsList> contentsList;

    public IndexList(String title) {
        this.title = title;
        this.contentsList = getContents();

    }

    public String getTItle() {
        return this.title;
    }

    public ArrayList<ContentsList> getContentsList() {
        return this.contentsList;
    }


    private ArrayList<ContentsList> getContents() {
        ArrayList<ContentsList> temp = new ArrayList<ContentsList>();
        if (this.title.equals("뉴스")) {
            temp.add(new ContentsList("조선일보", "http://www.chosun.com/site/data/rss/rss.xml", R.mipmap.chosun));
            temp.add(new ContentsList("동아일보", "http://rss.donga.com/total.xml", R.mipmap.donga));
            temp.add(new ContentsList("한겨례", "http://www.hani.co.kr/rss/", R.mipmap.han));
            temp.add(new ContentsList("중앙일보", "http://rss.joins.com/joins_news_list.xml", R.mipmap.chungang));
            temp.add(new ContentsList("네이버", "http://feeds.feedburner.com/naver_news_popular", R.mipmap.naver));

        }
        if (this.title.equals("날씨")) {

        }
        if (this.title.equals("스포츠")) {

            temp.add(new ContentsList("스포츠한국", "http://rss.hankooki.com/sports/sp00_list.xml", R.mipmap.sportkorea));
            temp.add(new ContentsList("일간스포츠", "http://rss.joins.com/joins_ilgan_list.xml", R.mipmap.ilgan));
        }
        if (this.title.equals("연예")) {
            temp.add(new ContentsList("스타뉴스", "http://rss.moneytoday.co.kr/st_news.xml", R.mipmap.starnews));

        }
        if (this.title.equals("IT")) {
            temp.add(new ContentsList("ComputerWeekly", "http://www.computerweekly.com/rss/Latest-IT-news.xml", R.mipmap.cw));
        }
        if (this.title.equals("나의FEED")) {
/*디비에서 받아올 수 있게 해야함*/
        }

        return temp;
    }
}
