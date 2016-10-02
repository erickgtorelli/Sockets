import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
    
public class Client
{
 
    private static Socket socket;
    private static int windowSize;     //Size of the window
    private static String file;        //File content to transmit
    private static int intermediaryPort;//Number of the intermediary port
    private static boolean mode;       //0 normal, 1 debug
    private static int timeout;        //Timeout time in ms 
    private static int segmentCounter; //Counter of segments
    private static int[][] ack;         //time-segment-ack received //0=no ack received, 1=ack received, 2=timeout
 
    public static void main(String args[])
    {
        try
        {
            String host = "localhost";
            int port_intermediary = 25002;
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port_intermediary);
            
            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
 
            String sendMessage = "Client " + "\n";
            bw.write(sendMessage);
            bw.flush();
            System.out.println("Message sent to the Intermediary : "+sendMessage);
            
            
             //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            System.out.println("Message received from the Intermediary : " + message);
            
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
                socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    static void inicialization(int windowSize,String file,int intermediaryPort, boolean mode, int timeout){
        this.windowSize=windowSize;     
        this.file=file;
        this.intermediaryPort=intermediaryPort;
        this.mode=mode;
        this.timeout=timeout;        
        segmentCounter=0; 
        ack = new int[windowSize][3];         
    }

    
    /* REQ: path of file, encoding of file
    *  MOD: -
    *  RET: String with all chars of file
    */
    static String readFile(String path, Charset encoding) throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);    //std enconding = StandardCharsets.UTF_8
    }

    /* REQ: character to be encoded
    *  MOD: -
    *  RET: character encoded
    */
    static String createSegment(char character){
        
        String ret = (segmentCounter+":"+character);
        segmentCounter++;
        return ret;
    }

    static void newWindow(){
        for( int i=0; i<windowSize; i++){
            ack[i][0]=0;
            ack[i][1]=segmentCounter+i;
            ack[i][2]=0;
        }
    }

    static void setTimeoutTime(int seg){
        ack[segmentCounter-seg][0]=System.currentTimeMillis()+timeout;
    }

    static void setAck(int seg){
        ack[seg][2]=1;
    }

    static boolean checkWindowAck(){
        while (i<windowSize && ack[i][2] == 1){
            i++;
        }
        if (i == windowSize){
            return true;
        }
        else{
            return false
        }
    }

    static void windowTimeout(){
        for( int i=0; i<windowSize; i++){
            if (ack[i][0] > System.currentTimeMillis()){
                ack[i][2]=2;
            }
        }
    }

    static void newSlidingWindow(){
        newWindow();
        for(int i=0; i<windowSize;i++){
            setTimeoutTime(i);
            createSegment(file.charAt(segmentCounter));
            //send string here
        }
    }

}