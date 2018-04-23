import java.util.HashMap;
import java.util.List;

public class Network {
	
	List<Character> neighbors;
    char nodeid;
    char destinationid;
    int messageid;
    HashMap<Character, Integer> map_maxid;
    DataLink dl;
    Transport tp;
    
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
    	builder.append(dest);
    	builder.append(messageid/10);
    	builder.append(messageid%10);    	
    	builder.append(len-1);
    	builder.append(message);
    	messageid++;
    	
    	for(Character neighbor: neighbors) {
		    dl.datalink_receive_from_network(builder.toString(), builder.length(), neighbor);
	    }
    }
    
   public void network_receive_from_datalink(String message, char neighbor_id) {
	   char sourceid = message.charAt(0);
	   char destinationid = message.charAt(1);
	   int messageid = Integer.parseInt(message.substring(2, 4));
	   int len = Integer.parseInt(message.substring(4, 5))+1;
	   String mesg = message.substring(5, 5+len);
	   if(destinationid == nodeid) {
		   tp.transport_receive_from_network(mesg, mesg.length(), sourceid);
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

}
