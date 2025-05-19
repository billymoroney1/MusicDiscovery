package com.billy.music;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class BandcampScraper {

    private final String bandcampProfileURL;

    private static Logger LOG = LoggerFactory.getLogger(BandcampScraper.class);
    public BandcampScraper(String bandcampProfileURL){
        this.bandcampProfileURL = bandcampProfileURL;
    }

    public void getProfile(){
        try {
            Document bandcampProfile = Jsoup.connect(bandcampProfileURL).get();
            String title = bandcampProfile.title();
            LOG.info("title: {}", title);
        } catch (IOException e){
            LOG.error("Error trying to access bandcamp profile, trace:");
            e.printStackTrace();
        }
    }

    public void getLibrary(){
        try {
            Document bandcampProfile = Jsoup.connect(bandcampProfileURL).get();
            // class collection-item-container
            Elements albums = bandcampProfile.getElementsByClass("collection-title-details");
            for (Element el : albums){
                Elements links = el.getElementsByTag("a");
                for (Element link : links){
                    LOG.info(link.attr("href"));
                }
//                LOG.info(el.toString());
            }
        } catch (Exception e) {
            LOG.error("Error fetching albums from library");
            e.printStackTrace();
        }
    }
}
