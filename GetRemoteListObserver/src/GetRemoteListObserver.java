
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class GetRemoteListObserver extends UnicastRemoteObject implements GetRemoteListObserverInterface
{
    public GetRemoteListObserver() throws RemoteException {}
    
    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException {
        System.out.println(description);
    }
    
    public static void main(String[] args) 
    {
        try {
            // Cria e lanca o servico
            GetRemoteListObserver observer = new GetRemoteListObserver();
            System.out.println("Servico GetRemoteListObserver criado e em execucao...");
            
            // Localiza o servico remoto nomeado "GetRemoteList"
            String objectUrl = "rmi://127.0.0.1/GetRemoteList"; //rmiregistry on localhost
            
            if(args.length > 0)
                objectUrl = "rmi://"+args[0]+"/GetRemoteList";
            
            GetRemoteListServiceInterface getRemoteListService = (GetRemoteListServiceInterface) Naming.lookup(objectUrl);
            
            // Adiciona observador no servico remoto
            getRemoteListService.addObserver(observer);
            
            System.out.println("<Enter> para terminar...");
            System.out.println();
            System.in.read();
            
            getRemoteListService.removeObserver(observer);
            UnicastRemoteObject.unexportObject(observer, true);
            
        } catch(RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch(Exception e) {
            System.out.println("Erro - " + e);
        }
    }
}
