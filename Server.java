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
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
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
        guiArguments();
    }
    
    
    public static void startServer(int port2, int windowSize, boolean mode) 
    {
        Server server = new Server();
        Util tools = new Util();
        try
        {
            int port = port2;
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
                    if(mode){
                        System.out.print("Contenido del paquete recibido: "+received.getPackageContent());
                        System.out.print("Enviando ACK del segmento: "+received.getPackageSec());
                    }
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
    
    /*
    * Ask for the initial values of intermediary
    * If everthing is ok run the StartIntermediary
    */
    private static void guiArguments(){
       //arguments
        int portClient, windowSize;
        //enter fields
        JTextField portClientTextField = new JTextField(5);
        JTextField windowSizeTextField = new JTextField(5);
        //panel initialization 
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Port:"));
        myPanel.add(portClientTextField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("WindowSize:"));
        myPanel.add(windowSizeTextField);
        JCheckBox mode = new JCheckBox("Debug mode");
        myPanel.add(mode);
  

        //value Checking 
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter the initial values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                portClient = Integer.parseInt(portClientTextField.getText());
                windowSize = Integer.parseInt(windowSizeTextField.getText());
                //check for positive values
                if (portClient > 0 && windowSize >= 0) { 
                    startServer(portClient,windowSize,mode.isSelected());
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid initial values", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.lang.NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid initial values", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
    }
}


