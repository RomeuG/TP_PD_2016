import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by romeu on 1/7/17.
 */
public interface GetRemoteListServiceInterface extends Remote {

    public ArrayList<Server> getLista() throws RemoteException;

    public void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
    public void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException;

}
