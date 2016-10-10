

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.Socket;
import java.net.InetAddress;
    

/**
 *
 * @author DaniloJ
 */
public class Client{

    private static Socket socket;
    final int windowSize;          //Size of the window
    final String file;             //File content to transmit
    final int intermediaryPort;    //Number of the intermediary port
    final boolean mode;            //0 normal, 1 debug
    final double timeout;          //Timeout time in s 
    
    private static int segmentCounter;      //Counter of segments
    private static int[] windowSegments;    //segment to be sent
    private static double[] windowTime;     //next timeout
    private final Util util;
    public int resend;
    
    public static void main(String args[]) throws IOException
    {
        try
        {
            Client var = new Client(10, "archivo.txt", 10, false, 1000);
            boolean finishedFile = false;
            boolean allAck= false;
            while (!finishedFile || !allAck) {
                var.listenForAck();
                allAck = var.selectiveRepeat();
                if (var.windowTime[var.windowSize-1] == -1 && !finishedFile) {
                    finishedFile = var.newWindow();
                }
            }
            var.finished();
            System.out.println("Resent packages: "+ var.resend);
        }
        catch (Exception exception)
            {
                exception.printStackTrace();
            }

    }
    public Client(int windowSize,String path,int intermediaryPort, boolean mode, int timeout) throws IOException{
        resend=0;
        util = new Util();
        this.windowSize=windowSize;     
        this.file=readFile(path,StandardCharsets.UTF_8);
        this.intermediaryPort=intermediaryPort;
        this.mode=mode;
        this.timeout=timeout;        
        segmentCounter=0; 
        windowTime = new double[windowSize];  
        windowSegments= new int[windowSize];
        for(int i = 0; i<this.windowSize; i++){
            windowTime[i] = 0;
            windowSegments[i] = segmentCounter;
            segmentCounter++;
        }
        int port = 25002;
        String host = "localhost";
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
         util.sendPackage(socket, new Package("test"));
    }
    
    public void listenForAck(){
        Package p = util.receivePackage(socket);
        System.out.println(p.getPackage());
        int segment = p.getPackageSec();
        System.out.println("ACK: " + segment);
        boolean found = false;
        int var = 0;
        while(!found && var<windowSize){
            if(segmentAt(var)==segment){
                found = true;
                setAck(var);
            }
            var++;
        }
        if(var>=windowSize){
            System.out.print("ACK no encontrado");
        }
    }
    
    public void finished(){
        util.sendPackage(socket, new Package(-1,' '));
        double time = System.currentTimeMillis()+timeout;
        boolean received = false;
        while(!received){
            Package p = util.receivePackage(socket);
            if (" ".equals(p.getPackageContent())){
                received = true;
            }
            if(time > System.currentTimeMillis()){
                util.sendPackage(socket, new Package(-1,' '));
            }
        }
    }
    
    public int segmentAt(int x){
        return windowSegments[x];
    }
    
    /* REQ: path of file, encoding of file
    *  MOD: -
    *  RET: String with all chars of file
    */
    public String readFile(String path, Charset encoding) throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);    //std enconding = StandardCharsets.UTF_8
    }

    /* REQ: -
    *  MOD: Move window by one segment at once
    *  RET: Boolean telling if all file has been set into window
    */
    public boolean newWindow() {
        boolean finished = true;
        if (segmentCounter < file.length()) {
            for (int i = 0; i < windowSize-1; i++) {
                windowTime[i] = windowTime[i + 1];
                windowSegments[i] = windowSegments[i + 1];
            }
            windowSegments[0] = segmentCounter;
            windowTime[0] = 0;
            segmentCounter++;
            finished = false;
        }
        /*System.out.print("Nueva ventana: ");
        for(int i= 0; i<windowSize;i++){
            System.out.print(windowSegments[i]+" ");
        }
        System.out.print("Ventana tiempo: ");
        for(int i= 0; i<windowSize;i++){
            System.out.print(windowTime[i]+" ");
        }
        System.out.println();*/
        return finished;
    }

    /* REQ: Positive integer between 0 and windowSize
    *  MOD: Timeout of segment passed by parameter
    *  RET: -
    */
    public void setTimeoutToSegment(int seg){
        windowTime[seg]=System.currentTimeMillis()+timeout;
    }

    /* REQ: Positive integer between 0 and windowSize
    *  MOD: Set the ack of the segment passed by parameter
    *  RET: -
    */
    public void setAck(int seg){
        windowTime[seg]=-1;
    }

    /* REQ: -
    *  MOD: Sent all pending segments of the window, set their timeout out and resend expired segments with a new timeout
    *  RET: 
    */
    public boolean selectiveRepeat() {
        boolean allAck = true;
        for (int x = 0; x < windowSize; x++) {
            if (windowTime[x] != -1) {
                allAck = false;
                if (windowTime[x] > System.currentTimeMillis()){
                    resend++;
                    util.sendPackage(socket, new Package(windowSegments[x],file.charAt(windowSegments[x])));
                    setTimeoutToSegment(x);//se reprograma/programa el timeout
                    System.out.println("Renviando: " + windowSegments[x]);
                }
                if (windowTime[x]==0) {
                    util.sendPackage(socket, new Package(windowSegments[x],file.charAt(windowSegments[x])));
                    setTimeoutToSegment(x);//se reprograma/programa el timeout
                    System.out.println("Enviando: " + windowSegments[x]);
                }
            }
        }
        return allAck;
    }
    
}