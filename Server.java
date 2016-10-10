import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;
 
public class Server
{
    private ArrayList<Character> receivedMessage = new ArrayList<Character>(1000);
    private static Socket socket;
    
    public boolean fileCompleted = false;
    public Server(){
        for(int i = 0;i<1000;i++){
            receivedMessage.add(i,' ');
        }
    }
    public static void main(String[] args)
    {
        Server server = new Server();
        Util tools = new Util();
        try
        {
 
            int port = 25001;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port " + port);
            socket = serverSocket.accept();
            //Server is running always. This is done using this while(true) loop
            while(!server.fileCompleted)
            {

                //Reading the message from the client
                
                Package received = tools.receivePackage(socket);
                //fileCompleted ? 
                if(received.getPackageSec() == -1){
                    server.fileCompleted = true;
                }
                //file don't compleated, continue
                else{
                    //transfer the received content to the ArrayList
                    System.out.println(received.getPackageContent());
                    System.out.println(received.getPackageSec());
                    server.receivedMessage.add(received.getPackageSec(),received.getPackageContent());
                    //Returning Message                
                    //Sending the response back to the client.
                    tools.sendPackage(socket,received);
                }
            }
            server.writeIntoFile(server.receivedMessage);
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

    public void writeIntoFile(ArrayList<Character> list)throws FileNotFoundException{
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


