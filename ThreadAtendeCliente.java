package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
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

    @Override
    public void run() {

        System.out.println("[INFO] - Thread para receber packets.");

        synchronized (running) {
            do {
                try {
                    packet = new DatagramPacket(new byte[MAX_BYTES], MAX_BYTES);
                    this.trataHb.getSdSocket().receive(packet);

                    //this.trataHb.setClSocket(new DatagramSocket(packet.getPort()));

                    address = packet.getAddress();
                    port = packet.getPort();

                    this.recv = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
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
                            trataHb.getClSocket().send(p);

                            System.out.println("[INFO] - Enviei packet para cliente.");
                        }
                    } else if(msg instanceof MsgDirectoryServer) {

                        System.out.println("[INFO] - Recebi heartbeat do servidor.");

                        hb = (MsgDirectoryServer) msg;

                        System.out.println("[INFO] - Packet identificado como heartbeat.");

                        ArrayList<Server> serverList = trataHb.getServerList();

                        if(!serverList.contains(hb.getServerName())) {
                            Server newServer = new Server(hb.getServerName(), hb.getClientesOn(), address.toString(), port);
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
