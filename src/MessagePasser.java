import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessagePasser {


	public String username;
	public String filename;
	public User user;
	public configFileParse config;
	public int port;
	public LinkedHashMap<String, nodeInfo> nodes = new LinkedHashMap<String, nodeInfo>();
	public ConcurrentLinkedQueue<Message> messageRec = new ConcurrentLinkedQueue<Message>();
	public HashMap<String, Socket> sockets = new HashMap<String, Socket>();
	public ConcurrentLinkedQueue<Message> delaySend = new ConcurrentLinkedQueue<Message>();
	public ConcurrentLinkedQueue<Message> delayRec = new ConcurrentLinkedQueue<Message>();
	public ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
	public MessagePasser(String configuration_filename, String local_name) throws FileNotFoundException {
		config = new configFileParse(configuration_filename);
		filename = configuration_filename;
		username = local_name;
		port = config.getPortbyName(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return;
		}
		nodes= config.getNetMap(username);
		//sockets = getSocketMap(nodes);
		user = new User(username, port,messageRec);
	}
	
	private HashMap<String, Socket> getSocketMap(
			HashMap<String, nodeInfo> nodes) {
		// TODO Auto-generated method stub
		HashMap<String, Socket> ans = new HashMap<String, Socket>();
		for(String each:nodes.keySet())
		{
			nodeInfo hold= nodes.get(each);
			try {
				//System.out.println(hold.ip+"\t"+hold.port);
				Socket sendd = new Socket(hold.ip, hold.port);
				ans.put(each, sendd);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return ans;
	}
	private void reconfig() throws FileNotFoundException {
		// TODO Auto-generated method stub
		config = new configFileParse(filename);
		port = config.getPortbyName(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return;
		}
		nodes= config.getNetMap(username);
		System.out.println(nodes);
		//sockets = getSocketMap(nodes);
		//user = new User(username, port,messageRec);
	}

	void send(Message mes) throws FileNotFoundException {
		reconfig();
		System.out.println(mes.des);
		if(this.nodes.containsKey(mes.des)==false)
		{
			System.out.println("can not find this node information in the config");
			return;
		}
		String hold = config.sendRule(mes);
		System.out.println(hold+"-----");
		switch(hold){
			case "drop":
				break;
			case "duplicate":
				sendMessage(mes);
				mes.set_duplicate();
				sendMessage(mes);
				break;
			case "delay":
				delaySend.offer(mes);
				break;
			default:
				sendMessage(mes);
				break;
		}
	}


	private void sendMessage(Message mes) {
		// TODO Auto-generated method stub
		if(this.sockets.containsKey(mes.des)==false)
		{
			nodeInfo hold= nodes.get(mes.des);
			try {
				//System.out.println(hold.ip+"\t"+hold.port);
				Socket sendd = new Socket(hold.ip, hold.port);
				sockets.put(mes.des, sendd);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try{
		Socket hold = sockets.get(mes.des);
		ObjectOutputStream out;
		out = new ObjectOutputStream(hold.getOutputStream());
		out.writeObject(mes);
		out.flush();
		out.reset();
		while(!delaySend.isEmpty())
		{
			out.writeObject(delaySend.poll());
			out.flush();
			out.reset();
		}
		}catch(IOException e){
			System.err.println("send fail");
			return;
		}
		
	}

	Message receive() throws FileNotFoundException {
		reconfig();

		receiveMessage();
		if(!messages.isEmpty()){
			Message mes = messages.poll();
			return mes;
		}
		else{
			return new Message(null,null, null, "no message received");
		}
		
		
	}

	private void receiveMessage() {
		Message mes;
		if(!messageRec.isEmpty()){
			mes = messageRec.poll();
			String action = this.config.recvRule(mes);
			switch(action){
			case "drop":
				break;
			case "duplicate":
				//System.out.println("receive: duplicate");
				messages.offer(mes);
				messages.offer(mes);
				while(!delayRec.isEmpty()){
					messages.offer(delayRec.poll());
				}
				break;
			case "delay":
				//System.out.println("receive: delay");
				delayRec.offer(mes);
				receiveMessage();
				break;
			default:
				//default action
				//System.out.println("receive: default");
				messages.offer(mes);
				while(!delayRec.isEmpty()){
					messages.offer(delayRec.poll());
				}
			}
	}
	}
}

