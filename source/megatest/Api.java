package megatest;

import java.util.ArrayList;

/*
 * For example bild api:
 * https://testsuper.su/api/getpost/ru/2/newvk?ext=smiles&place=3
 * https://megatest.pro/api/getonetest/ru/0?h=125&key=cihipdpohhihephdljcidffkncegcinp
 * https://megatest.org/api/fivenewtests?type=json&h=15
 */
public class Api{
	
	private static String https = "https://";
	private static String[] domens = {"megatest.online","megatest.pw","megatest.org","megatest.name","testdev.ru","testsuper.su","testsuper.name","testsuper.net"};
	private static String[] networks = {"ok","vk","newvk","fb"};
	private static String[] exts = {"ext=smiles","ext=tst","ext=wa","ext=gul","ext=quiz","ext=super"};
	private static String[] langs = {"ru","en"};
	private static String[] genders = {"0","1","2"};
	private static String[] apis = {"/api/getpost/","/api/getonetest/","/api/fivenewtests"};
	private static String key = "key=cihipdpohhihephdljcidffkncegcinp";
	ArrayList<String> list = new ArrayList<>();
	
	public ArrayList<String> getAllListApi(){
		apiGetPost();
		apiFiveNewTests();
		apiGetOneTest();
		return list;
	}
	
	public ArrayList<String> apiGetPost(){
			for (String ext:exts){
				for(String gender:genders){
					for(String network:networks){
						for(String lang:langs){
							for(String domen:domens){
							StringBuffer link = new StringBuffer(); 
							link.append(https).append(domen).append(apis[0]).append(lang).append("/").append(gender).append("/").append(network).append("?").append(ext);
							list.add(link.toString());
						}
					}
				}
			}		
		}
		return list;
	}
	
	public ArrayList<String> apiFiveNewTests() {
		for (String domain: domens){
			for (String lang:langs){
				StringBuffer link = new StringBuffer();
				link.append(https).append(domain).append(apis[2]).append("?lang=").append(lang);
				list.add(link.toString());
			}
		}
		return list;
	}
	
	
	public ArrayList<String> apiGetOneTest(){
			for (String domain:domens){
				for (String lang:langs){
					for (String gender:genders){
						StringBuffer link = new StringBuffer();
						link.append(https).append(domain).append(apis[1]).append(lang).append("/").append(gender).append("?h=15&").append(key);
						list.add(link.toString());
					}
				}
			}	
		return list;
	}
}
