# Single-Threaded TCP ATM
Question 2 implements a simple, single-threaded and connection-oriented client/server model (simulating a banking system) which communicate via strings. Since the server is only single threaded, it will only support 1 client connection at any given time (but can backlog/queue up to 3). After a client has established a connection, the server will accept a SINGLE string as a command before disconnecting the client - ie the client must reconnect to perform more actions.

To compile and run the server:
javac Q2_Server.java
java Q2_Server

To compile the client:
javac Q2_Client.java
java Q2_Client

After the server is compiled and executed, it ready and waitng for client connections at port 13064 on the local host.

After the client is compiled and executed, it begins running a simple command-line interface which provides basic commands as follows:

!-connect
C-create
R-retrieve
D-deposit
W-withdraw
E-quit client
S-quit server
Q-quit client & server
