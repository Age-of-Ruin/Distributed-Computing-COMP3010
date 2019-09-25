import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
    String welcomeMessage() throws RemoteException;
    String createAccount(int acctNumber) throws RemoteException;
    String retrieveBalance(int acctNumber) throws RemoteException;
    String depositAmount(int acctNumber, int amount) throws RemoteException;
    String withdrawAmount(int acctNumber, int amount) throws RemoteException;
    String accountSummary() throws RemoteException;
}