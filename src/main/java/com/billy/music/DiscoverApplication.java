package com.billy.music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscoverApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DiscoverApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(DiscoverApplication.class, args);
	}

	@Override
	public void run(String... args) {
		BandcampScraper scraper = new BandcampScraper("https://bandcamp.com/billymoroney");
		scraper.getProfile();
		scraper.getLibrary();
	}

}
