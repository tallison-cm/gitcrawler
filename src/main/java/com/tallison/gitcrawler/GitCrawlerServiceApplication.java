package com.tallison.gitcrawler;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tallison.gitcrawler.util.SimpleCache;

@SpringBootApplication
public class GitCrawlerServiceApplication {
	
	//Time for cache entry expiration and cache initialization
	public static final Long DEFAULT_CACHE_TIMEOUT = 60*60000L;
	public static final SimpleCache<String, List<Object>> cache = new SimpleCache<String, List<Object>>(DEFAULT_CACHE_TIMEOUT);
	
    public static void main(String[] args) {
        SpringApplication.run(GitCrawlerServiceApplication.class, args);
    }

}
