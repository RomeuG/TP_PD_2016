import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romeu on 12/31/16.
 */
public class CorrerServicoDirectoria extends UnicastRemoteObject implements GetRemoteListServiceInterface {

    public static final String SERVICE_NAME = "GetRemoteList";
    public static final int MAX_CHUNCK_SIZE = 10000; //bytes

    List<GetRemoteFileObserverInterface> observers;

    private static TrataHeartBeat thb;

    protected CorrerServicoDirectoria() throws RemoteException {
        this.observers = new ArrayList<>();
    }

    @Override
    public ArrayList<Server> getLista() throws RemoteException {
            return thb.getServerList();
    }

    @Override
    public void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException {

    }

    @Override
    public void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException {

    }


    public static void main(String[] args) {
        thb = new TrataHeartBeat();
    }

}
