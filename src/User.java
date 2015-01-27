
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;




public class User implements Runnable{
	public ServerSocket serverSocket;
	int port;
	String name;
	private volatile boolean running;
	public ConcurrentLinkedQueue messageQueue;
	public User(String name,int port,ConcurrentLinkedQueue messageRec)
	{
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println("waiting for messages");
         while(running)
         {
        	 Socket slaveSocket;
        	 try{
             slaveSocket = serverSocket.accept();
             Connection handler;
             handler = new Connection(slaveSocket,messageQueue);
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
}
