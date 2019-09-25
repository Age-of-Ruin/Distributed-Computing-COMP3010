import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
        
public class Q3_Server_RMI implements RemoteInterface {
        
 	static Hashtable<Integer, Integer> bankInfo = new Hashtable<Integer, Integer>(); // Hash table for bank info

    public Q3_Server_RMI() {}

    public synchronized String welcomeMessage() {
        return "Welcome to the Bank!";
    }

    public synchronized String createAccount(int acctNumber) {
        
        String response = "";

        if (!bankInfo.containsKey(acctNumber)) {

	        // Create account at $0
	        bankInfo.put(acctNumber, 0);

	        // Successful response
	        response = "\nAccount number " + acctNumber + " has been successfully created.\n";

		} else {

			// Invalid reponse
			response = "\nWoops - has already been created.\n";
		}

        return response;
    }

    public synchronized String retrieveBalance(int acctNumber) {
        
        String response = "";
        int balance = -1;

        if (bankInfo.containsKey(acctNumber)) {

	        // Retrieve balance
	        balance = bankInfo.get(acctNumber);

	        // Successful response
	        response = "\nAccount number " + acctNumber + " has balance $" + balance + ".\n";

		} else {

			// Invalid reponse
			response = "\nWoops - cannot find account.\n";
		}

        return response;
    }

    public synchronized String depositAmount(int acctNumber, int amount) {
        
        String response = "";
        int balance = -1;
        int newBalance = -1;

        if (bankInfo.containsKey(acctNumber)) {

	        // Retrieve balance
	        balance = bankInfo.get(acctNumber);
	        newBalance = balance + amount;
	        bankInfo.put(acctNumber, newBalance);

	        // Successful response
	        response = "\nAccount number " + acctNumber + " went from balance $" + balance + 
	        " to a new balance of $" + newBalance + ".\n";

		} else {

			// Invalid reponse
			response = "\nWoops - cannot find account.\n";
		}

        return response;
    }

    public synchronized String withdrawAmount(int acctNumber, int amount) {
        
        String response = "";
        int balance = -1;
        int newBalance = -1;

        if (bankInfo.containsKey(acctNumber)) {

	        // Retrieve balance
	        balance = bankInfo.get(acctNumber);

	        if(balance - amount >= 0) {

		        newBalance = balance - amount;
		        bankInfo.put(acctNumber, newBalance);

		        // Successful response
		        response = "\nAccount number " + acctNumber + " went from balance $" + balance + 
		        " to a new balance of $" + newBalance + ".\n";

		    } else {
			
				// Invalid reponse
				response = "\nWoops - took out too much money.\n";
		    }

		} else {

			// Invalid reponse
			response = "\nWoops - cannot find account.\n";
		}

        return response;
    }

    public synchronized String accountSummary(){

    	return "\n" + bankInfo.toString() + "\n";

    }

        
    public static void main(String args[]) {
        
        try {

        	// Create server object
            Q3_Server_RMI obj = new Q3_Server_RMI();

            // Create remote stub (for remote access of methods) and interface at port 13065
            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(obj, 13065);

            // Bind the remote server stub in the registry (use port 13064 for client connection)
            Registry registry = LocateRegistry.getRegistry(13064);
            registry.bind("BankRegistry", stub);

            System.out.println("Server ready.");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
    }
}
