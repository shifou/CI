import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Manager {
	public static void main(String[] args) throws IOException, InterruptedException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		MessagePasser messagePasser = new MessagePasser(args[0], args[1]);
		while(true){
			System.out.println("Enter the command you want to execute: send or receive");
			String cm = in.readLine();
			String dest, kind, sendingMessage;	
			switch(cm){
				case "send":
					
					Message message = new Message(dest,kind, sendingMessage);
					message.set_source();
					message.set_seqNum();
					messagePasser.send(message);
					break;
				case "receive":
					System.out.println(messagePasser.receive().toString());
					break;
				default:
					System.err.println("Illegal input format! Please enter again!");
				}
		}
	}
}
