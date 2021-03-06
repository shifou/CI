
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;




public class User implements Runnable{
	public ServerSocket serverSocket;
	int port;
	String name;
	private volatile boolean running;
	public ConcurrentLinkedQueue messageQueue;
	public ConcurrentHashMap<String, Socket> sk;
	public ConcurrentHashMap<String, ObjectOutputStream> st;
	public LinkedHashMap<String, nodeInfo> nodes;
	public User(String name,int port,ConcurrentLinkedQueue messageRec, ConcurrentHashMap<String, Socket> sockets, ConcurrentHashMap<String, ObjectOutputStream> streams, LinkedHashMap<String, nodeInfo> nodes)
	{
		this.nodes = nodes;
		sk = sockets;
		st= streams;
		messageQueue=messageRec;
		this.name=name;
		running = true;
        try {
         serverSocket = new ServerSocket((short)port);
     } catch (IOException e) {
         e.printStackTrace();
         System.out.println("failed to create the user "+name+" socket");
         System.exit(0);
     }
        System.out.println("start User "+name+" at: "+port);
	}
	public void shutdown()
	{
		running=false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println("waiting for messages");
         while(running)
         {
        	 Socket slaveSocket;
        	 try{
             slaveSocket = serverSocket.accept();
             ObjectOutputStream out=new ObjectOutputStream(slaveSocket.getOutputStream());
             ObjectInputStream objInput = new ObjectInputStream(slaveSocket.getInputStream());
             String name="";
			try {
				name = (String)objInput.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(nodes.containsKey(name)==false)
            {
            		System.out.println("receive connection from unknown host");
            		continue;
            }
            sk.put(name, slaveSocket);
            st.put(name, out);
			Connection handler;
             handler = new Connection(slaveSocket,out,objInput,messageQueue);
             //System.out.println(slaveSocket.getInetAddress()+"\t"+slaveSocket.getPort());
				new Thread(handler).start();
	           // System.out.println("begin send");
	            /*
	            Manager.manager.con.put(conId, slaveService);
	            Message msg= new Message(msgType.CONNECT,conId);
	            Manager.manager.send(conId, msg);
	             */
			//	System.out.println("receive: "+slaveSocket.getInetAddress()+":"+slaveSocket.getPort()+" join in");
        	  }
        	 catch(IOException e){
        	         e.printStackTrace();
        	         System.out.println("socket user "+this.name+" accept failed");
        	         continue;
        	     }
		}
	}
	private String checkName(InetAddress ip, int port2) {
		System.out.println("---"+ip.getHostAddress()+"\t"+port2);
		for(String hold : nodes.keySet())
		{
			nodeInfo tmp = nodes.get(hold);
			System.out.println(tmp.ip+"\t"+tmp.port);
			if(tmp.ip.equals(ip.getHostAddress())&& tmp.port==port2)
				return hold;
		}
		return "";
	}
}
