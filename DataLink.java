import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataLink {
	int count;
	Network nw;
	List<Character> neighbors;
    char nodeid;
    char destinationid;
    char start;
    char end;
    char esc;
    HashMap<Character, Channel> map_sent;
    HashMap<Character, Channel> map_receive;
    HashMap<String, Reader> map_reader = new HashMap<String, Reader>();
    
    public DataLink(char nodeid, char destinationid, List<Character> neighbors) {
    	this.neighbors = neighbors;
    	this.nodeid = nodeid;
    	this.destinationid = destinationid;
    	map_sent = new HashMap<Character, Channel>();
    	map_receive = new HashMap<Character, Channel>();
    	start = 'F';
    	end = 'E';
    	esc = 'X';
    	count = 0;
    }
    
    public void datalink_receive_from_network(String message, int len, char next_hop){
    	StringBuilder builder = new StringBuilder();
    	Channel c;
    	if(map_sent.containsKey(next_hop)) {
    		c = map_sent.get(next_hop);
    	}
    	else {
    		c = new Channel(2);
    		map_sent.put(next_hop, c);
    	}
    	boolean sent = false;
    	while(!sent) {
    		for(int i = 0; i < c.N; ++i) {
            	if(c.ab[i] == c.sb[i]) {
            		c.sb[i] = (c.sb[i] + 1)%c.N;
            		builder.append(start);
                	builder.append("data "+i+" "+c.sb[i]+" <");
                	for(int j = 0; j < len; ++j) {
                		builder.append(message.charAt(j));
                		if(message.charAt(j) == end || message.charAt(j) == start) {
                			builder.append(esc);
                		}
                		if(message.charAt(j) == esc) {
                			builder.append(esc);
                		}
                	}
                	builder.append(">E");
                    Writer wtr = new Writer("from"+nodeid+"to"+next_hop);
                    wtr.writeFile(builder.toString()); 
                	c.body[i] = builder.toString();
                	System.out.println("Send frame to channel: "+builder.toString());
                	sent = true;
                	break;
            	}
            }
    		if(!sent) {
    			datalink_receive_from_channel();
    		}
    	}
    }
    
    public void datalink_receive_from_channel(){
    	File file = new File(".");
	    for (File f : file.listFiles()) {
	    	String fname = f.getName();
	        if(fname.charAt(fname.length() - 1) == nodeid) {
	        	char fromid = fname.charAt(4);
	        	if(!map_reader.containsKey(fname)) {
	        		map_reader.put(fname, new Reader(fname));
	        	}
	        	Reader Rdr = map_reader.get(fname); 
	        	String frame =  Rdr.readFrame().trim();
	        	if(!frame.isEmpty()) {
	        		System.out.println("Get frame from channel: "+frame);
	        		String message = processFrame(frame, fromid);
	        		if(!message.isEmpty()) {
	        			nw.network_receive_from_datalink(message, fromid);
		        	}
	        	}
	        }
	    }
	    ++count;
	    if(count % 5 == 0) {
	    	//check_timeout(neighbors);
	    }
    }
    
    public void check_timeout(List<Character> neighbors) {
    	for(char neighbor: neighbors) {
        	if(map_sent.containsKey(neighbor)) {
        		Channel c = map_sent.get(neighbor);
        		for(int i = 0; i < c.N; ++i) {
        		    if(c.ab[i] != c.sb[i]) {
                	    Writer wtr = new Writer("from"+nodeid+"to"+neighbor);
                	    wtr.writeFile(c.body[i]); 
        		    }
        		}
        	}
    	}    	
    }
    
    public String processFrame(String frame, char fromid) {
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < frame.length(); ++i) {
    		if((frame.charAt(i) == start || frame.charAt(i) == end) && i < frame.length()-1 && frame.charAt(i+1) == esc) {
    			builder.append(frame.charAt(i));
    		}
    		else if(frame.charAt(i) != start && frame.charAt(i) != end && frame.charAt(i) != esc) {
    			builder.append(frame.charAt(i));
    		}
    		else if(frame.charAt(i) == esc && i < frame.length() -1 && frame.charAt(i+1) == esc) {
    			builder.append(frame.charAt(i));
    		}
    	}
    	String st = builder.toString();
    	String s[] = st.split(" ");
    	if(s.length > 0) {
    		if(s[0].trim().equals("data")) {
    			int startIndex = st.indexOf('<');
    			int endIndex = st.indexOf('>');
    			st = st.substring(startIndex+1, endIndex);
    			Channel c;
    			if(map_receive.containsKey(fromid)) {
    				c = map_receive.get(fromid);
    			}
    			else {
    				c = new Channel(2);
    				map_receive.put(fromid, c);
    			}
    			            	
    			//send ack
    			StringBuilder builder_ack = new StringBuilder();
    			builder_ack.append(start+"ack "+s[1] + " "+s[2]+end);

            	Writer wtr = new Writer("from"+nodeid+"to"+fromid);
                wtr.writeFile(builder_ack.toString()); 
                
    			int b = Integer.parseInt(s[2].trim());
            	int n = Integer.parseInt(s[1].trim());     
            	if(b == c.nb[n]) {
            		
            		c.nb[n] = (c.nb[n] + 1)%2;
            		return st;
            	}
            	
    		}
    		else if(s[0].trim().equals("ack")){// get ack from fromid
    			Channel c = map_sent.get(fromid);
    			int n = Integer.parseInt(s[1].trim());
    			int b = Integer.parseInt(s[2].trim());
    			c.ab[n] = b;
    			//System.out.println("Get ack: n ab sb: "+n+" "+c.ab[n]+" "+c.sb[n]);
    		}
    	}
    	return "";
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
		    for(Character neighbor: neighbors) {
			    dl.datalink_receive_from_network(message, message.length(), neighbor);
		    }
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
		
		// TODO Auto-generated method stub

	}

}
