
import Communication.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FileServer {

    /* IP/Porto TCP e Nome do Servidor */
    private int PORT_TCP = 7000;
    private String IP_TCP;
    private String nomeServer;
    private final String defaultFolder = "AreaTrabalho";
    private List<TCP> clientes;
    private List<String> usernamesClients;

    /* Serviço de Directoria (IP e Porto) */
    public String IP_UDP = "192.168.1.67";
    public int PORT_UDP = 8000;

    // CONSTRUTOR
    public FileServer(String nomeServer) {
        this.nomeServer = nomeServer;
        
        // Abrir/Criar directório principal do sistema de ficheiros
        if (CreateDirectory(".", defaultFolder))
            System.out.println("Root Created");

        clientes = new ArrayList<>();
    }

    // GETTERS
    public int getPORT_TCP() { return PORT_TCP; }
    public String getNomeServer() { return nomeServer; }

    /**
     * Método que corre a Thread para começar a aceitar clientes e a Thread
     * para enviar um HeartBeat para o Serviço de Directoria e reutiliza a
     * thread principal para comandos.
     */
    public void startServer() {
        AtendeClientes tAtende = null;

        try {
            Thread tHeartBeat = new Thread(new NotificationThread());
            tHeartBeat.setDaemon(true); //Pode dar problemas
            tHeartBeat.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            tAtende = new AtendeClientes();
            tAtende.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Encerrar todas as Threads a correr
        try {
            tAtende.setRunning(false);
            tAtende.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[Remote Server] shutdown");
    }

    /* Cria um Directorio com o <dirName> no path */
    private boolean CreateDirectory(String path, String dirName) {
        File theDir = new File(path + "/" + dirName);

        // Se o directorio nao existe, cria-o
        if (!theDir.exists())
            return theDir.mkdir();

        return true;
    }

    public synchronized void defineIP_TCP(String ipTCP) {
        IP_TCP = ipTCP;
    }

    /**
     * Thread -> Notification To Directory Service ( sends HeartBeat )
     */
    class NotificationThread extends Thread {

        private DatagramSocket s;
        private InetAddress addr;
        private Boolean running;

        // Construtor
        public NotificationThread() throws SocketException, UnknownHostException {
            s = new DatagramSocket();
            addr = InetAddress.getByName(IP_UDP);

            running = true;
        }

        public synchronized void setRunning(Boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            synchronized (running) {
                while (running) {
                    try {
                        ByteArrayOutputStream bout = null;
                        ObjectOutputStream out = null;
                        DatagramPacket sendP;

                        s.setSoTimeout(10000);

                        bout = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(bout);
                        out.writeObject(new MsgDirectoryServer(FileServer.this));
                        out.flush();

                        sendP = new DatagramPacket(bout.toByteArray(), bout.size(), addr, PORT_UDP);

                        s.send(sendP);
                    } catch (UnknownHostException | NumberFormatException | SocketTimeoutException e) {
                        System.out.println("Erro " + e);
                        return;
                    } catch (IOException e) {
                        System.out.println("Erro " + e);
                        return;
                    }

                    try {
                        sleep(30000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Thread -> Accept new clients
     */
    class AtendeClientes extends Thread {

        private ServerSocket serverSocket;
        private List<AtendeCliente> clientThreads;
        private Boolean running;

        // Construtor
        public AtendeClientes() throws IOException {
            serverSocket = new ServerSocket(PORT_TCP);
            serverSocket.setSoTimeout(10000);
            defineIP_TCP(Communication.getHostAddress());

            clientThreads = new ArrayList<>();
            running = true;
        }

        public synchronized void setRunning(Boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            synchronized (running) {
                System.out.println("[Thread Atendimento Clientes] A aguardar novos clientes");

                while (running) {
                    try {
                        Socket s = serverSocket.accept();
                        AtendeCliente atendeCliente = new AtendeCliente(s);
                        clientThreads.add(atendeCliente);
                        atendeCliente.start();
                        System.out.println("Lancada thread para atender o cliente " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
                    } catch (IOException e) {
                        System.out.println("Erro " + e);
                        return;
                    }
                }
            }

            System.out.println("[Thread Atendimento Clientes] Server is not accepting new clients anymore");

            // Warn all clients
            for (int i = 0; i < clientThreads.size(); i++) {
                try {
                    clientThreads.get(i).setRunning(false);
                    clientThreads.get(i).join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Erro " + e);
                return;
            }

            // Warn Directory Service
            // ...
        }
    }

    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread implements Observer {
        
        private Socket socket;
        private Boolean running;

        // Construtor
        public AtendeCliente(Socket socket) {
            this.socket = socket;
            running = true;
        }

        public synchronized void setRunning(Boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            synchronized (running) {
                while (running) {
                    ObjectInputStream in;
                    try {
                        in = new ObjectInputStream(socket.getInputStream());

                        Object obrec = in.readObject();
                        String msg;

                        if (obrec instanceof String) {
                            msg = (String) obrec;

                            String[] splitted = msg.split(":");
                            switch (splitted[0]) {
                                case "login":
                                    executarComandoLogin(splitted);
                                    break;

                                case "logout":
                                    executarComandoLogout(splitted);
                                    break;

                                case "registar":
                                    executarComandoRegistar(splitted);
                                    break;

                                case "download":
                                    executarComandoDownload(splitted);
                                    break;

                                case "upload":
                                    executarComandoUpload(splitted);
                                    break;

                                case "exit":
                                    executarComandoExit();
                                    break;
                            }
                        } else {
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject("<EXIT>");
                            out.flush();
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        private void executarComandoLogin(String[] splittedList) {
            Cliente cli = new Cliente(splittedList[1], splittedList[2]);

            if (cli.getUsername() != null && cli.getPassword().equals(splittedList[2])) {
                String username = splittedList[1];

                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject("<Login_Success>");
                    outputStream.flush();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Adiciona um novo objecto do tipo TCP à lista
                clientes.add(new TCP(socket, cli));
                // Create user directory
                CreateDirectory(defaultFolder, cli.getUsername());

            } else {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject("<Login_failed>");
                    outputStream.flush();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void removeCliente(String username) {
            int conta = 0;
            for (TCP c : clientes) {
                if (c.getCli().getUsername().equals(username)) {
                    clientes.remove(conta);
                }

                conta++;
            }
        }

        private void executarComandoLogout(String[] splittedList) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout);
                DatagramSocket s = new DatagramSocket();
                MsgLogoutToDirectory msg = new MsgLogoutToDirectory(splittedList[0], InetAddress.getByName(IP_UDP), PORT_UDP);
                out.writeObject(msg);

                DatagramPacket dp = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName(IP_UDP), PORT_UDP);
                s.send(dp);
            } catch (SocketException ex) {
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            removeCliente(splittedList[0]);
        }

        private void executarComandoRegistar(String[] splittedList) {
            Cliente cli = new Cliente(splittedList[1], splittedList[2]);

            File f = new File("ComRegistos"); // mudar directoria!
            FileWriter fileWriter;
            BufferedWriter bw;
            PrintWriter pw;

            try {
                if (!existsClient(splittedList[1], f)) {
                    if (f.exists()) {
                        fileWriter = new FileWriter(f, true);
                        bw = new BufferedWriter(fileWriter);
                        pw = new PrintWriter(bw);
                        pw.println(cli.getUsername() + ":" + cli.getPassword());
                        pw.close();
                    } else {
                        fileWriter = new FileWriter(f, false);
                        bw = new BufferedWriter(fileWriter);
                        pw = new PrintWriter(bw);
                        pw.println(cli.getUsername() + ":" + cli.getPassword());
                        pw.close();
                    }
                }
            } catch (IOException ex) {
            }
        }

        private boolean existsClient(String splittedList, File f) {
            if (f.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        String[] aux = line.split(":");
                        if (aux[0].equals(splittedList))
                            return true;
                        
                        line = br.readLine();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                }
            }

            return false;
        }

        private void executarComandoDownload(String[] splittedList) {

        }

        private void executarComandoUpload(String[] splittedList) {

        }

        private void executarComandoExit() {
            setRunning(false);
        }

        @Override
        public void update(Observable o, Object arg) {
            System.out.println(arg);
        }
    }
}
