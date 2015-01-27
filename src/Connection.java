import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Connection implements Runnable {
	private Socket socket;
	private volatile boolean running;
	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	public Connection(Socket slaveSocket, Map receRules) throws IOException {
		// TODO Auto-generated constructor stub
		socket = slaveSocket;
		objOutput = new ObjectOutputStream(slaveSocket.getOutputStream());
		objOutput.flush();
		objInput = new ObjectInputStream(slaveSocket.getInputStream());
		running=true;
	}
	public void run() {
		try {
			Message receiveMessage;
			while (running) {
				try {

					receiveMessage = (Message) objInput.readObject();
					
				} catch (ClassNotFoundException e) {
					//System.out.println("read disconnected message");
					continue;
				}
				catch(EOFException e)
				{
					//System.out.println("detect disconnected message");
					continue;
				}
				catch(Exception e)
				{
					//System.out.println("-----");
					continue;
				}
				switch (receiveMessage.getKind()) {
				case "LookUp":
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("message error in handle");

		}
	}
	
}