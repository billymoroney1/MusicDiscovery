package com.billy.music;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
}
