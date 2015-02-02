import java.io.File;
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
	public long last;
	public LinkedHashMap<String, nodeInfo> nodes = new LinkedHashMap<String, nodeInfo>();
	public ConcurrentLinkedQueue<Message> messageRec = new ConcurrentLinkedQueue<Message>();
	public HashMap<String, Socket> sockets = new HashMap<String, Socket>();
	public HashMap<String, ObjectOutputStream> streams= new HashMap<String, ObjectOutputStream>();
	public ConcurrentLinkedQueue<Message> delaySend = new ConcurrentLinkedQueue<Message>();
	public ConcurrentLinkedQueue<Message> delayRec = new ConcurrentLinkedQueue<Message>();
	public ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
	public MessagePasser(String configuration_filename, String local_name) throws FileNotFoundException {
		config = new configFileParse(configuration_filename);
		filename = configuration_filename;
		File hold  = new File(filename);
		last=hold.lastModified();
		System.out.println("last: "+last);
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
		new Thread(user).start();
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
	private boolean reconfig() throws FileNotFoundException {
		// TODO Auto-generated method stub
		if(last>=new File(filename).lastModified())
		{

			System.out.println("last: "+new File(filename).lastModified());
			 return false;
		
		}	 
		last=new File(filename).lastModified();
		config = new configFileParse(filename);
		port = config.getPortbyName(username);
		if(port==-1)
		{
			System.out.println("can not find the user info in config");
			return false;
		}
		nodes= config.getNetMap(username);
		sockets.clear();
		streams.clear();
		//System.out.println(nodes);
		//sockets = getSocketMap(nodes);
		//user = new User(username, port,messageRec);
		return true;
	}

	void send(Message mes) throws FileNotFoundException {
		System.out.println("reread: "+reconfig());
		//System.out.println(mes.des);
		if(this.nodes.containsKey(mes.des)==false)
		{
			System.out.println("can not find this node information in the config");
			return;
		}
		String hold = config.sendRule(mes);
		//System.out.println(hold+"-----");
		switch(hold){
			case "drop":
				System.out.println("drop");
				break;
			case "duplicate":
				sendMessage(mes);
				mes.set_duplicate();
				sendMessage(mes);
				break;
			case "delay":
				System.out.println("send delay");
				delaySend.offer(mes);
				break;
			default:
				System.out.println("send");
				sendMessage(mes);
				break;
		}
	}


	private void sendMessage(Message mes) {
		// TODO Auto-generated method stub
		if(this.sockets.containsKey(mes.des)==false)
		{
			System.out.println("add "+mes.des+" to socket list");
			nodeInfo hold= nodes.get(mes.des);
			try {
				System.out.println(hold.ip+"\t"+hold.port);
				Socket sendd = new Socket(hold.ip, hold.port);
				ObjectOutputStream out=new ObjectOutputStream(sendd.getOutputStream());
				sockets.put(mes.des, sendd);
				streams.put(mes.des, out);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try{
			//System.out.println("des: "+mes.des);
		ObjectOutputStream out= streams.get(mes.des);
		out.writeObject(mes);
		out.flush();
		out.reset();
		//System.out.println("###"+delaySend.size());
		}catch(IOException e){
			System.err.println("send fail");
			return;
		}
		while(!delaySend.isEmpty())
		{
			sendMessage(delaySend.poll());
		}
		
		
	}

	Message receive() throws FileNotFoundException {
		System.out.println("reread: "+reconfig());

		receiveMessage();
		if(!messages.isEmpty()){
			Message mes = messages.poll();
			return mes;
		}
		else{
			return new Message(null,null, null, null,"no message received");
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

