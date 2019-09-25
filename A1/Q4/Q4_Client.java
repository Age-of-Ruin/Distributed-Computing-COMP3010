// Author: Richard Constantine
// Date: Feb 8, 2018

import java.net.*;
import java.io.*;

public class Q4_Client {
    
    static DatagramSocket cliSock = null;        // client's socket
    static InetAddress addr = null;              // addr of server (local host for now)
    static DatagramPacket packet = null;         // packet used for transmission

    public static void main(String[] args) {

	    BufferedReader inReader = null;    // buffered version of inStream
        String input = "";                 // used to hold user input        
        byte[] buf;       // buffer used for DG packet
        String servMsg = "";               // holds the response message from server        

	    System.out.println("Client starting...");

	    // create socket and address
	    try {
       		addr = InetAddress.getLocalHost();
       		cliSock = new DatagramSocket(); // create client socket
	    } catch (Exception e) {
       		System.out.println("Creation of client's Socket failed.");
       		System.exit(1);
	    } // end try-catch


	    // Set up terminal input stream
	    
	    try {
	        inReader = new BufferedReader(new InputStreamReader(System.in));
	    } catch (Exception e) {
	        System.out.println("Socket output stream failed.");
	        System.exit(1);
	    }

	    // Read and send client command
	    do {
	        try {
                System.out.println("\nPlease enter command: C-create, R-retrieve, D-deposit, W-withdraw, E-quit client");  
		        input = inReader.readLine();
                buf = new byte[1024];
		        buf = input.getBytes();
		        packet = new DatagramPacket(buf, buf.length, addr, 13064);
		        cliSock.send(packet);

	        } catch (Exception e) {
		        System.out.println("Terminal read or socket output failed.");
		        System.exit(1);
	        }

            // Quit if E is read by keyboard
            if (input.equals("E")) {
                System.out.println("Quitting client...");   

	            // Close the socket and terminal input
	            try {
	                inReader.close();
	                cliSock.close();
	            } catch (Exception e) {
	                System.out.println("Client couldn't close socket - possibly already closed.");
	            }

	            System.out.println("Client finished.");
                System.exit(0);
            }

	        // Receive and extract the servers response
            try {
                buf = new byte[1024];
                packet = new DatagramPacket(buf, buf.length);
                cliSock.receive(packet);
                servMsg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("\n" + servMsg);
            } catch (Exception e) {
                System.err.println("LookupServer: socket receive failed.");
                System.exit(1);
            } 

	    } while (true) ;

    } // main
} // End of Client
