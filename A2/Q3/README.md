# Java RMI ATM
The programs Q3_Server_RMI.java and Q3_Client_RMI.java reimplement the Bank Server & Client developed from A1 (using a volitile hashtable to store bank data), except the program is developed using java RMI instead of java Sockets.

RMI automatically generates a thread for each request/method call - the server deals with these by making each method synchronized such that only 1 client at a time will enter the method & complete their call.

** Note: Please fill in /your/class/dir/here with the desired directory that will store all the class files. **

The steps to compile and run the program are as follows:

To compile all files use:
javac -d /your/class/dir/here RemoteInterface.java Q3_Server_RMI.java Q3_Client_RMI.java

To start the rmiregistry on my assigned port number (13064) on Linux:
rmiregistry 13064 &

To start the server:
java -classpath /your/class/dir/here -Djava.rmi.server.codebase=file:/your/class/dir/here Q3_Server_RMI &

To run the client:
java -classpath /your/class/dir/here Q3_Client_RMI
