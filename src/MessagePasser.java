import org.yaml.snakeyaml.Yaml;

public class MessagePasser {
		
		
}

public class parseConfigFile{
	public List<HashMap<String, String>> configuration;
	public List<HashMap<String, String>> sendRules;
	public List<HashMap<String, String>> recvRules;
	
	public void parseYAML(String config_filename)
	{
		Yaml conf = new Yaml();
		InputStream file = new FileInputStream(new File(config_filename));
		
	}
}