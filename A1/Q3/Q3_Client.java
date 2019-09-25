// Author: Richard Constantine
// Date: Feb 8, 2018

import java.net.*;
import java.io.*;

public class Q3_Client {
    
    static BufferedReader inSockStream = null;   // stream used to read from socket
    static DataOutputStream outSockStream = null;// stream used to write to socket
    static Socket cliSock = null;                // client's socket
    static InetAddress addr = null;              // addr of server (local host for now)

    public static void main(String[] args) {

	    BufferedReader inReader = null;    // buffer input stream
        String input = "";                 // used to hold user input        

	    System.out.println("Client starting...");

	    // Set up terminal/keyboard input 
	    try {
	        inReader = new BufferedReader(new InputStreamReader(System.in));
	    } catch (Exception e) {
	        System.out.println("Failed to set up terminal.");
	        System.exit(1);
	    }

        // MAIN LOOP:
	    // Read and send strings over the socket until 'E' or 'Q' is entered
	    do {
            System.out.println("\nPlease enter command: !-connect, C-create, R-retrieve, " + 
            "D-deposit, W-withdraw, E-quit client");  
            try {         
                input = inReader.readLine(); // Read user input
            } catch (Exception e){
                System.out.println("Error reading input.");
            }

            processInput(input);

	    } while (!input.equals("E")) ;

        System.out.println("Quitting client...");   

	    // Close the streams, socket and terminal input
	    try {
	        inReader.close();
            inSockStream.close();
	        outSockStream.close();
	        cliSock.close();
	    } catch (Exception e) {
	        System.out.println("Client couldn't close socket - possibly already closed.");
	    }

	    System.out.println("Client finished.");
 
    } // End of Main

    private static void processInput(String input){

        String serverMsg = "";
        String cliMsg = "";
        char cmd;

        // Parse user command
        if (input.length() < 1) {
            System.out.println("\nIncorrect Entry - please try again...");       
            return;
        }

        cmd = input.charAt(0);
        cliMsg = input;
      
        // Choose appropriate command
        switch(cmd){   
   
            case '!':
                // Connect to server   
                // Create socket    
                System.out.println("\nConnecting...");

                // Create client socket
	            try {
                    addr = InetAddress.getLocalHost();
	                cliSock = new Socket(addr,13064);
	            } catch (Exception e) {
	                System.out.println("Creation of client's Socket failed.");
	            }

                // Set up socket streams
	            try {
	                inSockStream = new BufferedReader(new InputStreamReader(cliSock.getInputStream()));
	                outSockStream = new DataOutputStream(cliSock.getOutputStream());
	            } catch (Exception e) {
	                System.out.println("Socket output stream failed.");
	            }

                // Read welcome message from server
                try {
                    serverMsg = inSockStream.readLine();
                    System.out.println(serverMsg);
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                }

                return;

            case 'C':
                // Tell server to create account using givent account number
                System.out.println("\nCreating account...");

                // Send client message
	            try {
                    outSockStream.writeBytes(cliMsg + "\n");
                } catch (Exception E) {
                    System.out.println("Cannot send message due to connection error - server may be down...");
                    return;
                }

                // Read message from server
                try {
                    serverMsg = inSockStream.readLine();
                    System.out.println(serverMsg);
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                    return;
                }

                break;

            case 'R':
                // Tell server to retrieve balance
                System.out.println("\nRetrieving balance...");

                // Send client message
	            try {
                    outSockStream.writeBytes(cliMsg + "\n");
                } catch (Exception E) {
                    System.out.println("Cannot send message due to connection error - server may be down...");
                    return;
                }

                // Read message from server
                try {
                    serverMsg = inSockStream.readLine();
                    System.out.println(serverMsg);
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                    return;
                }

                break;

            case 'D':
                // Tell server to add specified money to given acctNumber
                System.out.println("\nDepositing money...");
                
                // Send client message
	            try {
                    outSockStream.writeBytes(cliMsg + "\n");
                } catch (Exception E) {
                    System.out.println("Cannot send message due to connection error - server may be down...");
                    return;
                }

                // Read message from server
                try {
                    serverMsg = inSockStream.readLine();
                    System.out.println(serverMsg);
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                    return;
                }

                break;

            case 'W':
                // Tell server to add specified money to given acctNumber
                System.out.println("\nWithdrawing money...");
                
                // Send client message
	            try {
                    outSockStream.writeBytes(cliMsg + "\n");
                } catch (Exception E) {
                    System.out.println("Cannot send message due to connection error - server may be down...");
                    return;
                }

                // Read message from server
                try {
                    serverMsg = inSockStream.readLine();
                    System.out.println(serverMsg);
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                    return;
                }

                break;

            // Ignore kill code (shutdown done in main)
            case 'E':
                return;

            default:
                System.out.println("\nUnknown command entered...");
                return;
        }

        // Read closing message from server
        try {
            serverMsg = inSockStream.readLine();
            System.out.println(serverMsg);
        } catch (Exception e) {
            System.out.println("Socket input failed.");
        }

    } // End of processInput

} // End of Client
