import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
public class Connection implements Runnable {
	private Socket socket;
	ConcurrentLinkedQueue messageQueue;
	private volatile boolean running;
	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	public Connection(Socket slaveSocket, ConcurrentLinkedQueue mq) throws IOException {
		// TODO Auto-generated constructor stub
		socket = slaveSocket;
		objOutput = new ObjectOutputStream(slaveSocket.getOutputStream());
		objOutput.flush();
		objInput = new ObjectInputStream(slaveSocket.getInputStream());
		running=true;
		messageQueue=mq;
	}
	public void run() {
		try {
			Message mes;
			while (running) {
				try {
					mes = (Message) objInput.readObject();
					//System.out.println("socket rec: "+mes.toString());
					
					messageQueue.offer(mes);
					
				} catch (ClassNotFoundException e) {
					System.out.println("read disconnected message");
					continue;
				}
				catch(EOFException e)
				{
					System.out.println("detect disconnected message");
					continue;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("message error in handle");

		}
	}
	
}
