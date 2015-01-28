
import java.io.*;
import java.util.*;

//import java.lang.*;
import org.yaml.snakeyaml.Yaml;

public class configFileParse {
		
		private List<LinkedHashMap<String ,Object>> NodeInfo;
		private ArrayList<LinkedHashMap<String,Object>> sendRules;
		private ArrayList<LinkedHashMap<String,Object>> recvRules;
		
		public configFileParse(String configFile) throws FileNotFoundException {
			  
			    NodeInfo = new ArrayList<LinkedHashMap<String,Object>>();
			    sendRules = new ArrayList<LinkedHashMap<String,Object>>();
			    recvRules = new ArrayList<LinkedHashMap<String,Object>>();
			    InputStream input = new FileInputStream(new File(configFile));
			    Yaml yaml = new Yaml();
			    HashMap data = (HashMap)yaml.load(input);
			    
			    for(LinkedHashMap<String, Object> p :(ArrayList<LinkedHashMap<String, Object>>)data.get("configuration"))
			    {
			    	LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
			    	tmp.putAll(p);
			    	NodeInfo.add(tmp);
			    }
			 
			    
			    for(LinkedHashMap<String, Object> p :(ArrayList<LinkedHashMap<String, Object>>)data.get("sendRules"))
			    {
			    	LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
			    	tmp.putAll(p);
			    	sendRules.add(tmp);	    	
			    	
			    }
			   
			    
			    for(LinkedHashMap<String, Object> p :(ArrayList<LinkedHashMap<String, Object>>)data.get("receiveRules"))
			    {
			    	LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
			    	tmp.putAll(p);
			    	recvRules.add(tmp);	    	
			    	
			    }
			   
			  
			}
			
		
		public List<LinkedHashMap<String, Object>> get_config()
		{
				return NodeInfo;
		}
			
		public LinkedHashMap<String, Object> findByName(String name)
			{
				for(LinkedHashMap<String, Object> t : NodeInfo)
				{
					if(name.equals(t.get("name")))
					{
						return t;
					}
				}
				return null;
			}
			
		public LinkedHashMap<String, nodeInfo> getNetMap(String username)
		{
			LinkedHashMap<String,nodeInfo> tmp = new LinkedHashMap<String,nodeInfo>();
			
			for(LinkedHashMap<String, Object> t : NodeInfo){
				if(!username.equals(t.get("name")))
				{
					nodeInfo nod = new nodeInfo(((String)t.get("ip")),((Integer)t.get("port")).intValue());
					tmp.put(username, nod);
				}
			}
			
			return tmp;
		}
		public int getPortbyName(String name)
		{	
				for(LinkedHashMap<String, Object> t : NodeInfo)
				{
					
					if(name.equals(t.get("name")))
					{
						if(t.get("port") != null)
						{
							return ((Integer)t.get("port")).intValue();
						}
					}
				}

				return -1;
			}
			
			public boolean itemExist(String item, LinkedHashMap<String, Object> t)
			{
				if(t.get(item) == null)
				{
					return false;
				}else{
					return true;
				}
			}
			
			public String sendRule(Message sendMsg)
			{
				
				for(LinkedHashMap<String, Object> t : sendRules)
				{
					boolean targetRule = true;
					if(itemExist("seqNum",t))
					{
						if(((Integer)t.get("seqNum")).intValue() == sendMsg.seq)
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}

					if(itemExist("dest",t))
					{
						if(((String)t.get("dest")).equals(sendMsg.des))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}

					if(itemExist("src",t))
					{
						if(((String)t.get("src")).equals(sendMsg.src))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}
					if(itemExist("kind",t))
					{
						if(((String)t.get("kind")).equals(sendMsg.kind))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}
					if(targetRule == true)
					{
						return ((String)t.get("action"));
					}
					
				}
				return null;   // no rule need to apply on this message
			}
			
			public String recvRule(Message recvMsg)
			{
				
				for(LinkedHashMap<String, Object> t : recvRules)
				{
					boolean targetRule = true;
					if(itemExist("seqNum",t))
					{
						if(((Integer)t.get("seqNum")).intValue() == recvMsg.seq)
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}

					if(itemExist("dest",t))
					{
						if(((String)t.get("dest")).equals(recvMsg.des))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}

					if(itemExist("src",t))
					{
						if(((String)t.get("src")).equals(recvMsg.src))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}
					if(itemExist("kind",t))
					{
						if(((String)t.get("kind")).equals(recvMsg.kind))
						{
							targetRule = (targetRule && true);
						}else{
							continue;
						}
					}
					if(targetRule == true)
					{
						return ((String)t.get("action"));
					}
					
				}
				return null;   // no rule need to apply on this message
			}
			
			
			public static void main(String[] arg) throws FileNotFoundException{
				configFileParse a = new configFileParse("/Users/Moon/Desktop/example.yaml");
				Message t = new Message("alice","alice","Ack",null);
				t.set_seqNum(5);
				t.set_src("charlie");
				System.out.println(a.recvRule(t));
			}   

}
