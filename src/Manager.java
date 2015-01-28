import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Manager {
	public static void main(String[] args) throws IOException, InterruptedException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		MessagePasser messagePasser = new MessagePasser(args[0], args[1]);
		int seq=1;
		while(true){
			System.out.println("Enter the command you want to execute: send or rec");
			String cm = in.readLine();
			String[] hold = cm.split("#");
			String dest, kind, data;	
			switch(hold[0]){
				case "send":
					if(hold.length!=4)
					{
						System.err.println("wrong send command!\n");
						System.out.println("usage: send#bob#ack#what is your name");
						break;
					}
					Message message = new Message(args[1],hold[1],hold[2], hold[3]);
					message.set_seqNum(seq++);
					messagePasser.send(message);
					break;
				case "rec":
					System.out.println(messagePasser.receive().toString());
					break;
				default:
					System.err.println("Illegal input format! Please enter again!");
				}
		}
	}
}
