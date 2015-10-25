package com.example.kang_won.widgetex;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity {

    private String streamTitle = "";
    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult = (TextView) findViewById(R.id.xmlTextView);

        ProcessXmlTask xmlTask = new ProcessXmlTask();
        xmlTask.execute("http://newssearch.naver.com/search.naver?where=rss&query=maroon5");
    }

    private class ProcessXmlTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            try {

                URL rssUrl = new URL(urls[0]);
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                RSSHandler myRSSHandler = new RSSHandler();
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource(rssUrl.openStream());
                myXMLReader.parse(myInputSource);

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
            mResult.setText(streamTitle);
            super.onPostExecute(result);
        }
    }

    private class RSSHandler extends DefaultHandler {
        final int stateUnknown = 0;
        final int stateTitle = 1;
        int state = stateUnknown;

        int numberOfTitle = 0;
        String strTitle = "";
        String strElement = "";

        @Override
        public void startDocument() throws SAXException {
            strTitle = "--- Start Document ---\n";
        }

        @Override
        public void endDocument() throws SAXException {
            strTitle += "--- End Document ---";
            streamTitle = "Number Of Title: " + String.valueOf(numberOfTitle) + "\n" + strTitle;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equalsIgnoreCase("title")) {
                state = stateTitle;
                strElement = "Title: ";
                numberOfTitle++;
            } else {
                state = stateUnknown;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("title")) {
                strTitle += strElement + "\n";
            }
            state = stateUnknown;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String strCharacters = new String(ch, start, length);
            if (state == stateTitle) {
                strElement += strCharacters;
            }
        }
    }
}
