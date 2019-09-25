Question 3 now implements a multi-threaded and connection-oriented client/server model (simulating a banking system) which communicate via strings. The multi-threaded implementation supports many concurrent client connections (ie multiple clients can hold a connection and make changes at a single moment). NOTE: The processRequest() method in Q3_Server is declared as SYNCHRONIZED - this means that only 1 thread can change/update the bankInfo hashtable at any one time. As previously, after a client has established a connection, the server will accept a SINGLE string as a command before disconnecting the client - ie the client must reconnect to perform more actions.

To compile and run the server:
javac Q3_Server.java
java Q3_Server

To compile the client:
javac Q3_Client.java
java Q3_Client

After the server is compiled and executed, it ready and waitng for client connections at port 13064 on the local host.

After the client is compiled and executed, it begins running a simple command-line interface which provides basic commands as follows:

!-connect
C-create
R-retrieve
D-deposit
W-withdraw
E-quit client

FINAL NOTE: Since this version does not support any method of shutting down the server (server runs on infinite-loop), a kill signal (CTRL-C) should be performed on the server to stop execution.
