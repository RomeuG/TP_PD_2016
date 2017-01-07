
import java.rmi.Remote;

public interface GetRemoteListServiceInterface extends Remote
{
    public InfoParaCliente getList() throws java.rmi.RemoteException;
    
    public void addObserver(GetRemoteListObserverInterface observer) throws java.rmi.RemoteException;
    public void removeObserver(GetRemoteListObserverInterface observer) throws java.rmi.RemoteException;    
}
