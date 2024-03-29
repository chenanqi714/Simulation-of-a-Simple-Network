import java.io.*;
import java.util.*;
import java.lang.*;

public class Writer {

    int count;
    String channel;
    
    /** Creates a new instance of writer */
    public Writer(String channel) {
    
        try
        {
        	boolean fileExists = false;
        	this.channel = channel;
	        String pathname = channel;

	        
	        File file = new File(".");
        	for (File f : file.listFiles()) {
        		String fname = f.getName();
        		if(fname.equals(channel)) {
        			fileExists = true;
        		}
        	}
        	if(!fileExists) {        		
        		File SharedFile = new File(pathname);
                FileWriter SFile = new FileWriter(SharedFile);
                SFile.close();
        	}
        	               
        	count = 0;
        }
        catch(Exception e)
        {
            System.out.println(e + "in writer");
        }
    }
    
    /*========= read output files and write into input files ==========*/
    void writeFile(String message)
    {
        try
        {                              
                    String filePath = channel;
                    BufferedWriter WriteFile = new BufferedWriter(new FileWriter(filePath,true));
                    WriteFile.write(message);
                    WriteFile.close();                       
                    count++;
                   
        }
        catch(Exception e)
        {
            System.out.println(e + " in writeFile()");
        }
    }
                            
}
