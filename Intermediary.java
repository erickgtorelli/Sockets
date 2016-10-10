import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.lang.Math;

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
    private int probabilityP;
    private boolean debug;
    BufferedReader br = null;


    Intermediary(String name, int portServer,int portClient,int probabilityP,boolean debug)
    {
        threadName = name;
        this.debug = debug;
        this.probabilityP = probabilityP;
        System.out.println("Creating " +  threadName);
        //Only the thread called FromServer will start the ports and the variables
        if(threadName=="FromServer"){
          
          try
          {
              //Server_intermediary Init
              int port_intermediary = portClient;
              intermediarySocket = new ServerSocket(port_intermediary);
              System.out.println("Intermediary Started and listening to the port " + port_intermediary);
             //Listening the Server
              String host = "localhost";
              int port_server = portServer;
              InetAddress address = InetAddress.getByName(host);
              socket_with_server = new Socket(address, port_server);
              socket_with_server.setSoTimeout(3*1000);
              //Socket with client
              socket_with_client = intermediarySocket.accept();
          }
          catch (Exception exception)
          {
              exception.printStackTrace();
          }
        }
    }  
    //main flow of the intermediary class 
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
                     if(message_from_client != null){
                     System.out.println("Message received from client is "+ message_from_client);
                     
                     //Send the message to the server
                     //IF DEBUG MODE 
                     if(debug){
                        boolean result = askPackageDebug(received.getPackage());
                        if (result) {
                         System.out.println("Sending Package!");
                         tools.sendPackage(socket_with_server,received); 
                        }
                     }
                     // !DEBUG
                     else{
                      
                       int random = (int )(Math.random() * 100 + 1);
                       //Lost of packages simulation with the probablityP 
                       if(random > probabilityP){
                         tools.sendPackage(socket_with_server,received);
                       }
                       else{
                        System.out.println("Package Lost!");
                       }
                      }
                    }
            }
         }
         else if(threadName=="FromServer"){
            while(true)
            {
                
                    //Get the return message from the server
                    Package received = tools.receivePackage(socket_with_server);
                    String message_from_server = received.getPackage();
                    System.out.println("Message received from the server : " + message_from_server);
                    //Sending the answer from the server back to the client
                    tools.sendPackage(socket_with_client,received);              
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

    private boolean askPackageDebug(String pack){
      JPanel panel = new JPanel();
      JCheckBox mode = new JCheckBox("Send the Package");
      panel.add(mode);
      int result = JOptionPane.showConfirmDialog(null, panel,
                pack, JOptionPane.OK_CANCEL_OPTION);

      if (result == JOptionPane.OK_OPTION){
        return mode.isSelected();
      }

      return false;

    }
    /*
    * Ask for the initial values of intermediary
    * If everthing is ok run the StartIntermediary
    */
    private static void guiArguments(){
       //arguments
        int portClient, portServer, probabilityP;
        //enter fields
        JTextField clientPort = new JTextField(5);
        JTextField serverPort = new JTextField(5);
        JTextField probability = new JTextField(5);
        //panel initialization 
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Client Port:"));
        myPanel.add(clientPort);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Server Port:"));
        myPanel.add(serverPort);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Packages lost rate %:"));
        myPanel.add(probability);
        JCheckBox mode = new JCheckBox("Slow execution");
        myPanel.add(mode);
  

        //value Checking 
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter the initial values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                portClient = Integer.parseInt(clientPort.getText());
                portServer = Integer.parseInt(serverPort.getText());
                probabilityP = Integer.parseInt(probability.getText());
                //check for positive values
                if (portClient > 0 && portServer > 0 && probabilityP >= 0) { 
                   if(!(portServer == portClient)){
                    
                      startIntermediary(portClient,portServer,probabilityP,mode.isSelected());
                   }
                   else{
                    JOptionPane.showMessageDialog(null, "The ports for Server and Client must be different", "Error", JOptionPane.ERROR_MESSAGE);
                   }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid initial values", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.lang.NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid initial values", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
    }
    //Start the intermediary class
    private static void  startIntermediary(int portClient,int portServer,int probabilityP,boolean debug)
    {
      Intermediary fromServer = new Intermediary("FromServer",portServer,portClient,probabilityP,debug);
      Intermediary fromClient = new Intermediary("FromClient",portServer,portClient,probabilityP,debug);

      fromServer.start();
      fromClient.start();
    }
    public static void main(String args[])
    {
      guiArguments();
    
    }

}
