// Author: Richard Constantine
// Date: Feb 27, 2018

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class Q2_Server implements Runnable {

    static Connection dbConn = null;    // holds DB connection
    Socket cliConnection = null;       // socket per client connection

    // Constructor called for every client connection
    Q2_Server(Socket cliSock){
        this.cliConnection = cliSock;
    }

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

        // Create DB connection
        String dbName = "";
        String userID = "";
        String passwd = "";
        try {
            dbName = args[0];
            userID = args[1];
            passwd = args[2];
        } catch (Exception ex) {
            System.out.println("Need args: dbName, userID, passwd - exiting...");
            System.exit(1);
        }

        accessDB(dbName, userID, passwd);

        System.out.println("Successfully connected to DB.");

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
            new Thread(new Q2_Server(cliSock)).start();

        } while (true);

    } // End of Main

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

    public static void accessDB(String dbName, String userID, String passwd){
        
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        
        } catch (Exception ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        try {
            String srvr = "127.0.0.1";
            String port = "3306";
            String db = dbName;
            String url = "jdbc:mysql://"+srvr+":"+port+"/"+db;
            String uid = userID;
            String pw = passwd;
            dbConn = DriverManager.getConnection(url, uid, pw);

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }


    }           

    private synchronized static void processRequest(String cliMsg, Socket cliSock, BufferedReader inSockStream, DataOutputStream outSockStream){
    
        String values = "";
        String args[] = new String[2];
        int acctNumber = -1;
        int balance = -1;  
        int newBalance = -1;
        int transferRequest = -1;      

        // Parse message
        char cmd = cliMsg.charAt(0);

        if(cliMsg.length() == 2 || cliMsg.length() == 3)
            cmd = 'X';

        else if (cliMsg.contains("<"))
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
                try {
                    acctNumber = Integer.parseInt(values);
                } catch (Exception e) {
                    System.out.println("Error - Acct number not received or incorrectly formatted.");
                }
                
                // Check that account number is sane
                // Insert into new account DB
                int badInsert = 0;
                if (acctNumber >= 0){
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("INSERT INTO constan7_BANKACCOUNTS VALUES (?, ?)");
                        stmt.setInt(1, acctNumber);
                        stmt.setInt(2, 0);
                        stmt.executeUpdate();
                        outSockStream.writeBytes("Account number " + acctNumber + " successfully created.\n");
                        System.out.println("Account number " + acctNumber + " successfully created.");
                    } catch (Exception E) {
                        System.out.println("Cannot enter account number into DB");
                        badInsert = 1;
                    }
                } else {
                    try {
                        outSockStream.writeBytes("Error - incorrect or duplicate acct number...\n");
                        System.out.println("Error - incorrect or duplicate acct number...");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }
                }

                if(badInsert == 1){
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
                try {
                    acctNumber = Integer.parseInt(values);
                } catch (Exception e) {
                    System.out.println("Acct number not received or incorrectly formatted.");
                }

                // Retrieve Balance
                if (acctNumber >= 0){
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("SELECT * from constan7_BANKACCOUNTS WHERE accountNum = ?;");
                        stmt.setInt(1, acctNumber);

                        ResultSet rs = stmt.executeQuery();

                        System.out.println("RESULTS FROM THE DATABASE:");
                        
                        while (rs.next()) {
                            acctNumber = rs.getInt(1);
                            balance = rs.getInt(2);
                            
                            System.out.println("AccountNum: " + acctNumber + " Balance: " + balance);
                        } // end while

                    } catch(Exception ex) {
                        System.out.println("Problem accessing database.");
                        System.out.println("SQLException: " + ex.getMessage());
                    } // end try-catch

                    // Send balance to client
                    try {
                        outSockStream.writeBytes("Account number " + acctNumber + " has balance $" + balance + ".\n");
                        System.out.println("Account number " + acctNumber + " has balance $" + balance + ".\n");
                    } catch (Exception E) {
                        System.out.println("Cannot send message due to connection error - client may be down...");
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

            case 'D':
                // Server deposits requested amount into bank account
                System.out.println("\nDepositing money...");

                // Read account number and deposit amount from client
                try {
                    acctNumber = Integer.parseInt(args[0]);
                    transferRequest = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Acct number or transfer amount not formatted properly.");
                }
                
                // Check that bank account exists that transfer request is rational/sane
                if (transferRequest >= 0) {

                    // Retrieve balance
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("SELECT * from constan7_BANKACCOUNTS WHERE accountNum = ?;");
                        stmt.setInt(1, acctNumber);

                        ResultSet rs = stmt.executeQuery();

                        System.out.println("RESULTS FROM THE DATABASE:");
                        
                        while (rs.next()) {
                            acctNumber = rs.getInt(1);
                            balance = rs.getInt(2);
                            
                            System.out.println("AccountNum: " + acctNumber + " Balance: " + balance);
                        } // end while

                    } catch(Exception ex) {
                        System.out.println("Problem accessing database.");
                        System.out.println("SQLException: " + ex.getMessage());
                    } // end try-catch

                    // Update balance
                    newBalance = balance + transferRequest;
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("UPDATE constan7_BANKACCOUNTS SET balance = ? WHERE accountNum = ?;");
                        stmt.setInt(1, newBalance);
                        stmt.setInt(2, acctNumber);
                        stmt.executeUpdate();
                    } catch(Exception ex) {
                        System.out.println("Problem accessing database.");
                        System.out.println("SQLException: " + ex.getMessage());
                    } // end try-catch

                    // Send confirmation message to client
                    try {
                        outSockStream.writeBytes("Account number " + acctNumber + " went from balance $" +
                        balance + " to a new balance of $" + newBalance + ".\n");
                        
                        System.out.println("Account number " + acctNumber + " went from balance $" +
                        balance + " to a new balance of $" + newBalance + ".\n");
                    } catch (Exception E) {
                        System.out.println("Error - Cannot send message due to connection error - client may be down...");
                    }

                // Send error message
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
                try {
                    acctNumber = Integer.parseInt(args[0]);
                    transferRequest = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Error - Acct number or transfer amount not formatted properly.");    
                }
                
                // Check that bank account exists that transfer request is rational/sane
                if (transferRequest >= 0) {      
                    
                    // Retrieve balance
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("SELECT * from constan7_BANKACCOUNTS WHERE accountNum = ?;");
                        stmt.setInt(1, acctNumber);

                        ResultSet rs = stmt.executeQuery();

                        System.out.println("RESULTS FROM THE DATABASE:");
                        
                        while (rs.next()) {
                            acctNumber = rs.getInt(1);
                            balance = rs.getInt(2);
                            
                            System.out.println("AccountNum: " + acctNumber + " Balance: " + balance);
                        } // end while

                    } catch(Exception ex) {
                        System.out.println("Problem accessing database.");
                        System.out.println("SQLException: " + ex.getMessage());
                    } // end try-catch

                    if (balance - transferRequest >= 0){

                    // Update balance
                    newBalance = balance - transferRequest;
                    
                    try {
                        PreparedStatement stmt = dbConn.prepareStatement("UPDATE constan7_BANKACCOUNTS SET balance = ? WHERE accountNum = ?;");
                        stmt.setInt(1, newBalance);
                        stmt.setInt(2, acctNumber);
                        stmt.executeUpdate();
                    } catch(Exception ex) {
                        System.out.println("Problem accessing database.");
                        System.out.println("SQLException: " + ex.getMessage());
                    } // end try-catch

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
                try {
                    outSockStream.writeBytes("Error - Unknown command entered...\n");
                    System.out.println("Error - Unknown command entered...");
                } catch (Exception E) {
                    System.out.println("Error - Cannot send message due to connection error - client may be down...");
                }
                
                break;
        }

    } // End of processRequest

} // End of Server
