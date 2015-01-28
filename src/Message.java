import java.io.Serializable;

public class Message implements Serializable{
	
	String hostname;
	String src;
	String des;
	String kind;
	String data;
	String action;
	boolean duplicate = false;
	int seq;
	
	public Message(String hostname,String dest, String kind, String data){
		this.hostname=src=hostname;
		des = dest;
		this.kind = kind;
		this.data = data;
	}
	public void set_hostname(String name){
		this.hostname = name;
	}
	public void set_src(String source){
		this.src = source;
	}
	
	public void set_seqNum(int sequenceNumber){
		this.seq = sequenceNumber;
	}
	
	public void set_duplicate(){
		this.duplicate = true;
	}
	
	public void set_action(String action){
		this.action = action;
	}
	public String toString()
	{
		return src+" to "+des+" "+seq+" act: "+action+" "+kind+" dup: "+duplicate+" "+data; 
	}

	public String getKind() {
		// TODO Auto-generated method stub
		return kind;
	}
	
}
