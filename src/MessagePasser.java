import java.util.concurrent.ConcurrentLinkedQueue;

import org.yaml.snakeyaml.Yaml;

public class MessagePasser {

	public String username;
	public User user;
	public configFileParse config;
	public int port;
	ConcurrentLinkedQueue<Message> messageRec = new ConcurrentLinkedQueue<Message>();
	public MessagePasser(String configuration_filename, String local_name) {
		config = new configFileParse(configuration_filename);
		username = local_name;
		port = config.getByport(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return;
		}
		user = new User(username, port,messageRec);
	}

	void send(Message message) {
		String action = config.checkSendRule(message);
		switch(message.action){
		case "drop":
			break;
		case "duplicate":
			sendMessage(message);
			message.set_duplicate();
			sendMessage(message);
			break;
		case "delay":
			break;
		default:
			sendMessage(message);
			break;
		}
	}

	Message receive() {
		Message message;
		if(!messageRec.isEmpty()){
			message = messageRec.poll();
		String action = config.checkSendRule(message);
		switch(message.action){
		case "drop":
			break;
		case "duplicate":
			break;
		case "delay":
			break;
		default:
			break;
		}
		}
	}
}
