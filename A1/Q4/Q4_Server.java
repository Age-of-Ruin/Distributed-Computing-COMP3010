// Author: Richard Constantine
// Date: Feb 8, 2018

import java.net.*;
import java.io.*;
import java.util.*;


public class Q4_Server implements Runnable {

    static Hashtable<Integer, Integer> bankInfo = new Hashtable<Integer, Integer>(); // Hash table for bank info
    DatagramPacket pkt;

    // Constructor called for every client packet
    Q4_Server(DatagramPacket packet){
        this.pkt = packet;
    }

    public void run() {

        String cliMsg = new String(pkt.getData(), 0, pkt.getLength());
        InetAddress addr = pkt.getAddress();
        int portNumber = pkt.getPort();
        DatagramSocket cliSock = null;

	    try {
	        cliSock = new DatagramSocket();
	    } catch (Exception e) {
	        System.out.println("Creation of DataGram Socket failed.");
	        System.exit(1);
	    }

        System.out.println("The message received was: " + cliMsg);

        // Process client request
        processRequest(cliMsg, cliSock, addr, portNumber);
    }

    public static void main(String[] args) {

	    DatagramSocket servSock = null;  	// server's master socket
	    DatagramPacket packet = null;	    // the datagram packet
        byte[] buf = new byte[1024];        // buffer used for DG packet 

	    System.out.println("Server starting.");

	    // Create Socket
	    try {
	        servSock = new DatagramSocket(13064);
	    } catch (Exception e) {
	        System.out.println("Creation of DataGram Socket failed.");
	        System.exit(1);
	    }

	    // Read packets from the socket and print them until 
	    do {
	       // Receive a datagram and extract the client's message
            try {
                packet = new DatagramPacket(buf, buf.length);
                servSock.receive(packet);
               
            } catch (Exception e) {
                System.err.println("LookupServer: socket receive failed.");
                System.exit(1);
            }

            // Create thread to handle client request
            new Thread(new Q4_Server(packet)).start();

        } while (true);

    } // End of Main

    private synchronized static void processRequest(String cliMsg, DatagramSocket cliSock, InetAddress addr, int portNumber){
        
        String values = "";
        String args[] = new String[2];
        String response = "";
        DatagramPacket packet;
        byte[] buf;
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
                        response = "Account number " + acctNumber + " successfully created.";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }

                } else {
                    try {
                        response = "Error - incorrect or duplicate acct number...";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
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
                        response = "Account number " + acctNumber + " has balance $" + balance + ".";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
                    } catch (Exception E) {
                        System.out.println("Cannot send message due to connection error - client may be down...");
                    }

                } else {
	                try {
                        response = "Error - incorrect acct number...";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
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
                        response = "Account number " + acctNumber + " went from balance $" +
                        balance + " to a new balance of $" + newBalance + ".";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }

                } else {
	                try {
                        response = "Error - incorrect acct number or deposit amount...";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
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
                            response = "Account number " + acctNumber + " went from balance $" +
                            balance + " to a new balance of $" + newBalance + ".";
                            buf = response.getBytes();
                            packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                            cliSock.send(packet);
                            System.out.println(response);
                        } catch (Exception E) {
                            System.out.println("Error - Cannot send message due to connection error - client may be down...");
                        }

                    } else {
	                    try {
                            response = "Error - Account number " + acctNumber + " only has balance $" +
                            balance + " - withdrawing $" + transferRequest + " will bring the balance below $0.";
                            buf = response.getBytes();
                            packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                            cliSock.send(packet);
                            System.out.println(response);
                        } catch (Exception E) {
                            System.out.println("Error - Cannot send message due to connection error - client may be down...");
                        }           
                    }

                } else {
	                try {
                        response = "Error - incorrect acct number or withdraw amount...";
                        buf = response.getBytes();
                        packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                        cliSock.send(packet);
                        System.out.println(response);
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                break;
            
            default:
                try {
                    response = "Error - incorrect acct selection entered...";
                    buf = response.getBytes();
                    packet = new DatagramPacket(buf, buf.length, addr, portNumber);
                    cliSock.send(packet);
                    System.out.println(response);
                System.out.println("Error - incorrect acct selection entered...");
                } catch (Exception E) {
                    System.out.println("Error - Cannot send message due to connection error - client may be down...");
                }

                break;
                
                case 'E':
                    // Ignore kill code from client
                    System.out.println("Client has exited.");
                    break;

        }

    } // End of processRequest
} // End of Server
