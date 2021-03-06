package com.tallison.gitcrawler.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tallison.gitcrawler.GitCrawlerServiceApplication;
import com.tallison.gitcrawler.model.ExtensionData;
import com.tallison.gitcrawler.util.SizeConverter;

public class GitCrawlerService {
	
	//Method for making requests on GitHub
	private List<Object> doGetRequest(String url) throws IOException{
		if( GitCrawlerServiceApplication.cache.containsKey(url) ) {
			List<Object> cachedResult = GitCrawlerServiceApplication.cache.get(url).orElse(null);
			if( cachedResult != null)
				return cachedResult;
		}
		
		URL urlObj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		InputStream responseStream = connection.getInputStream();
		
		List<Object> result = new BufferedReader(new InputStreamReader(responseStream)).lines().collect(Collectors.toList());
		GitCrawlerServiceApplication.cache.put(url, result);
		
		return result;
	}
	
	//The method that initiates requests. All links are placed in a list for the next requests
	public List<ExtensionData> getRepository(String gitToCrawl) throws IOException {
		List<Object> result = this.doGetRequest(gitToCrawl);
		List<String> linksToGo = this.getLinksToGo(result);
		
		List<ExtensionData> listED = new ArrayList<ExtensionData>();
		this.getFolder(linksToGo, listED);
		
		return listED;
	}
	
	//Recursive method that cycles through all folders and files. A list containing the extensions, their line sizes and quantities is created
	private void getFolder(List<String> linksToGo, List<ExtensionData> listED) throws IOException {
		for(String link: linksToGo) {
			
			try {
				List<Object> linkResult = this.doGetRequest(link);
				List<String> innerLinksToGo = this.getLinksToGo(linkResult);
			
				if(innerLinksToGo.size() == 0) {
					ExtensionData ed = this.getExtensionData(linkResult, link);
					this.addElement(listED, ed);
				}else {
					this.getFolder(innerLinksToGo, listED);
				}
				
			}catch (Exception e) {
				continue;
			}
		}
	}

	//A parser to transform the result of the request into a list of links
	private List<String> getLinksToGo(List<Object> resultPage) {
		List<String> rawLinksToGo = resultPage.stream().map( o -> (String) o ).filter( o -> o.contains("href") && o.contains("js-navigation-open link-gray-dark") ).collect(Collectors.toList());
		List<String> rawLastTime = resultPage.stream().map( o -> (String) o ).filter( o -> o.contains("datetime") ).collect(Collectors.toList());
		List<String> linksToGo = new ArrayList<String>();
		
		Integer idx = 0;
		for(String link: rawLinksToGo) {
			
			if( rawLastTime.size() > 0 ) {
				String lastUpd = rawLastTime.get(idx);
				this.verifyTimeInCache(link, lastUpd);
			}
			
			String startHref = "href=\"";
			Integer hrefIndex = link.indexOf(startHref);
			linksToGo.add("https://github.com" + link.substring(hrefIndex+startHref.length(), link.indexOf("\"", hrefIndex+startHref.length())));
			
			idx+=1;
		}
		
		return linksToGo;
	}
	
	//Method that checks whether the file has been updated recently. If it has been updated, the key is removed from the cache
	private void verifyTimeInCache(String link, String lastUpd) {
		String startLastUpd = "datetime=\"";
		Integer lastUpdIndex = lastUpd.indexOf(startLastUpd);
		
		String strDateTime = lastUpd.substring(lastUpdIndex+startLastUpd.length(), lastUpd.indexOf("\"", lastUpdIndex+startLastUpd.length()));
		LocalDateTime dateTime = LocalDateTime.parse(strDateTime.replace("Z", ""));
		
		Long timeDiff = LocalDateTime.now().toEpochSecond(ZoneOffset.MAX) - dateTime.toEpochSecond(ZoneOffset.MAX);
		if( timeDiff < GitCrawlerServiceApplication.DEFAULT_CACHE_TIMEOUT/100 )
			GitCrawlerServiceApplication.cache.remove(link);
	}
	
	//Method that, when reaching the end of a recursion, returns the number of lines and the file size in the class ExtensionData
	private ExtensionData getExtensionData(List<Object> result, String fileUrl) {
		String fileExt = this.getFileExtension(fileUrl);
		Integer nLines = 0;
		Double size = 0d;
		
		String resultStr = result.stream().map( o -> (String) o ).reduce("", String::concat);
		String infosDiv = "text-mono f6 flex-auto pr-3 flex-order-2 flex-md-order-1 mt-2 mt-md-0\">";
		String spanDivider = "/span>";
		
		Integer infosIdx = resultStr.toLowerCase().indexOf(infosDiv);
		Integer infosEndIdx = resultStr.toLowerCase().indexOf("</div>", infosIdx);
		String infosLine = resultStr.substring(infosDiv.length()+infosIdx, infosEndIdx).trim();
		
		try {
			Integer indexOfLines = infosLine.toLowerCase().indexOf("lines");
			nLines = Integer.parseInt(infosLine.substring(0, indexOfLines).trim());
		}catch (Exception e) {
			System.err.println("Number of lines not found");
		}
		
		try {
			String sizeLine = infosLine;
			Integer indexOfSpan = infosLine.toLowerCase().indexOf(spanDivider);
			if( indexOfSpan > 0)
				sizeLine = infosLine.substring(spanDivider.length()+indexOfSpan).trim();
			size = SizeConverter.toBytes(sizeLine.split(" ")[0], sizeLine.split(" ")[1].toLowerCase());
		}catch (Exception e) {
			System.err.println("Number of bytes not found");
		}
		
		return new ExtensionData(fileExt, nLines, size);
	}
	
	//Method that returns the file extension
	private String getFileExtension(String url) {
		String[] split_url = url.split("\\.");
		return split_url[split_url.length - 1];
	}
	
	//Method to add and accumulate the number of lines and the size of files by extension
	private void addElement(List<ExtensionData> listED, ExtensionData new_ed) {
		ExtensionData old_ed = listED.stream().filter( o -> o.getName().equals(new_ed.getName()) ).findFirst().orElse(null);
		if(old_ed != null) {
			old_ed.setLines( old_ed.getLines() + new_ed.getLines() );
			old_ed.setSize( (double) Math.round(old_ed.getSize() + new_ed.getSize()) );
		}else
			listED.add(new_ed);
	}
	
}

