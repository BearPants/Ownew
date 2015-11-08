package com.example.kang_won.widgetex;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class RSSHandler extends DefaultHandler {

    private final int STATE_UNKNOW = 0;
    private final int STATE_TITLE = 1;
    private final int STATE_URL = 2;
    private final int STATE_DESCRIPTION = 3;
    private final int STATE_DATE = 4;
    private final int STATE_TUMNAILURL = 5;
    private int currentState = STATE_UNKNOW;
    private boolean complete = false;
    private String str = null;
    private String strCharacters;
    boolean itemFound = false;

    RssInfo rssInfo;
    ItemList rssInfoList;

    public RSSHandler() {

    }

    public ItemList getRssInfoList() {
        return rssInfoList;
    }

    @Override
    public void startDocument() throws SAXException {
        rssInfo = new RssInfo();
        rssInfoList = new ItemList();

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (localName.equalsIgnoreCase("item")) {
            itemFound = true;
            rssInfo = new RssInfo();
            complete = true;
            currentState = STATE_UNKNOW;
        } else if (localName.equalsIgnoreCase("title")) {
            currentState = STATE_TITLE;
        } else if (localName.equalsIgnoreCase("description")) {
            currentState = STATE_DESCRIPTION;
        } else if (localName.equalsIgnoreCase("link")) {
            currentState = STATE_URL;
        } else if (localName.equalsIgnoreCase("pubDate")) {
            currentState = STATE_DATE;
        } else if (qName.equalsIgnoreCase("media:thumbnail")) {
            currentState = STATE_TUMNAILURL;
            str = attributes.getValue(0);
        } else {
            currentState = STATE_UNKNOW;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (localName.equalsIgnoreCase("item") && complete) {
            rssInfoList.addItem(rssInfo);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        strCharacters = new String(ch, start, length);


        if ((currentState == STATE_TITLE) && (strCharacters.indexOf("[") != -1)) {
            currentState = STATE_UNKNOW;
            complete = false;
  /*          int i = 0;
            int count = 0;

            while (count < 2) {
                if(start+i>=ch.length)
                if (ch[start + i] == ']') {
                    count++;
                }
                i++;
            }

            strCharacters = new String(ch, start, i);
*/
        }

        if (itemFound == true) {
            switch (currentState) {
                case STATE_TITLE:
                    rssInfo.setItemTitle(strCharacters);
                    break;
                case STATE_DESCRIPTION:
                    rssInfo.setItemDescription(strCharacters);
                    break;
                case STATE_URL:
                    rssInfo.setItemURL(strCharacters);
                    break;
                case STATE_DATE:
                    rssInfo.setItemDate(strCharacters);
                    break;
                case STATE_TUMNAILURL:
                    rssInfo.setItemThumnailURL(str);
                default:
                    break;
            }
        }

        currentState = STATE_UNKNOW;
    }

}