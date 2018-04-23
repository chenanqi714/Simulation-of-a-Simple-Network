import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transport {
	
	char nodeid;
    char destinationid;
    String message;
    String[] message_pieces;
    int  SWS;
    int  LAR;
    int  LFS;
    boolean ack[];
    List<Character> neighbors;
    Network nw;
    DataLink dl;
    HashMap<Character, String[]> message_recieved;
    HashMap<Character, Integer> max_sn;
    int count = 0;
    
    public Transport(char nodeid, char destinationid, List<Character> neighbors, String message) {
    	this.neighbors = neighbors;
    	this.nodeid = nodeid;
    	this.destinationid = destinationid;
    	this.SWS = 3;
    	this.LAR = 0;
    	this.LFS = 0;
    	this.ack = new boolean[100];
    	message_pieces = new String[message.length()/5 + 1];
        //split message into pieces, each has 5 bytes
    	int i = 0;
        int j = 0;
        while(i < message.length()) {
        	if(i + 5 <= message.length()) {
        		message_pieces[j] = message.substring(i, i+5);
        	}
        	else {
        		message_pieces[j] = message.substring(i, message.length());
        	}
        	i = i + 5;
        	j = j + 1;
        }
        message_recieved = new HashMap<Character, String[]>();
        max_sn = new HashMap<Character, Integer>();
    }
    
    public void transport_send_string() {
    		if(LFS < message_pieces.length && LAR + SWS != LFS) {
    		    StringBuilder builder = new StringBuilder();
        	    builder.append("d");
        	    builder.append(nodeid);
        	    builder.append(destinationid);
        	    builder.append(LFS/10);
        	    builder.append(LFS%10);    	
        	    builder.append(message_pieces[LFS]);
        	    LFS = LFS + 1;
        	    nw.network_receive_from_transport(builder.toString(), builder.toString().length(), destinationid);
    	    }
    	    ++count;
    	    if(count % 20 == 0) {
    		    check_timeout() ;
    	    }   	
    }
    
    public void transport_receive_from_network(String message, int len, char source) {
    	char message_type = message.charAt(0);
    	char sourceid = message.charAt(1);
    	char destinationid = message.charAt(2);
    	int  LFS = Integer.parseInt(message.substring(3, 5));
    	if(message_type == 'd') {
    	    //send ack
    		StringBuilder builder = new StringBuilder();
        	builder.append("a");
        	builder.append(destinationid);
        	builder.append(sourceid);
        	builder.append(LFS/10);
        	builder.append(LFS%10);
        	nw.network_receive_from_transport(builder.toString(), builder.toString().length(), sourceid);
    		
 
        	String mesg = message.substring(5, len);
    		if(message_recieved.containsKey(sourceid)) {
    		    message_recieved.get(sourceid)[LFS] = mesg;
    		}
    		else {
    			String[] list = new String[100];
    			list[LFS] = mesg;
    			message_recieved.put(sourceid, list);
    		}
    	}
    	else {
    		ack[LFS] = true;
    		while(ack[LAR]) {
    			ack[LAR]= false;
    			LAR = LAR + 1;
    		}
    	}   	
    }
    
    public void check_timeout() {
    	if(LAR != LFS) {
    		for(int k = LAR; k < LFS; ++k) {
    			if(!ack[k]) {
    				StringBuilder builder = new StringBuilder();
    	        	builder.append("d");
    	        	builder.append(nodeid);
    	        	builder.append(destinationid);
    	        	builder.append(k/10);
    	        	builder.append(k%10);    	
    	        	builder.append(message_pieces[k]);
    	        	nw.network_receive_from_transport(builder.toString(), builder.toString().length(), destinationid);
    			}
    		}
    	}
    }
    
    public void transport_output_all_received() {
    	Writer wtr = new Writer("node"+nodeid+"received");
    	for(Character sourceid: message_recieved.keySet()) {
    		wtr.writeFile("From "+sourceid+" received: ");
    		for(String message: message_recieved.get(sourceid)) {
    			if(message!=null) {
    				wtr.writeFile(message);
    			}
    			else {
    				break;
    			}
    		}
    		wtr.writeFile("\n");
    	}
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
