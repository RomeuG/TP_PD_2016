import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by romeu on 1/7/17.
 */
public interface GetRemoteListServiceInterface extends Remote {

    ArrayList<Server> getLista() throws RemoteException;

    void addServer(Server srv) throws RemoteException;
    void removeServer(Server srv) throws RemoteException;

    void addClient(Cliente client, String serverName) throws RemoteException;
    void removeClient(Cliente client, String serverName) throws RemoteException;
//
//    public void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
//    public void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException;

}
