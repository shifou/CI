import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessagePasser {


	public String username;
	public User user;
	public configFileParse config;
	public int port;
	public HashMap<String, nodeInfo> nodes = new HashMap<String, nodeInfo>();
	public ConcurrentLinkedQueue<Message> messageRec = new ConcurrentLinkedQueue<Message>();
	public HashMap<String, Socket> sockets = new HashMap<String, Socket>();
	public MessagePasser(String configuration_filename, String local_name) {
		config = new configFileParse(configuration_filename);
		username = local_name;
		port = config.getByport(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return;
		}
		nodes= config.getNetMap(username);
		sockets = getSocketMap(nodes);
		user = new User(username, port,messageRec);
	}
	
	private HashMap<String, Socket> getSocketMap(
			HashMap<String, nodeInfo> nodes2) {
		// TODO Auto-generated method stub
		return null;
	}

	void send(Message message) {
		if(this.nodes.containsKey(message.des)==false)
		{
			System.out.println("can not find this node information in the config");
			return;
		}
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

