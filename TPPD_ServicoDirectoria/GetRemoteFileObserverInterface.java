import java.rmi.RemoteException;

/**
 * Created by romeu on 1/7/17.
 */
public interface GetRemoteFileObserverInterface {

    public void notifyNewOperationConcluded(String description) throws RemoteException;

}
