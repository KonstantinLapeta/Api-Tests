package megatest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class SpeedExecute {
	
		public static void main(String[] args) {
			Api api = new Api();
			
			isCode200(api.getAllListApi());
			//isCode200(api.apiFiveNewTests());
			//isCode200(api.apiGetOneTest());
			//parseApiFiveNewTests(api.apiFiveNewTests());
			
		}
		
		public static void isCode200(ArrayList <String>urls){
			long start = System.currentTimeMillis();
	        
	        ExecutorService executor = Executors.newFixedThreadPool(30);
	        for (String url : urls) {
	            executor.execute(() -> {
	            	int response = getResponse(url);
	            	if(response==200){
	            		System.out.println(url +" - " + response);	
	            	}else{
	            		System.out.println("ERROR" + url);
	            		throw new Error();
	            	}
	            }); 
	        }
	        	executor.shutdown();
	        while (!executor.isTerminated()) {
	        }
	        System.out.println("Finished all threads; Urls: " + urls.size());
	        System.out.println((System.currentTimeMillis() - start) / 1000 + " sec");
		}
		
		public static void parseApiFiveNewTests(ArrayList <String> links){
			
			long start = System.currentTimeMillis();
			ExecutorService executor = Executors.newFixedThreadPool(10);
			for(String link:links){
				 
				 	 executor.execute(() -> {
					 Document dom = null;
				try {
					dom = Jsoup.connect(link.toString()).get();
				} catch (IOException e) {
				e.printStackTrace();
				}
				Elements elementsA = dom.getElementsByTag("a");
			for(int a = 0; a<elementsA.size(); a++){
				String href = elementsA.get(a).attr("href");
				int responseCode = getResponse(href);
				if(responseCode != 200 || elementsA.size() != 7){
					System.out.println("ERROR " + link);
					throw new Error();
				}
			System.out.println(href + " - " + responseCode);
		}});
	  }
			executor.shutdown();
	        while (!executor.isTerminated()) {
	        }
	        System.out.println("Finished all threads; Urls: " + links.size()*7);
	        System.out.println((System.currentTimeMillis() - start) / 1000);
	}
		
		
		public static int getResponse(String url){
			int response = 0;
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                 conn.setRequestMethod("GET");
                 response = conn.getResponseCode();
             } catch (Exception e) {  }
			return response;
		}
		
}
