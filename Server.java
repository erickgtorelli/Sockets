import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;
 
public class Server
{
    private ArrayList<char> recivedMessage = new ArrayList<char>();
    private static Socket socket;
    Util tools = new Util();
    boolean fileCompleted = false;
    public static void main(String[] args)
    {
        try
        {
 
            int port = 25001;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port " + port);
 
            //Server is running always. This is done using this while(true) loop
            while(!fileCompleted)
            {

                //Reading the message from the client
                socket = serverSocket.accept();
                Package received = tools.receivePackage(socket);
                //fileCompleted ? 
                if(reviced.getPackage().equals("finish")){
                    fileCompleted = true;
                }
                //file don't compleated, continue
                else{
                    //transfer the recived content to the ArrayList
                    recivedMessage.add(recived.getPackageSec(),recived.getPackageContent());
                    //Returning Message                
                    //Sending the response back to the client.
                    tools.sendPackage(socket,recived);
                }
            }
            writeIntoFile(recivedMessage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
      
    }

    public void writeIntoFile(ArrayList<Character> list) throws FileNotFoundException{
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list)
        {
            builder.append(ch);
        }
        try(  PrintWriter out = new PrintWriter( "filename.txt" )  ){
            out.println( builder.toString()); 
        }
    }
}


