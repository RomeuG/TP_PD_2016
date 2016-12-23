
// FEITO COM ERROS DE COMPILACAO

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DirectoryService implements DirServiceInterface {

    private static final long serialVersionUID = 1L;
    private static final int PORT = 1337;

    // lista servidores
    private List<ServerInfo> serverList;
    // lista utilizadores registados & logados
    private List<Utilizador> userListRegistered;
    private List<Utilizador> userListLoggedIn;

    public void DirServiceInit() {
        // Obter utilizadores registados

        // Iniciar thread server check
        Thread verifyServer = new Thread(new ServerCheck());
        verifyServer.setDaemon(true);
        verifyServer.start();

        // Iniciar thread da resposta dos clientes
        Thread threadClients = new Thread(new ThreadClients());
        verifyServer.setDaemon(true);
        verifyServer.start();
    }

    public void parseHeartBeat(DatagramPacket packetObj) {
        try {
            byte[] data = packetObj.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = null;

            is = new ObjectInputStream(in);

        } catch (IOException e) {
            e.printStackTrace();
        }

        HeartBeat hb = (HeartBeat) is.readObject();
        // TODO: verificar de onde veio
        // TODO: tratar

    }

    // Thread verificacao servidor
    // Se nao existir resposta do serviddor
    // no maximo de 30 segundos, este e
    // removido da lista de servidores
    // activos
    class ServerCheck implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (serverList) {
                        if (!serverList.isEmpty()) {

                            Iterator<ServerInfo> it = serverList.iterator();

                            while (it.hasNext()) {
                                ServerInfo s = it.next();

                                long seconds = (new Date().getTime() - s.getDate().getTime()) / 1000;
                                if (seconds > 30) {
                                    it.remove();
                                }
                            }
                        }
                    }

                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ThreadClients implements Runnable {

        DatagramSocket s;
        byte[] buf;

        private void tratarPacket(DatagramPacket packet) {

        }

        @Override
        public void run() {
            while (true) {
                try {
                    s = new DatagramSocket();

                    buf = new byte[1000];

                    DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    s.receive(dp);

                    DatagramPacket out = new DatagramPacket(buf, buf.length, dp.getAddress(), dp.getPort());
                    s.send(out);

                    s.setSoTimeout(1000);

                } catch (SocketException e) {
                    s.close();
                } catch (IOException e) {
                    s.close();
                }
            }
        }
    }
}
