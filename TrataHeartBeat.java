package com.company;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by romeu on 12/31/16.
 */
public class TrataHeartBeat {

    private final static int TIMEOUT = 10000;

    private final static String IP = "127.0.0.1";
    private final static int PORT = 1338;

    private DatagramSocket srvSocket;
    private DatagramSocket clSocket;
    private DatagramSocket sdSocket;

    Thread atendeServer;
    Thread atendeCliente;

    private ArrayList<Server> serverList;

    TrataHeartBeat() {

        serverList = new ArrayList<>();

        try {
            this.srvSocket = new DatagramSocket();
            this.srvSocket.setSoTimeout(TIMEOUT);

            this.clSocket = new DatagramSocket();
            this.clSocket.setSoTimeout(TIMEOUT);

            this.sdSocket = new DatagramSocket(PORT);
            this.sdSocket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // iniciar threads
        //atendeServer = new ThreadAtendeServidor(this);
        atendeCliente = new ThreadAtendeCliente(this);

        //atendeServer.setDaemon(true);
        atendeCliente.setDaemon(true);

        //atendeServer.start();
        atendeCliente.start();

        try {
            //atendeServer.join();
            atendeCliente.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addToServerList(Server name) {
        serverList.add(name);
    }

    public DatagramSocket getSrvSocket() {
        return srvSocket;
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

    public DatagramSocket getClSocket() {
        return clSocket;
    }

    public void setClSocket(DatagramSocket clSocket) {
        this.clSocket = clSocket;
    }
}
