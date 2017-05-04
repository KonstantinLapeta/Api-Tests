package siteParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LinksChecker {
	
	private static List <String>list = new ArrayList<>();

		public static void main(String[] args) throws IOException {
			
			checkResponse();
		}
		
		
		private static void checkResponse() throws IOException{
			parseLinksOnSite("https://megatest.online/ru");
			
			for(int l=0;l< list.size();l++){
				for(int i=0;i<list.size();i++){
					if(list.get(l) == list.get(i) || list.get(i).contains(".ru") || list.get(i).contains("www") || list.get(i).contains(".com")){
						list.remove(i);
					}
				}
				String linkRes = "https://megatest.online"+list.get(l);
				System.out.println(linkRes + " - "+getResponse(linkRes));
				
			}	
		}
		
		private static void parseLinksOnSite(String url) throws IOException{
			Document html = Jsoup.connect(url).get();
			for(int i=0; i<html.getElementsByTag("a").size();i++){
				String current = ""+html.getElementsByTag("a").get(i).attr("href");
				list.add(current);
			}
		}
		
		
		public static int getResponse(String urlToRead){
			
		      int response = 0;
		      try {
		    	  HttpURLConnection conn = (HttpURLConnection) new URL(urlToRead).openConnection();
		         conn.setRequestMethod("GET");
		         response = conn.getResponseCode();
		               } catch (Exception e) { 	  
		    	  e.printStackTrace();
		      }
		      return response;
		}
		
		
	}

	

