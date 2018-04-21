import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataLink {
	String message;
	List<Character> neighbors;
    int seqNum;
    int channelNum;
    int numOfChannel;
    char nodeid;
    char start;
    char end;
    char esc;
    
    public DataLink(char nodeid, String message, List<Character> neighbors) {
    	this.message = message;
    	this.neighbors = neighbors;
    	this.nodeid = nodeid;
    	seqNum = 0;
    	channelNum = 0;
    	numOfChannel = 2;
    	start = 'F';
    	end = 'E';
    	esc = 'X';
    }
    
    public void datalink_receive_from_network(String message, int len, char next_hop){
    	StringBuilder builder = new StringBuilder();
    	int x = channelNum;
    	int y = seqNum;
    	channelNum = (1+channelNum)%numOfChannel;
    	seqNum++;
    	builder.append(start);
    	builder.append("data "+x+" "+y+" <");
    	for(int i = 0; i < len; ++i) {
    		builder.append(message.charAt(i));
    		if(message.charAt(i) == end || message.charAt(i) == start) {
    			builder.append(esc);
    		}
    		if(message.charAt(i) == esc) {
    			builder.append(esc);
    		}
    	}
    	builder.append(">E");
    	Writer wtr = new Writer("from"+nodeid+"to"+next_hop);
    	wtr.writeFile(builder.toString());
    }
    
    public void datalink_receive_from_channel(){
    	File file = new File(".");
    	Writer wtr = new Writer("node"+nodeid+"received");
	    for (File f : file.listFiles()) {
	    	String fname = f.getName();
	        if(fname.charAt(fname.length() - 1) == nodeid) {
	        	Reader Rdr = new Reader(fname, "whatread"); 
	        	String frame =  Rdr.readFrame();
	        	wtr.writeFile(processFrame(frame));
	        }
	    }
    }
    
    public String processFrame(String frame) {
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
    		if(s[0].equals("data")) {
    			int startIndex = st.indexOf('<');
    			int endIndex = st.indexOf('>');
    			st = st.substring(startIndex+1, endIndex);
    		}
    	}
    	return st;
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
		
		    DataLink dl = new DataLink(sourceId, message, neighbors);
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
		    System.out.println("Hello");
		    DataLink dl = new DataLink(sourceId, "", neighbors);
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
