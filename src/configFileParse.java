
import java.io.*;
import java.util.*;
import java.lang.*;
import org.yaml.snakeyaml.Yaml;

public class configFileParse {
		
		private List<LinkedHashMap<String ,String>> NodeInfo;
		private ArrayList<LinkedHashMap<String,String>> sendRules;
		private ArrayList<LinkedHashMap<String,String>> recvRules;
		public configFileParse(String configFile) throws FileNotFoundException {
			  
			    NodeInfo = new ArrayList<LinkedHashMap<String,String>>();
			    sendRules = new ArrayList<LinkedHashMap<String,String>>();
			    recvRules = new ArrayList<LinkedHashMap<String,String>>();
			    InputStream input = new FileInputStream(new File(configFile));
			    Yaml yaml = new Yaml();
			    HashMap data = (HashMap)yaml.load(input);
			    
			    for(LinkedHashMap<String, String> p :(ArrayList<LinkedHashMap<String, String>>)data.get("configuration"))
			    {
			    	LinkedHashMap<String, String> tmp = new LinkedHashMap<String, String>();
			    	tmp.putAll(p);
			    	NodeInfo.add(tmp);
			    }
			    
			    for(LinkedHashMap<String, String> p :(ArrayList<LinkedHashMap<String, String>>)data.get("sendRules"))
			    {
			    	LinkedHashMap<String, String> tmp = new LinkedHashMap<String, String>();
			    	tmp.putAll(p);
			    	sendRules.add(tmp);	    	
			    	
			    }
			   
			    
			    for(LinkedHashMap<String, String> p :(ArrayList<LinkedHashMap<String, String>>)data.get("receiveRules"))
			    {
			    	LinkedHashMap<String, String> tmp = new LinkedHashMap<String, String>();
			    	tmp.putAll(p);
			    	recvRules.add(tmp);	    	
			    	
			    }
			   
			  
			}
			public List<LinkedHashMap<String, String>> get_config()
			{
				return NodeInfo;
			}
			
			public LinkedHashMap<String, String> findByName(String name)
			{
				for(LinkedHashMap<String, String> t : NodeInfo)
				{
					if(name.equals(t.get("name")))
					{
						return t;
					}
				}
				return null;
			}
			
			public String sendRule(Message sendMsg)
			{
				
			}
			
			public static void main(String[] arg) throws FileNotFoundException{
				configFileParse a = new configFileParse("/Users/Moon/Desktop/example.yaml");
				
					System.out.println(a.find_userinfo("p"));
			}   

}
