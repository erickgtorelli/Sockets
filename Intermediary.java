import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;

/*
* This is a multi-thread class
* One thread will handle the packages from the client to the server.
* The second one will handle the packages from the server to the client
*
*/
public class Intermediary implements Runnable{
    private Thread t;
    private String threadName;
	private static Socket socket_with_client;
 	private static Socket socket_with_server;
    private ServerSocket intermediarySocket;
    Util tools = new Util();
    String message_from_client;
    String message_received;

    Intermediary(String name)
    {
        threadName = name;

        System.out.println("Creating " +  threadName);
        if(threadName=="FromServer"){
                try
                {
                    //Server_intermediary Init
                    int port_intermediary = 25002;
                    intermediarySocket = new ServerSocket(port_intermediary);
                    System.out.println("Intermediary Started and listening to the port " + port_intermediary);
                   //Listening the Server
                    String host = "localhost";
                    int port_server = 25001;
                    InetAddress address = InetAddress.getByName(host);
                    socket_with_server = new Socket(address, port_server);
                    //Socket with client
                    socket_with_client = intermediarySocket.accept();
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
        }
    }  

    @Override
    public void run(){
        try
        {
         if(threadName == "FromClient")
         {  
            while(true)
            {
                
                     //Receiving message from client
                     Package received = tools.receivePackage(socket_with_client);
                     message_from_client = received.getPackage();
                     System.out.println("Message received from client is "+ message_from_client);
                     
                 
                     //Send the message to the server
                     String sendMessage = message_from_client + "Intermediary \n";
                     Package sending = new Package(sendMessage);
                     tools.sendPackage(socket_with_server,sending);
                     System.out.println("Message sent to the server : "+ sendMessage);
                     
            }
         }
         else if(threadName=="FromServer"){
            
            while(true)
            {
                
                    //Get the return message from the server
                    InputStream is_s = socket_with_server.getInputStream();
                    InputStreamReader isr_s = new InputStreamReader(is_s);
                    BufferedReader br_s = new BufferedReader(isr_s);
                    message_received = br_s.readLine();
                    System.out.println("Message received from the server : " + message_received);
                    
                
                 //Modifying the message from server 
                 message_received = message_received + "Intermediary \n";
               
                    //Sending the answer from the server back to the client
                    OutputStream os_s = socket_with_client.getOutputStream();
                    OutputStreamWriter osw_s = new OutputStreamWriter(os_s);
                    BufferedWriter bw_s = new BufferedWriter(osw_s);
                    bw_s.write(message_received);
                    System.out.println("Message from server sent to the client is "+ message_received);
                    bw_s.flush();
                    ;
                
            }
        }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                socket_with_client.close();
                socket_with_server.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }  

    public void start () {
       System.out.println("Starting " +  threadName );
       if (t == null) {
          t = new Thread (this, threadName);
          t.start ();
       }
    }
    public static void main(String args[])
    {
      Intermediary fromServer = new Intermediary("FromServer");
      Intermediary fromClient = new Intermediary("FromClient");

      fromServer.start();
      fromClient.start();
    }
}