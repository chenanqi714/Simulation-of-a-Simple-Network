import java.util.ArrayList;
import java.util.List;

public class foo {
	DataLink dl;
	Network nw;
	Transport tp;
	String message;
	char sourceId;
	char destinationId;
	List<Character> neighbors;
	int life;
	
	public foo(char sourceId, char destinationId, List<Character> neighbors, String message, int life) {
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.neighbors = neighbors;
		this.message = message;
		this.life = life;
		this.tp = new Transport(sourceId, destinationId, neighbors, message);
		this.dl = new DataLink(sourceId, destinationId, neighbors, life);
		this.nw = new Network(sourceId, destinationId, neighbors);
		this.dl.nw = this.nw;
		this.nw.dl = this.dl;
		this.tp.nw = this.nw;
		this.nw.tp = this.tp;
		this.tp.dl = this.dl;
	}
	

	public static void main(String[] args) {
		if(args.length < 4 ) {
			System.out.println("Usage: <program> <sourceId> <duration> <destinationId> <message> <neighbor(s)>");
			return;
		}
		char sourceId = args[0].charAt(0);
		int life = Integer.parseInt(args[1]);
		char destinationId = args[2].charAt(0);
		String message = args[3];
		List<Character> neighbors = new ArrayList<Character>();
		if(message.length() > 1) {
		    for(int i = 4; i < args.length; ++i) {
			    neighbors.add(args[i].charAt(0));
		    }
		}
		else {
			for(int i = 3; i < args.length; ++i) {
			    neighbors.add(args[i].charAt(0));
		    }
		}
		foo f = new foo(sourceId, destinationId, neighbors, message, life);
	    for (int i=0; i < life; i++) {							    
		   try {
			   if(message.length() > 1) {
				   f.tp.transport_send_string();
			   }
			   f.dl.datalink_receive_from_channel();
			   if(i != 0 && i % 5 == 0) {
				    f.dl.check_timeout();
			   }
			   Thread.sleep(1000);
			} 
			catch (InterruptedException e) {
			   e.printStackTrace();
			}
		}
		f.tp.transport_output_all_received();		
	}

}
