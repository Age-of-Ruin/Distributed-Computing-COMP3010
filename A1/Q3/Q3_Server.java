// Author: Richard Constantine
// Date: Feb 8, 2018

import java.net.*;
import java.io.*;
import java.util.*;


public class Q3_Server implements Runnable {

    static Hashtable<Integer, Integer> bankInfo = new Hashtable<Integer, Integer>(); // Hash table for bank info
    Socket cliConnection = null;       // socket per client connection

    // Constructor called for every client connection
    Q3_Server(Socket cliSock){
        this.cliConnection = cliSock;
    }

    public void run() {

        BufferedReader inSockStream = null;  // stream used to read from socket
        DataOutputStream outSockStream = null;// stream used to write to socket
	    String cliMsg = "";             // data received will be a string

        // Setup socket streams
        try {
            inSockStream = new BufferedReader(new InputStreamReader(cliConnection.getInputStream()));
            outSockStream = new DataOutputStream(cliConnection.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error - Couldn't create socket input stream - exiting.");
            System.exit(1);
        }

        System.out.println("Client connection established at " + cliConnection.getRemoteSocketAddress());

        // Send welcome message to client
        try {
            outSockStream.writeBytes("Welcome to the Bank!\n");
        } catch (Exception E) {
            System.out.println("Error - Cannot send message due to connection error - client may be down...");
        }

        // Read command from client and process
        try {
            cliMsg = inSockStream.readLine();
            System.out.println("The string received from client was: " + cliMsg);
        } catch (Exception e) {
            System.out.println("Error - cliMsg not received - socket input failed.");
        }

        // Process client request
        processRequest(cliMsg, cliConnection, inSockStream, outSockStream);

        System.out.println("\nClosing client connection...");

        // Send closing message
        try {
            outSockStream.writeBytes("Thanks for visiting the Bank! Please come again.\n");
        } catch (Exception E) {
            System.out.println("Error - Cannot send message due to connection error - client may be down...");
        }

        // Close the client socket and stream
        try {
            inSockStream.close();
            outSockStream.close();
            cliConnection.close();
            System.out.println("Client connection successfully closed.");
        } catch (Exception e) {
            System.out.println("Error - Server couldn't close a socket.");
        }

    } // End of run()

    public static void main(String[] args) {
        
        Socket cliSock = null;       // socket used to accept client
	    ServerSocket ServSock = null;    // server's master socket
	    InetAddress addr = null;     // address of server

	    System.out.println("Server starting...");

	    // Create Socket
	    try {
	        addr = InetAddress.getLocalHost(); // use this host for server
	        ServSock = new ServerSocket(13064,3,addr); // create server socket                      
	    } catch (Exception e) {
            System.out.println("Creation of ServerSocket failed - exiting...");
            System.exit(1);
	    }

        System.out.println("ServerSocket successfully created.");

	    // BEGIN MAIN LOOP:
        // Read strings from the connection and print them until Q is received
	    do {
	        
            System.out.println("Awaiting clt connections...");

            // Accept a connection
	        try {
	            cliSock = ServSock.accept(); // accept a connection from client
	        } catch (Exception e) {
	            System.out.println("Error - Connection accept failed - exiting.");
                System.exit(1);
	        }
            
            // Create new thread for client connection
            new Thread(new Q3_Server(cliSock)).start();

        } while (true);

    } // End of Main

    private synchronized static void processRequest(String cliMsg, Socket cliSock, BufferedReader inSockStream, DataOutputStream outSockStream){
    
        String values = "";
        String args[] = new String[2];
        Integer acctNumber;
        Integer balance;  
        Integer transferRequest;      

        // Parse message
        char cmd = cliMsg.charAt(0);

        if (cliMsg.contains("<"))
            values = cliMsg.substring(cliMsg.indexOf("<")+1, cliMsg.length()-1);
      
        if (values.contains(","))
            args = values.split(",");

        // Determine response to client request
        switch(cmd){   

            case 'C':
                // Server creates and account using supplied account
                // number (starting at $0)
                System.out.println("\nCreating account...");

                // Read account number from client
                acctNumber = -1;
                try {
                    acctNumber = Integer.parseInt(values);
                } catch (Exception e) {
                    System.out.println("Error - Acct number not received or incorrectly formatted.");
                }
                
                // Check that account number is correct and not duplicate
                if (acctNumber >= 0 && !bankInfo.containsKey(acctNumber)) {

                    // Create account at $0
                    bankInfo.put(acctNumber, 0); 

                    // Send confirmation message to client
	                try {
                        outSockStream.writeBytes("Account number " + acctNumber + " successfully created.\n");
                        System.out.println("Account number " + acctNumber + " successfully created.");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }

                } else {
	                try {
                        outSockStream.writeBytes("Error - incorrect or duplicate acct number...\n");
                        System.out.println("Error - incorrect or duplicate acct number...");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                break;

            case 'R':
                // Server retrieves balance associated with that account
                System.out.println("\nRetrieving balance...");

                // Read account number from client
                acctNumber = -1;
                try {
                    acctNumber = Integer.parseInt(values);
                } catch (Exception e) {
                    System.out.println("Acct number not received or incorrectly formatted.");
                }
                
                // Check that account exists
                if (bankInfo.containsKey(acctNumber)) {

                    // Acquire balance from account
                    balance = bankInfo.get(acctNumber);

                    // Send balance to client
	                try {
                        outSockStream.writeBytes("Account number " + acctNumber + " has balance $" + balance + ".\n");
                        System.out.println("Account number " + acctNumber + " has balance $" + balance + ".\n");
                    } catch (Exception E) {
                        System.out.println("Cannot send message due to connection error - client may be down...");
                    }

                } else {
	                try {
                        outSockStream.writeBytes("Error - incorrect acct number...\n");
                        System.out.println("Error - incorrect acct number...");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                break;

            case 'D':
                // Server deposits requested amount into bank account
                System.out.println("\nDepositing money...");

                // Read account number and deposit amount from client
                acctNumber = -1;
                transferRequest = -1;
                try {
                    acctNumber = Integer.parseInt(args[0]);
                    transferRequest = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Acct number or transfer amount not formatted properly.");
                }
                
                // Check that bank account exists that transfer request is rational/sane
                if (bankInfo.containsKey(acctNumber) && transferRequest >= 0) {

                    // Adjust balance
                    balance = bankInfo.get(acctNumber);
                    int newBalance = balance + transferRequest;
                    bankInfo.put(acctNumber, newBalance);

                    // Send confirmation message to client
	                try {
                        outSockStream.writeBytes("Account number " + acctNumber + " went from balance $" +
                        balance + " to a new balance of $" + newBalance + ".\n");
                        
                        System.out.println("Account number " + acctNumber + " went from balance $" +
                        balance + " to a new balance of $" + newBalance + ".\n");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }

                } else {
	                try {
                        outSockStream.writeBytes("Error - incorrect acct number or deposit amount...\n");
                        System.out.println("Error - incorrect acct number or deposit amount...");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                break;

            case 'W':
                // Server withdraws money from bank account
                System.out.println("\nWithdrawing money...");

                // Read account number and deposit amount from client
                acctNumber = -1;
                transferRequest = -1;
                try {
                    acctNumber = Integer.parseInt(args[0]);
                    transferRequest = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Error - Acct number or transfer amount not formatted properly.");    
                }
                
                // Check that bank account exists that transfer request is rational/sane
                if (bankInfo.containsKey(acctNumber) && transferRequest >= 0) {

                    // Adjust balance (or send error message if newBalance < 0)
                    balance = bankInfo.get(acctNumber);

                    if (balance - transferRequest >= 0){
                        int newBalance = balance - transferRequest;
                        bankInfo.put(acctNumber, newBalance);

                        // Send confirmation message to client
	                    try {
                            outSockStream.writeBytes("Account number " + acctNumber + " went from balance $" +
                            balance + " to a new balance of $" + newBalance + ".\n");
                            
                            System.out.println("Account number " + acctNumber + " went from balance $" +
                            balance + " to a new balance of $" + newBalance + ".\n");
                        } catch (Exception E) {
                            System.out.println("Error - Cannot send message due to connection error - client may be down...");
                        }

                    } else {
	                    try {
                            outSockStream.writeBytes("Error - Account number " + acctNumber + " only has balance $" +
                            balance + " - withdrawing $" + transferRequest + " will bring the balance below $0.\n");
                            
                            System.out.println("Error - Account number " + acctNumber + " only has balance $" +
                            balance + " - withdrawing $" + transferRequest + " will bring the balance below $0.\n");
                        } catch (Exception E) {
                            System.out.println("Error - Cannot send message due to connection error - client may be down...");
                        }           
                    }

                } else {
	                try {
                        outSockStream.writeBytes("Error - incorrect acct number or withdraw amount...\n");
                        System.out.println("Error - incorrect acct number or withdraw amount...");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                break;
            
            default:
                System.out.println("\nUnknown command entered...");
                break;
        }

    } // End of processRequest

} // End of Server
