import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessagePasser {


	public String username;
	public User user;
	public configFileParse config;
	public int port;
	public HashMap<String, nodeInfo> nodes = new HashMap<String, nodeInfo>();
	ConcurrentLinkedQueue<Message> messageRec = new ConcurrentLinkedQueue<Message>();
	public MessagePasser(String configuration_filename, String local_name) {
		config = new configFileParse(configuration_filename);
		username = local_name;
		port = config.getPortbyName(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return;
		}
		nodes= config.getNetMap(username); // TODO: return a hashmap<username, nodeinfo> which contains info other than the current username
		user = new User(username, port,messageRec);
	}

	void send(Message message) {
		String action = config.sendRule(message);
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
		String action = config.sendRule(message);
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

