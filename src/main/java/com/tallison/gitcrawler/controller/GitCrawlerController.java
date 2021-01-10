package com.tallison.gitcrawler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tallison.gitcrawler.model.ExtensionData;
import com.tallison.gitcrawler.service.GitCrawlerService;

@RestController
public class GitCrawlerController {

	//The endpoint that receives the requests
	//It's a GET method with one RequestParam, the url to the repository
	@GetMapping("/gitcrawler")
	public List<ExtensionData> gitCrawler(@RequestParam(value = "url", defaultValue = "https://github.com/tallison-cm/todo_list") String url) {
		GitCrawlerService crawler = new GitCrawlerService();
		List<ExtensionData> listED = new ArrayList<ExtensionData>();
		try {
			listED = crawler.getRepository(url);
		} catch (IOException e) {
			System.err.println("Repository not found");
		}
		return listED;
	}
	
}
