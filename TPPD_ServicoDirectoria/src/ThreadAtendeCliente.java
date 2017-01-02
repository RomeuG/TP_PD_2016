import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by romeu on 12/31/16.
 */
public class ThreadAtendeCliente extends Thread {

    private final static int MAX_BYTES = 10000;

    private final static String LISTA = "<QUERO_LISTA>";

    TrataHeartBeat trataHb;
    DatagramPacket packet;
    ObjectInputStream recv;

    ByteArrayOutputStream bout;
    ObjectOutputStream out;

    MsgDirectoryServer hb;

    InetAddress address;
    int port;

    Object msg;
    String msgString;

    Boolean running;

    ThreadAtendeCliente(TrataHeartBeat trataHb) {
        this.trataHb = trataHb;
        this.running = true;
    }

    public boolean verify(ArrayList<Server> serverList, String name) {
        for(Server srv : serverList) {
            if(srv.getServerName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void run() {

        System.out.println("[INFO] - Thread para receber packets.");

        synchronized (running) {
            do {
                try {

                    byte[] arr = new byte[MAX_BYTES];
                    packet = new DatagramPacket(arr, MAX_BYTES);
                    this.trataHb.getSdSocket().receive(packet);

                    address = packet.getAddress();
                    port = packet.getPort();

                    //this.recv = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                    this.recv = new ObjectInputStream(new ByteArrayInputStream(arr));
                    msg = recv.readObject();

                    if(msg instanceof String) {

                        System.out.println("[INFO] - Recebi packet do cliente.");

                        msgString = (String) msg;
                        System.out.println("[INFO] - String do cliente: " + msgString);

                        if (msgString.equals(LISTA)) {
                            InfoParaCliente infoPCl = new InfoParaCliente(trataHb.getServerList());

                            bout = new ByteArrayOutputStream();
                            out = new ObjectOutputStream(bout);

                            out.writeObject(infoPCl);
                            out.flush();

                            DatagramPacket p = new DatagramPacket(bout.toByteArray(), bout.size(), address, port);
                            this.trataHb.getSdSocket().send(p);

                            System.out.println("[INFO] - Enviei packet para cliente.");
                        }
                    } else if(msg instanceof MsgDirectoryServer) {

                        System.out.println("[INFO] - Recebi heartbeat do servidor.");

                        hb = (MsgDirectoryServer) msg;

                        System.out.println("[INFO] - Packet identificado como heartbeat.");

                        ArrayList<Server> serverList = trataHb.getServerList();

                        if (!verify(serverList, hb.getServerName())) {
                            Server newServer = new Server(hb.getServerName(), hb.getClientesOn(), address.toString(), port, hb.getPortTCP());
                            trataHb.addToServerList(newServer);
                            System.out.println("[INFO] - Adicionei servidor a lista.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } while (this.running);
        }
    }
}
