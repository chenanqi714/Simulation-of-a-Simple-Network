import java.util.ArrayList;
import java.util.List;

public class foo {

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
		    Transport tp = new Transport(sourceId, destinationId, neighbors, message);
			DataLink dl = new DataLink(sourceId, destinationId, neighbors);
			Network nw = new Network(sourceId, destinationId, neighbors);
			dl.nw = nw;
			nw.dl = dl;
			tp.nw = nw;
			nw.tp = tp;
			tp.dl = dl;
            
			for (int i=0; i < life; i++) {
				tp.transport_send_string();
			    dl.datalink_receive_from_channel();
			    if(i % 5 == 0) {
			    	dl.check_timeout(neighbors);
			    }			    
			    try {
					Thread.sleep(1000);
			    } catch (InterruptedException e) {
					e.printStackTrace();
			    }
			}
			tp.transport_output_all_received();
		}
		else {
			for(int i = 3; i < args.length; ++i) {
			    neighbors.add(args[i].charAt(0));
		    }
			Transport tp = new Transport(sourceId, destinationId, neighbors, message);
			DataLink dl = new DataLink(sourceId, destinationId, neighbors);
			Network nw = new Network(sourceId, destinationId, neighbors);
			dl.nw = nw;
			nw.dl = dl;
			tp.nw = nw;
			nw.tp = tp;
			tp.dl = dl;

			for (int i=0; i < life; i++) {
			    dl.datalink_receive_from_channel();
			    if(i % 5 == 0) {
			    	dl.check_timeout(neighbors);
			    }
			    try {
					Thread.sleep(1000);
			    } catch (InterruptedException e) {
					e.printStackTrace();
			    }
			}
			tp.transport_output_all_received();
		}
		
	}

}
