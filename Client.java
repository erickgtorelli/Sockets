/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication5;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
    

/**
 *
 * @author DaniloJ
 */
public class JavaApplication5 {

    private static Socket socket;
    final int windowSize;          //Size of the window
    final String file;             //File content to transmit
    final int intermediaryPort;    //Number of the intermediary port
    final boolean mode;            //0 normal, 1 debug
    final double timeout;          //Timeout time in s 
    
    private static int segmentCounter;      //Counter of segments
    private static int[] windowSegments;    //segment to be sent
    private static double[] windowTime;     //next timeout
    
    public static void main(String args[]) throws IOException
    {
        JavaApplication5 var = new JavaApplication5(10,"archivo.txt",10,false,10);
        for(int i=0; i<=var.file.length()/var.windowSize; i++){
            var.newWindow();
            for(int x=0;x<var.windowSize;x++){
                //System.out.println(var.createSegment(x));//aqui se envia el segmento
                var.setTimeoutToSegment(x);
            }
            while(var.selectiveRepeat()){
                //recibir
            }
        }
        
    }
    
    public JavaApplication5(int windowSize,String path,int intermediaryPort, boolean mode, int timeout) throws IOException{
        this.windowSize=windowSize;     
        this.file=readFile(path,StandardCharsets.UTF_8);
        this.intermediaryPort=intermediaryPort;
        this.mode=mode;
        this.timeout=timeout;        
        segmentCounter=0; 
        windowTime = new double[windowSize];  
        windowSegments= new int[windowSize];
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

    /* REQ: character to be encoded
    *  MOD: -
    *  RET: character encoded
    */
    public String createSegment(int var){
        String ret = "";
        if(windowSegments[var]!=-1){
            char character = file.charAt(windowSegments[var]);
            ret = (windowSegments[var]+":"+character);
        }
        return ret;
    }

    public void newWindow(){
        int i=0;
        while(segmentCounter<file.length() && i<windowSize){
            windowSegments[i]=segmentCounter;
            windowTime[i]=0;
            segmentCounter++;
            i++;
        }
        if(segmentCounter>=file.length()){
            for(int x=i;x<windowSize;x++){
                windowSegments[x]=-1;
                windowTime[x]=-1;
            }
        }
    }

    public void setTimeoutToSegment(int seg){
        windowTime[seg]=System.currentTimeMillis()+timeout;
    }

    public void setAck(int seg){
        windowTime[seg]=-1;
    }

    public boolean selectiveRepeat(){
        boolean ack=false;
        for(int x=0;x<windowSize;x++){
                if(windowTime[x]!=-1){
                    if(windowTime[x]>System.currentTimeMillis()){
                        //System.out.println(var.createSegment(x));//aqui se reenvia el segmento
                        setTimeoutToSegment(x);//se reprograma el timeout
                    }
                    ack = true;
                }   
            }
        return ack;
    }
}