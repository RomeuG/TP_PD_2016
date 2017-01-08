import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

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
        synchronized (thb.getServerList()) {
            return thb.getServerList();
        }
    }

    @Override
    synchronized public void addServer(Server srv) throws RemoteException {
        synchronized (thb.getServerList()) {
            thb.getServerList().add(srv);
        }
    }

    @Override
    synchronized public void removeServer(Server srv) throws RemoteException {
        synchronized (thb.getServerList()) {
            if (thb.getServerList().contains(srv))
                thb.getServerList().remove(srv);
        }
    }

    @Override
    synchronized public void addClient(Cliente client, String serverName) throws RemoteException {
        synchronized (thb.getServerList()) {
            for (Server server : thb.getServerList()) {
                if (server.getServerName().equals(serverName)) {
                    server.getClientesOn().add(client);
                    break;
                }
            }
        }
    }

    @Override
    synchronized public void removeClient(Cliente client, String serverName) throws RemoteException {
        synchronized (thb.getServerList()) {
            for (Server server : thb.getServerList()) {
                if (server.getServerName().equals(serverName)) {
                    for (Cliente c : server.getClientesOn()) {
                        if (c.getUsername().equals(client.getUsername())) {
                            server.getClientesOn().remove(c);
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }

//    @Override
//    public synchronized void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException {
//        if(!observers.contains(observer)){
//            observers.add(observer);
//            System.out.println("+ um observador.");
//        }
//    }
//
//    @Override
//    public synchronized void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException {
//        if(observers.remove(observer))
//            System.out.println("- um observador.");
//    }
//
//    public synchronized void notifyObservers(String msg)
//    {
//        for(int i = 0; i < observers.size(); i++) {
//            try {
//                observers.get(i).notifyNewOperationConcluded(msg);
//            } catch(RemoteException e) {
//                observers.remove(i--);
//                System.out.println("Observador eliminado por estar inacessivel");
//            }
//        }
//    }

    public static void main(String[] args) {
        thb = new TrataHeartBeat();

        Registry r;

        try{
            try{

                System.out.println("Tentativa de lancamento do registry no porto " +
                        Registry.REGISTRY_PORT + "...");

                r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

                System.out.println("Registry lancado!");

            }catch(RemoteException e){
                System.out.println("Registry provavelmente ja' em execucao!");
                r = LocateRegistry.getRegistry();
            }

            CorrerServicoDirectoria fileService = new CorrerServicoDirectoria();

            System.out.println("Servico GetRemoteFile criado e em execucao ("+fileService.getRef().remoteToString()+"...");

            r.bind(SERVICE_NAME, fileService);

            System.out.println("Servico " + SERVICE_NAME + " registado no registry...");

        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }

}
