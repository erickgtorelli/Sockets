import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

/*
* This class include methods and functions usefull to simplify the code on the project
*/
public class Util{ 

/*
*
* @param Sokect
* @return String received package 
*/
public Package receivePackage(Socket source){
	Package received = new Package("empty");
	try
	{
	 	InputStream is = source.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String message_from_source = br.readLine();
	    received.setPackage(message_from_source);
    
    }
    catch (Exception exception)
    {
        exception.printStackTrace();
    }
    System.out.println("Message received");
    return received;
}

public void sendPackage(Socket destination,Package sending){
	try
	{
		OutputStream os = destination.getOutputStream();
	    OutputStreamWriter osw = new OutputStreamWriter(os);
	    BufferedWriter bw = new BufferedWriter(osw);
	    String sendMessage = sending.getPackage() + "\n";
	    bw.write(sendMessage);
	    bw.flush();
    }
    catch (Exception exception)
    {
        exception.printStackTrace();
    }

    System.out.println("Message sent");
 
}


 public static void main(String args[])
    {
    	System.out.println("test");
    }


}

