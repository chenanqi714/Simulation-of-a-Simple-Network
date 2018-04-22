import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Network {
	
	List<Character> neighbors;
    char nodeid;
    char destinationid;
    int messageid;
    HashMap<Character, Integer> map_maxid;
    DataLink dl;
    
    public Network(char nodeid, char destinationid, List<Character> neighbors) {
    	this.neighbors = neighbors;
    	this.nodeid = nodeid;
    	this.destinationid = destinationid;
    	this.messageid = 0; 
    	map_maxid = new HashMap<Character, Integer>();
    }
    
    public void network_receive_from_transport(String message, int len, char dest) {
    	StringBuilder builder = new StringBuilder();
    	builder.append(nodeid);
    	builder.append(destinationid);
    	builder.append(messageid/10);
    	builder.append(messageid%10);    	
    	builder.append(len/10);
    	builder.append(len%10);
    	builder.append(message);
    	messageid++;
    	
    	for(Character neighbor: neighbors) {
		    dl.datalink_receive_from_network(builder.toString(), builder.length(), neighbor);
	    }
    }
    
   public void network_receive_from_datalink(String message, char neighbor_id) {
	   System.out.println("network_receive_from_datalink: "+message);
	   char sourceid = message.charAt(0);
	   char destinationid = message.charAt(1);
	   int messageid = Integer.parseInt(message.substring(2, 4));
	   int len = Integer.parseInt(message.substring(4, 6));
	   String mesg = message.substring(6, 6+len);
	   if(destinationid == nodeid) {
		   Writer wtr = new Writer("node"+nodeid+"received");
		   wtr.writeFile(mesg);
	   }
	   else {
		   if(!map_maxid.containsKey(sourceid)) {
			   map_maxid.put(sourceid, messageid);
			   for(Character neighbor: neighbors) {
				   if(neighbor != neighbor_id) {
					   dl.datalink_receive_from_network(message, message.length(), neighbor);
				   }					    
			   }
		   }
		   else {
			   int maxid = map_maxid.get(sourceid);
			   if(messageid > maxid) {
				   map_maxid.put(sourceid, messageid);
				   for(Character neighbor: neighbors) {
					   if(neighbor != neighbor_id) {
						   dl.datalink_receive_from_network(message, message.length(), neighbor);
					   }					    
				   }
			   }
		   }
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
		if(message.length() > 1) {
			List<Character> neighbors = new ArrayList<Character>();
		    for(int i = 4; i < args.length; ++i) {
			    neighbors.add(args[i].charAt(0));
		    }
		    DataLink dl = new DataLink(sourceId, destinationId, neighbors);
		    Network nw = new Network(sourceId, destinationId, neighbors);
		    dl.nw = nw;
		    nw.dl = dl;
		    
		    nw.network_receive_from_transport(message, message.length(), destinationId);

		    for (int i=0; i < life; i++) {
		    	dl.datalink_receive_from_channel();
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		   
		}
		else {
			List<Character> neighbors = new ArrayList<Character>();
		    for(int i = 3; i < args.length; ++i) {
			    neighbors.add(args[i].charAt(0));
		    }
		    DataLink dl = new DataLink(sourceId, destinationId, neighbors);
		    Network nw = new Network(sourceId, destinationId, neighbors);
		    dl.nw = nw;
		    nw.dl = dl;
		    
		    for (int i=0; i < life; i++) {
		    	dl.datalink_receive_from_channel();
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }

		}

	}

}
