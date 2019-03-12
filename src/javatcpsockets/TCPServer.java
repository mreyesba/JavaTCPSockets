/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JGUTIERRGARC
 */

import java.net.*;
import java.io.*;
import java.util.Random;


public class TCPServer {
    private static Random rn = new Random();
    
    public static void main (String args[]) {
	try{
		int serverPort = 7896; 
		ServerSocket listenSocket = new ServerSocket(serverPort);
		while(true) {
			//System.out.println("Waiting for messages..."); 
                        Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
			Connection c = new Connection(clientSocket);
                        c.sendMonster(rn.nextInt(9) + 1);
                        c.start();
		}
	} catch(IOException e) {System.out.println("Listen :"+ e.getMessage());}
    }
   
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
	    try {
		clientSocket = aClientSocket;
		in = new DataInputStream(clientSocket.getInputStream());
		out =new DataOutputStream(clientSocket.getOutputStream());
	     } catch(IOException e)  {System.out.println("Connection:"+e.getMessage());}
	}
        
        @Override
	public void run(){
	    try {		
                // an echo server
		String data = in.readUTF();	     
                System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress() + " is " + data);
		//out.writeUTF(data);
	    } 
            catch(EOFException e) {
                System.out.println("EOF:"+e.getMessage());
	    } 
            catch(IOException e) {
                System.out.println("IO:"+e.getMessage());
	    } finally {
                try {
                    clientSocket.close();
                } catch (IOException e){
                    System.out.println(e);
                }
            }
        }
        
        public void sendMonster(int n){
            MulticastSocket s =null;
            try {  
                InetAddress group = InetAddress.getByName("228.5.6.8"); // destination multicast group 
	    	s = new MulticastSocket(6789);
	   	s.joinGroup(group); 
                //s.setTimeToLive(10);
                System.out.println("Messages' TTL (Time-To-Live): "+ s.getTimeToLive());
                String myMessage= n + "";
                byte [] m = myMessage.getBytes();
	    	DatagramPacket messageOut = 
			new DatagramPacket(m, m.length, group, 6789);
	    	s.send(messageOut);

	    	s.leaveGroup(group);		
 	    }
            catch (SocketException e){
                System.out.println("Socket: " + e.getMessage());
            }
            catch (IOException e){
                System.out.println("IO: " + e.getMessage());
            }
            finally {
               if(s != null) s.close();
           }
        }
}


