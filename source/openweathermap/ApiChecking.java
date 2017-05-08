package openweathermap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * This class implements methods of building api from the http://api.openweathermap.org 
 * by fetching parameters for get a query from a local file. 
 */

public class ApiChecking {
	
	public static String key = "appid=e24b841213a930b339399227886fa8bb";
	public static String apiForOneDay = "http://api.openweathermap.org/data/2.5/weather?";
	public static String apiForFewDays = "http://api.openweathermap.org/data/2.5/forecast?";
	public static String apiForGroupCountry = "http://api.openweathermap.org/data/2.5/group?";
	public static String dataParametrs = new File("dataCountry.json").getAbsolutePath();
	public static JSONParser parser = new JSONParser();
	
	
	public static void main(String[] args) throws Exception {
		
		ApiChecking api = new ApiChecking();
		api.positiveScenario();
		System.out.println("-------------negative-scenario---------------");
		api.negativeScenario();
	}
	
	
	/* The method implements positive scenario of tests 
	 * Since api gives the coordinates in different ways, 
	 * we had to round off the coordinate parameters.
	 */
	public void positiveScenario() throws Exception{
		JSONArray array = getDataFromFile(dataParametrs);
		for(int i=0; i<array.size();i++){
			JSONObject list = (JSONObject) array.get(i);
			JSONObject coord = (JSONObject)list.get("coord");
			String lon = coord.get("lon").toString();
			String lat = coord.get("lat").toString();
			//Rounding for api http://api.openweathermap.org/data/2.5/weather?lat=50.433334&lon=30.516666
			double roundLat = Math.rint(100.0*Double.parseDouble(lat))/100.0;
			double roundLon = Math.rint(100.0*Double.parseDouble(lon))/100.0;
			//Rounding for api http://api.openweathermap.org/data/2.5/forecast?lat=50.433334&lon=30.516666
			double roundLatFewDays = Math.round(Double.parseDouble(lat) * 10000.0) / 10000.0;
			double roundLonFewDays = Math.round(Double.parseDouble(lon) * 10000.0) / 10000.0;
			
			if((int)roundLonFewDays%10 == -1){
				roundLonFewDays = new BigDecimal(roundLonFewDays).setScale(4, RoundingMode.UP).doubleValue();
			}
			
			if(isResponseCode200(getApiID(list))&&
			isResponseCode200(getApiName(list))&&
			isResponseCode200(getApiCoord(list))&&
			isResponseCode200(getApiIDFewDays(list))&&
			isResponseCode200(getApiNameFewDays(list))&&
			isResponseCode200(getApiNameFewDays(list))&&
			isParamEqualsJson(getApiID(list), list.get("_id").toString(),"id")&&
			isParamEqualsJson(getApiName(list), list.get("name").toString(), "name")&&
			isParamCoordEqualsJson(getApiCoord(list),Double.toString(roundLat), Double.toString(roundLon))&&
			isParamEqualsJsonFewDays(getApiIDFewDays(list), list.get("_id").toString(),"id")&&
			isParamEqualsJsonFewDays(getApiNameFewDays(list), list.get("name").toString(),"name")&&
			isParamEqualsJsonFewDays(getApiNameFewDays(list), list.get("country").toString(),"country")&&
			isParamCoordEqualsJson(getApiCoordFewDays(list),Double.toString(roundLatFewDays), Double.toString(roundLonFewDays))){
			} else{
				throw new Exception();
			}
		}
	}
			
	public void negativeScenario() throws Exception{
				
		String badApiKey = apiForOneDay + "id=703448" + "&" + key + "a";//401
		String badApiID = apiForOneDay + "id=0" + "&" + key; //404
		String badApiCountry = apiForOneDay + "q=TESTAPI" + "&" + key;//404
		String withoutParams = apiForOneDay + key; //400
		String withoutKey = apiForOneDay + "q=London";//401
		String badCoord = apiForOneDay + "lat=0123" + "&" + "lon=0" + "&" + key;//400
		String badApiKeyFewDays = apiForFewDays + "id=703448" + "&" + key + "f";//401
		String badApiIDFewDays = apiForFewDays + "id=0" + "&" + key; //404
		String withoutApiCountryFewDays = apiForFewDays + "q="+ "&" + key;;//404
		String withoutParamsFewDays = apiForFewDays + key; //400
		String withoutKeyFewDays = apiForFewDays + "q=London";//401
		String badCoordFewDays = apiForFewDays + "lat=0123" + "&" + "lon=0" + "&" + key;//400
		
		if(!isResponseCode200(badApiKey)&& 
		!isResponseCode200(badApiID)&& 
		!isResponseCode200(badApiCountry)&&	
		!isResponseCode200(withoutParams)&&
		!isResponseCode200(withoutKey)&& 
		!isResponseCode200(badCoord)&& 
		!isResponseCode200(badApiKeyFewDays)&& 
		!isResponseCode200(badApiIDFewDays)&&
		!isResponseCode200(withoutApiCountryFewDays)&&	
		!isResponseCode200(withoutParamsFewDays)&& 
		!isResponseCode200(withoutKeyFewDays)&&	
		!isResponseCode200(badCoordFewDays)){
		} else{
			throw new Exception();
		}
		
	}
	
	public static boolean isResponseCode200(String linkApi) throws ParseException{
		Object obj = parser.parse(getHTML(linkApi));
		JSONObject apiData = (JSONObject) obj;
		String responseCod = apiData.get("cod").toString();
		if(responseCod.equals("200")){
			System.out.println("Expected result: 200"+ " Actual result: " + responseCod);
			return true;
		}
		System.out.println("!Don't match. Code: " + responseCod + " message: " + apiData.get("message"));
		return false;
	}
	
	public static boolean isParamEqualsJson(String linkApi, String id, String parametr) throws ParseException{
		
		Object obj = parser.parse(getHTML(linkApi));
		JSONObject apiData = (JSONObject) obj;
		String parametrApi = apiData.get(parametr).toString();
			if(id.equals(parametrApi)){
				System.out.println("Expected result: " + id+ " Actual result: " + parametrApi);
				return true;
			} else{
				System.out.println("!Don't match params from url "+id + " " + " with params from got json "+parametrApi);
				return false;
			}
	}
	
	public static boolean isParamEqualsJsonFewDays(String linkApi, String id, String parametr) throws ParseException{
		
		Object obj = parser.parse(getHTML(linkApi));
		JSONObject apiData = (JSONObject) obj;
		JSONObject city = (JSONObject)apiData.get("city");
		String parametrApi = city.get(parametr).toString();
			if(id.equals(parametrApi)){
				System.out.println("Expected result: " + id+ " Actual result: " + parametrApi);
				return true;
			} else{
				System.out.println("!Don't match params from url "+id + " " + " with params from got json "+parametrApi);
				return false;
			}
	
	}
	
	public static boolean isParamCoordEqualsJson(String linkApi, String getParametrURL, String getTwoParametrURL) throws ParseException{
		
		Object obj = parser.parse(getHTML(linkApi));
		JSONObject apiData = (JSONObject) obj;
		JSONObject coord = (JSONObject)apiData.get("coord");
		if(linkApi.contains("forecast")){
			JSONObject city = (JSONObject) apiData.get("city");
			coord = (JSONObject)city.get("coord");	
		}
		String lon = coord.get("lon").toString();
		String lat = coord.get("lat").toString();
		
			if(getParametrURL.equals(lat) && getTwoParametrURL.equals(lon)){
				System.out.println("Expected result: " + getParametrURL + " & " +getTwoParametrURL+ " Actual result: "+ lat + " & " + lon);
				return true;
			} else{
				System.out.println("!Don't match params from url "+getParametrURL + " "  + getTwoParametrURL + " with params from got json "+lat + " " + lon);
				return false;
			}
	
	}

	public static boolean isParamCoordEqualsJsonFewDays(String linkApi, String getParametrURL, String getTwoParametrURL) throws ParseException{
	
	Object obj = parser.parse(getHTML(linkApi));
	JSONObject apiData = (JSONObject) obj;
	JSONObject city = (JSONObject) apiData.get("city");
	JSONObject coord = (JSONObject)city.get("coord");
	String lon = coord.get("lon").toString();
	String lat = coord.get("lat").toString();
	getParametrURL.contains(lat);
		if(getParametrURL.equals(lat) && getTwoParametrURL.equals(lon)){
			System.out.println("Expected result: " + getParametrURL + " & " +getTwoParametrURL+ " Actual result: "+ lat + " & " + lon);
			return true;
		} else{
			System.out.println("!don't match params from url "+getParametrURL + " "  + getTwoParametrURL + " with params from got json "+lat + " " + lon);
			return false;
		}
	}

	
	public static String getHTML(String urlToRead) {
	       
	      HttpURLConnection conn = null;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      InputStream inputStream; 
	      try {
	    	 URL url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         if(conn.getResponseCode() == 200){
	        	 inputStream = conn.getInputStream();
	         } else{
	        	 inputStream = conn.getErrorStream();
	         }
	         rd = new BufferedReader(new InputStreamReader(inputStream));
	         while ((line = rd.readLine()) != null) {
	        	 result += line;
	         }
	         rd.close();
	      } catch (Exception e) { 	  
	    	  e.printStackTrace();
	      }
	      return result;
	   }
	//read data from local file json
	public static String readDataFromFile(String path) throws IOException{
		String res = "";
		String line;
		@SuppressWarnings("resource")
		BufferedReader read = new BufferedReader(new FileReader(path));
	            while((line=read.readLine())!=null){        
	                     res+= line;
	            } 	        
		 return res;
	}
	
	private static JSONArray getDataFromFile(String pathOfFile) throws IOException, ParseException{
		String data = readDataFromFile(pathOfFile);
		Object obj = parser.parse(data);
		JSONObject allData = (JSONObject) obj;
		JSONArray listData = (JSONArray) allData.get("list");
	return listData;
	}
		
	public static String getApiID(JSONObject listData) throws IOException, ParseException{
			String id = listData.get("_id").toString();
			String apiLink = apiForOneDay + "id=" + id + "&" + key;
	return apiLink;
		}
		
	public static String getApiName(JSONObject listData) throws IOException, ParseException{
			String name = listData.get("name").toString();
			String apiLink = apiForOneDay + "q=" + name + "&" + key;
	return apiLink;
		}
		
	public static String getApiCoord(JSONObject listData) throws IOException, ParseException{
			JSONObject objLon = (JSONObject)listData.get("coord");
			JSONObject objLat = (JSONObject)listData.get("coord");
			String lon = objLon.get("lon").toString();
			String lat = objLat.get("lat").toString();
			String apiLink = apiForOneDay + "lat=" + lat + "&" + "lon=" + lon + "&" + key;
	return apiLink;
		}
		
	public static String getApiIDFewDays(JSONObject listData) throws IOException, ParseException{
			String id = listData.get("_id").toString();
			String apiLink = apiForFewDays + "id=" + id + "&" + key;
	return apiLink;
		}
	
	public static String getApiNameFewDays(JSONObject listData) throws IOException, ParseException{
			String name = listData.get("name").toString();
			String country = listData.get("country").toString();
			String apiLink = apiForFewDays + "q=" + name + "," + country + "&" + key;
	return apiLink;
		}
	
	public static String getApiCoordFewDays(JSONObject listData) throws IOException, ParseException{
			JSONObject objLon = (JSONObject)listData.get("coord");
			JSONObject objLat = (JSONObject)listData.get("coord");
			String lon = objLon.get("lon").toString();
			String lat = objLat.get("lat").toString();
			String apiLink = apiForFewDays + "lat=" + lat + "&" + "lon=" + lon + "&" + key;
	return apiLink;
		}
}
