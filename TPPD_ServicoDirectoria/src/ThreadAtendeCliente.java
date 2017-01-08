import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

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

    ArrayList<ClienteUDP> listaClUDP;

    InetAddress address;
    int port;

    Object msg;
    String msgString;

    Boolean running;

    ThreadAtendeCliente(TrataHeartBeat trataHb) {
        this.trataHb = trataHb;
        this.running = true;
        this.listaClUDP = new ArrayList<>();
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

                            System.out.println("[INFO] - Enviei InfoParaCliente.");
                        }
                    } else if(msg instanceof MSGClients) {
                        MSGClients _msg = (MSGClients) msg;

                        for(int i = 0; i < trataHb.getServerList().size(); i++) {
                            if(trataHb.getServerList().get(i).getServerName().equals(_msg.getServerName())) {
                                this.listaClUDP.add(new ClienteUDP(_msg.getUsername(), address.toString(), _msg.getServerName(), port));
                                MsgListaClientes lista = new MsgListaClientes(trataHb.getServerList().get(i).getClientesOn());

                                bout = new ByteArrayOutputStream();
                                out = new ObjectOutputStream(bout);

                                out.writeObject(lista);
                                out.flush();

                                for(ClienteUDP c : this.listaClUDP) {
                                    if(c.getServerName().equals(_msg.getServerName())) {
                                        DatagramPacket p = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName(c.getIp().substring(1, c.getIp().length())), c.getPort());
                                        this.trataHb.getSdSocket().send(p);
                                    }
                                }

                                System.out.println("[INFO] - Enviei MsgListaClientes.");
                            }
                        }
                    } else if(msg instanceof MSGToCliente) {
                        String _a;
                        int _p;

                        MSGToCliente _msg = (MSGToCliente) msg;

                        for (ClienteUDP c : this.listaClUDP) {
                            if (c.getUsername().equals(_msg.getDestinatario()) && c.getServerName().equals(_msg.getServerName())) {
                                _a = c.getIp();
                                _p = c.getPort();

                                bout = new ByteArrayOutputStream();
                                out = new ObjectOutputStream(bout);

                                out.writeObject(_msg);
                                out.flush();

                                DatagramPacket p = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName(_a.substring(1, _a.length())), _p);
                                this.trataHb.getSdSocket().send(p);

                                System.out.println("[INFO] - Enviei MSGToCliente - " + _msg.getRementente() + ":" + _msg.getDestinatario());

                                break;
                            }
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
                        } else if(verify(serverList, hb.getServerName())) {
                            for(int i = 0; i < serverList.size(); i++) {
                                if(serverList.get(i).getServerName().equals(hb.getServerName())) {
                                    trataHb.getServerList().get(i).setClientesOn(hb.getClientesOn());
                                }
                            }
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
