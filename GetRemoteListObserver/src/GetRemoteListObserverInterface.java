
import java.rmi.Remote;
import java.rmi.RemoteException;

interface GetRemoteListObserverInterface extends Remote 
{
    public void notifyNewOperationConcluded(String description) throws RemoteException;
}
