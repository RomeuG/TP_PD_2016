
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteRMI extends UnicastRemoteObject
{
    public ClienteRMI() throws RemoteException { }
    
    public static void main(String[] args) {
        ClienteRMI myRemoteService = null;
        GetRemoteListServiceInterface remoteListService;
        
        String objectURL = "127.0.0.1";
        
        if (args.length >= 1)
            objectURL = args[0];
        
        String registration = "rmi://" + objectURL + "/GetRemoteList";
        
        try {
            // Obtem a referencia remota para o servico com nome "GetRemoteList"
            remoteListService = (GetRemoteListServiceInterface) Naming.lookup(objectURL);
            
            // Lanca o servico local para acesso remoto por parte do serviço de directoria.
            myRemoteService = new ClienteRMI();
            System.out.println("Servico ClienteRMI criado e em execucao...");
        
            
            // Obtem a lista pretendida, invocando o metodo getLista no servico remoto.
//            if (remoteListService.getLista(myRemoteService))
//                System.out.println("Lista recebida com sucesso.");
//            else
//                System.out.println("Lista NAO recebida.");
            
        } catch(RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch(NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch(IOException e) {
            System.out.println("Erro E/S - " + e);
        } catch(Exception e) {
            System.out.println("Erro - " + e);
        }
        finally {
            if(myRemoteService != null) {
                // Termina o serviço local
                try {
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                } catch(NoSuchObjectException e){}
            }
        }
    }
}
