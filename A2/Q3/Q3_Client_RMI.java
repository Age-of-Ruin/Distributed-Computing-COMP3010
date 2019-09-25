import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Q3_Client_RMI {

    private Q3_Client_RMI() {}

    static RemoteInterface servStub;

    public static void main(String[] args) {

		String host = null; // Null = local host
		int port = 13064; // port number for registry
    	String response = ""; // Used for server response

		// Connect to registry and comm
        try {
        	// Find Registry located at host:port
            Registry registry = LocateRegistry.getRegistry(host, port);
            servStub = (RemoteInterface) registry.lookup("BankRegistry");
            
        	// Call remote method
            response = servStub.welcomeMessage();

            // Print response
            System.out.println(response);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }

    	String line = "";
    	String values = "";
        String params[] = new String[2];
		BufferedReader kbIn = new BufferedReader(new InputStreamReader (System.in));
		int acctNum = -1;
		int amount = -1;

        while (!line.equals("E")){

        	System.out.println("Please enter command: C-create, R-retrieve, D-deposit, W-withdraw, E-quit client");
        	
        	// Parse user input
        	try {
        		
        		line = kbIn.readLine();

		        if (line.contains("<"))
		            values = line.substring(line.indexOf("<")+1, line.length()-1);
		      
		        if (values.contains(","))
		            params = values.split(",");

		        if (line.length() == 2 || line.length() == 3){
		        	line = "Jeez Incorrect - go to default";
		        }

			} catch (Exception ex) {
				System.out.println("Error - incorrect input.");			
			}

			switch(line.charAt(0)) {

				// Create 
				case 'C':
	                try {	                   
	                    acctNum = Integer.parseInt(values);		                
		                
		                if (acctNum >= 0){
		                	response = servStub.createAccount(acctNum);
		                	System.out.println(response);
		                } else
		                	throw new Exception("Error - Acct number not incorrectly formatted.");

	                } catch (Exception ex) {
	                    System.out.println("Error - Acct number not incorrectly formatted.");
	                }
	               
	            	break;

        		// Retrieve
            	case 'R':
	                try {	                   
	                    acctNum = Integer.parseInt(values);		                
		                
		                if (acctNum >= 0){
		                	response = servStub.retrieveBalance(acctNum);
		                	System.out.println(response);
		                } else
		                	throw new Exception("Error - Acct number not incorrectly formatted.");

	                } catch (Exception ex) {
	                    System.out.println("Error - Acct number not incorrectly formatted.");
	                }

            		break;

        		// Deposit
        		case 'D':
	                try {	                   
	                    acctNum = Integer.parseInt(params[0]);		                
		                amount = Integer.parseInt(params[1]);

		                if (acctNum >= 0 && amount > 0){
		                	response = servStub.depositAmount(acctNum, amount);
		                	System.out.println(response);
		                } else
		                	throw new Exception("Error - Acct number or ammount not incorrectly formatted.");

	                } catch (Exception ex) {
	                    System.out.println("Error - Acct number not incorrectly formatted.");
	                }
        			
        			break;


				// Withdraw
    			case 'W':
	                try {	                   
	                    acctNum = Integer.parseInt(params[0]);		                
		                amount = Integer.parseInt(params[1]);

		                if (acctNum >= 0 && amount > 0){
		                	response = servStub.withdrawAmount(acctNum, amount);
		                	System.out.println(response);
		                } else
		                	throw new Exception("Error - Acct number or ammount not incorrectly formatted.");

	                } catch (Exception ex) {
	                    System.out.println("Error - Acct number not incorrectly formatted.");
	                }

    				break;

    			// Account summary
    			case 'S':
	                try {	                   
	                	response = servStub.accountSummary();
	                	System.out.println(response);

	                } catch (Exception ex) {
	                    System.out.println("Error - Acct number not incorrectly formatted.");
	                }
    				break;

				// Quit client
				case 'E':
					// Do Nothing
					break;


    			default:
    				System.out.println("Error - Incorrect input.");
    				break;
			}
        }
    
    System.out.println("Exiting client...");

    }
}