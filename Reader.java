import java.io.*;
import java.util.*;
import java.lang.*;

/*
 */
public class Reader {

    int count;
    String channel;
    String whatread;
    char start = 'F';
	char end = 'E';
	char esc = 'X';
    
    /** Creates a new instance of Reader */
    public Reader(String channel) 
    {
    
        try
        {
                    this.channel = channel;
                    //this.whatread = whatread;
        	        //String pathname = whatread;
                    //File SharedFile = new File(pathname);
                    //FileWriter SFile = new FileWriter(SharedFile);
                    //SFile.close();
                    count = 0;
                
            
        }
        catch(Exception e)
        {
            System.out.println(e + "in InitReader");
        }
    }
    
    /*========= read output files and write into input files ==========*/
    void readFile()
    {
        try
        {                              
                    String str = channel;
                    BufferedReader ReadFile = new BufferedReader(new FileReader(str));
                    int temp = 0;
                    while((str = ReadFile.readLine()) != null)
                    {
                        ++temp;
                        if(temp > count) /* new msg */
                        {
                                    String filePath = whatread;
                                    BufferedWriter WriteFile = new BufferedWriter(new FileWriter(filePath,true));
                                    WriteFile.write(str);
                                    WriteFile.write("\n");
                                    WriteFile.close();                       
                        }
                   }
                   count = temp;
                   
        }
        catch(Exception e)
        {
            System.out.println(e + " in readFile()");
        }
    }
    
    public String readFrame()
    {
        try
        {                              
                    String str = channel;
                    BufferedReader ReadFile = new BufferedReader(new FileReader(str));
                    int temp = 0;
                    int c;
                    boolean flag = false;
                    StringBuilder builder = new StringBuilder();
                    while((c = ReadFile.read()) != -1)
                    {
                        ++temp;
                        if(temp > count) /* new msg */
                        {
                            builder.append((char)c);
                        	if((char)c==end){
                            	flag = true;
                            }
                            else if(flag && (char)c==esc) {
                            	flag = false;
                            }
                            else if(flag && (char)c!=esc){
                            	--temp;
                            	break;
                            }
                        }
                   }
                   count = temp;
                   return builder.toString();
                   
        }
        catch(Exception e)
        {
            System.out.println(e + " in readFile()");
            return "";
        }
    }
    
              
           
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            Reader Rdr = new Reader("channel"); 
            for(int i=1;i<10;++i)
            {
                Thread.sleep(10000);
                Rdr.readFile();
            }
        }
        catch(Exception e)
        {
            System.out.println(e + " in reader main()");
        }
    }
}
