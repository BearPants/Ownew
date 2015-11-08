package com.example.kang_won.widgetex;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity {

    private TextView mResult;

    RSSHandler myRSSHandler = new RSSHandler();
    ItemList itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult = (TextView) findViewById(R.id.xmlTextView);

        ProcessXmlTask xmlTask = new ProcessXmlTask();
        xmlTask.execute("http://rss.moneytoday.co.kr/st_news.xml");
    }//http://rss.hankooki.com/sports/sp00_list.xml

    //http://rss.moneytoday.co.kr/st_news.xml
//http://www.chosun.com/site/data/rss/rss.xml
    private class ProcessXmlTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            try {

                String htmlCode = "";

                htmlCode = XMLparsing.getHTML(urls[0]);
                htmlCode.replaceAll("&", "&amp");
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource();
                myInputSource.setEncoding("UTF-8");
                myInputSource.setCharacterStream(new StringReader(htmlCode));
                myXMLReader.parse(myInputSource);

                itemList = myRSSHandler.getRssInfoList();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (SAXException e) {
                e.printStackTrace();
                //mResult.setText("Cannot connect RSS!");
            } catch (IOException e) {
                e.printStackTrace();
                // mResult.setText("Cannot connect RSS!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mResult.setText(itemList.getList());
            super.onPostExecute(result);
        }
    }

}
