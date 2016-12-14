
// FEITO COM ERROS DE COMPILACAO

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.Date;

public class DirectoryService implements DirServiceInterface {

    private static final long serialVersionUID = 1L;
    private static final int PORT = 1337;

    // lista servidores
    private List<Servidor> serverList;
    // lista utilizadores registados & logados
    private List<Utilizador> userListRegistered;
    private List<Utilizador> userListLoggedIn;

    public void DirServiceInit() {
        // Obter utilizadores registados

        // Iniciar thread
        Thread verifyServer = new Thread(new ServerCheck());
        verifyServer.setDaemon(true);
        verifyServer.start();
    }

    public void parseHeartBeat(DatagramPacket packetObj) {

        byte[] data = packetObj.getData();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);

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
                while(true){
                    synchronized(serverList) {
                        if( !serverList.isEmpty()){

                            Iterator<Servidor> it = serverList.iterator();

                            while (it.hasNext()) {

                                Servidor s = it.next();

                                long seconds = (new Date().getTime() - s.getDate().getTime()) / 1000;

                                if(seconds > 30){
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
}
