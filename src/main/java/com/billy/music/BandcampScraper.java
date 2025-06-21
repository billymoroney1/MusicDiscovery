package com.billy.music;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jsoup.select.Collector.collect;

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
//            LOG.info("title: {}", title);
        } catch (IOException e){
            LOG.error("Error trying to access bandcamp profile, trace:");
            e.printStackTrace();
        }
    }

    public void getLibrary(){
        Document bandcampProfile = null;
        try {
            bandcampProfile = Jsoup.connect(bandcampProfileURL).get();
        } catch (Exception e){
            System.err.println("Error scraping profile: " + e.getMessage());
        }

        if (null == bandcampProfile){return;}
        else {
            // class collection-item-container
            Elements albums = bandcampProfile.getElementsByClass("collection-title-details");
            ArrayList<String> albumLinks = new ArrayList<String>();
            for (Element el : albums) {
                Elements links = el.getElementsByTag("a");
                for (Element link : links) {
                    albumLinks.add(link.attr("href"));
                }
            }

            Document albumDetail = null;
            try {
                albumDetail = Jsoup.connect(albumLinks.get(6)).get();
            } catch (Exception e) {
                System.err.println("Error retrieving album details: " + e.getMessage());
            }

            if (albumDetail == null){}
            else {
                Elements fans = albumDetail.getElementsByClass("fan");
                ArrayList<String> fanPageLinks = new ArrayList<>();
                for (Element fan : fans) {
                    fanPageLinks.add(fan.attr("href"));
                }

                List<CompletableFuture<ArrayList<String>>> futures = fanPageLinks.stream()
                        .map(link -> CompletableFuture.supplyAsync(() -> {
                            ArrayList<String> fullTitles = new ArrayList<>();
                            try {
                               Document fanPageDoc = Jsoup.connect(link).get();
                               Elements albumDetails = fanPageDoc.getElementsByClass("collection-item-details-container");
                               for (Element details : albumDetails) {
                                   Elements albumTitle = details.getElementsByClass("collection-item-title");
                                   Elements albumArtist = details.getElementsByClass("collection-item-artist");
                                   String fullTitle = String.join(" ", albumTitle.getFirst().html(), albumArtist.getFirst().html());
                                   fullTitles.add(fullTitle);
                               }
                           } catch (Exception e) {
                               System.err.println("Error while scraping a page: " + e);
                           }
                           return fullTitles;
                       }))
                        .toList();

                CompletableFuture<Map<String, Long>> aggregateList = CompletableFuture
                        .allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(List::stream)
                            .collect(Collectors.groupingBy(
                                    Function.identity(),
                                    Collectors.counting()
                            )));


                LOG.info("blocking until crawlers are complete...");
                Map<String, Long> titleCounts = aggregateList.join();

                titleCounts.forEach((title, count) ->
                        System.out.println(String.join(" ", count.toString(), title))
                );

            }
        }
    }
}
