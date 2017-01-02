import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by romeu on 12/31/16.
 */
public class TrataHeartBeat {

    private final static int TIMEOUT = 30000;

    private final static String IP = "127.0.0.1";
    private final static int PORT = 1338;

    private DatagramSocket sdSocket;

    Thread atendeServer;
    Thread atendeCliente;

    private ArrayList<Server> serverList;

    TrataHeartBeat() {

        serverList = new ArrayList<>();

        try {
            this.sdSocket = new DatagramSocket(PORT);
            this.sdSocket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // iniciar threads
        atendeCliente = new ThreadAtendeCliente(this);

        atendeCliente.setDaemon(true);
        atendeCliente.start();

        try {
            atendeCliente.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void verificarServidores(MsgDirectoryServer hb, long tFinal) {
//        for (int i = 0; i < serverList.size(); i++) {
//            if (hb.getServerName().equals(serverList.get(i).getServerName())) {
//                long resultado = tFinal - temposHeartBeats.get(serverList.get(i));
//
//                if (resultado / 1000.0 > 30) {
//                    temposHeartBeats.remove(serverList.get(i));
//                    HeartBeat h = serverList.remove(i);
//                }
//            }
//        }
//    }

    public void addToServerList(Server name) {
        serverList.add(name);
    }

    public ArrayList<Server> getServerList() {
        return serverList;
    }

    public DatagramSocket getSdSocket() {
        return sdSocket;
    }

    public void setSdSocket(DatagramSocket sdSocket) {
        this.sdSocket = sdSocket;
    }

}
