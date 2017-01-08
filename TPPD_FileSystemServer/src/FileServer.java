
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

    private int PORT_TCP = 5001;
    private String IP_TCP;
    private String nomeServer;
    private final String defaultFolder = "AreaTrabalho";
    private List<TCP> clientes;
    private MsgDirectoryServer msg;

    /* Serviço de Directoria (IP e Porto) */
    public String IP_UDP = "127.0.0.1";
    public int PORT_UDP = 1338;

    // CONSTRUTOR
    public FileServer(String nomeServer) {
        this.nomeServer = nomeServer;

        // Abrir/Criar directório principal do sistema de ficheiros
        if (CreateDirectory(".", defaultFolder)) {
            System.out.println("Root Created");
        }

        clientes = new ArrayList<>();
    }

    // GETTERS
    public int getPORT_TCP() {
        return PORT_TCP;
    }

    public String getNomeServer() {
        return nomeServer;
    }

    /**
     * Método que corre a Thread para começar a aceitar clientes e a Thread para
     * enviar um HeartBeat para o Serviço de Directoria e reutiliza a thread
     * principal para comandos.
     */
    public void startServer() {
        AtendeClientes tAtende = null;
        NotificationThread tHeartBeat = null;

        try {
            msg = new MsgDirectoryServer(getNomeServer(), new ArrayList<>(), PORT_TCP);
            tHeartBeat = new NotificationThread();
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
            tAtende.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[Remote Server] shutdown");
    }

    /* Cria um Directorio com o <dirName> no path */
    private boolean CreateDirectory(String path, String dirName) {
        File theDir = new File(path + File.separator + dirName);

        // Se o directorio nao existe, cria-o
        if (!theDir.exists()) {
            return theDir.mkdir();
        }

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
            while (running) {
                try {
                    ByteArrayOutputStream bout = null;
                    ObjectOutputStream out = null;
                    DatagramPacket sendP;

                    s.setSoTimeout(30000);

                    bout = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(bout);
                    out.writeObject(msg);
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
            serverSocket.setSoTimeout(60000);
            defineIP_TCP(Communication.getHostAddress());

            clientThreads = new ArrayList<>();
            running = true;
        }

        public synchronized void setRunning(Boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            System.out.println("[Thread Atendimento Clientes] A aguardar novos clientes");

            while (running) {
                try {
                    Socket s = serverSocket.accept();
                    AtendeCliente atendeCliente = new AtendeCliente(s);
                    clientThreads.add(atendeCliente);
                    atendeCliente.start();
                    System.out.println("Lancada thread para atender o cliente " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
                } catch (SocketTimeoutException e) {
                    System.out.println("Erro " + e);
                } catch (IOException e) {}
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
        }
    }

    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread implements Observer {

        private Socket socket;
        private String myUsername;
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
            //synchronized (running) {
                while (running) {
                    ObjectInputStream in;
                    try {
                        in = new ObjectInputStream(socket.getInputStream());
                        Object obrec = in.readObject();
                        String msg;

                        if (obrec instanceof String) {
                            msg = (String) obrec;

                            String[] splitted = msg.split(":");

                            if (splitted[0].equals("login") || splitted[0].equals("registar") || (myUsername != null && isClientOn(myUsername))) {

                                switch (splitted[0]) {
                                    case "login":
                                        executarComandoLogin(splitted, socket);
                                        break;

                                    case "logout":
                                        executarComandoLogout();
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

                                    case "makedir":
                                        executarComandoMakedir(splitted);
                                        break;

                                    case "change_dir":
                                        executarComandoChangeDir(splitted);
                                        break;

                                    case "exit":
                                        executarComandoExit();
                                        break;
                                }
                            }
                        } else {
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject("<EXIT>");
                            out.flush();

                            try {
                                socket.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                        if (socket.isClosed() || socket == null) {
                            break;
                        }
                    } catch (SocketException e) {
                        System.out.println("Cliente saiu");
                        break;
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            //}
        }

        private void executarComandoLogin(String[] splittedList, Socket s) {
            Cliente cli = new Cliente(splittedList[1], splittedList[2]);
            myUsername = splittedList[1];

            File f = new File("Utilizadores"); // mudar directoria!
            if (existsClientLogin(splittedList, f) && !isClientOn(myUsername)) {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject("<Login_Success>");
                    outputStream.flush();

                    msg.getClientesOn().add(cli);

                    try {
                        DatagramSocket _s = new DatagramSocket();

                        ByteArrayOutputStream bout = null;
                        ObjectOutputStream out = null;
                        DatagramPacket sendP;

                        _s.setSoTimeout(30000);

                        bout = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(bout);
                        out.writeObject(msg);
                        out.flush();

                        sendP = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName("127.0.0.1"), PORT_UDP);

                        _s.send(sendP);
                    } catch (UnknownHostException | NumberFormatException | SocketTimeoutException e) {
                        System.out.println("Erro " + e);
                        return;
                    } catch (IOException e) {
                        System.out.println("Erro " + e);
                        return;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Adiciona um novo objecto do tipo TCP à lista
                clientes.add(new TCP(socket, cli));
                cli.setIp(s.getInetAddress().getHostAddress());
                cli.setPorto(s.getPort());
            } else {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject("<Login_Failed>");
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

        private void removeClienteHeartBeat(String username) {
            for (Cliente c : msg.getClientesOn()) {
                if (c.getUsername().equals(username)) {
                    msg.getClientesOn().remove(c);
                    return;
                }
            }
        }

        private void executarComandoLogout() {
            removeClienteHeartBeat(myUsername);

            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout);
                DatagramSocket s = new DatagramSocket();
                out.writeObject(msg);

                DatagramPacket dp = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName(IP_UDP), PORT_UDP);
                s.send(dp);

                ObjectOutputStream outs = new ObjectOutputStream(socket.getOutputStream());
                outs.writeObject("<Logout_OK>");
                outs.flush();

                s.close();
                socket.close();
            } catch (SocketException ex) {
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            removeCliente(myUsername);
        }

        private void executarComandoRegistar(String[] splittedList) {
            Cliente cli = new Cliente(splittedList[1], splittedList[2]);

            File f = new File("Utilizadores"); // mudar directoria!
            FileWriter fileWriter;
            BufferedWriter bw;
            PrintWriter pw;
            ObjectOutputStream out;

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

                    // Regista-se com sucesso
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("<Regist_Success>");
                    out.flush();

                    // Create user directory
                    CreateDirectory(defaultFolder, cli.getUsername());
                } else {
                    // Regista-se SEM sucesso
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("<Regist_Failed>");
                    out.flush();
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
                        if (aux[0].equals(splittedList)) {
                            return true;
                        }

                        line = br.readLine();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                }
            }

            return false;
        }

        private boolean existsClientLogin(String[] splittedList, File f) {
            if (f.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        String[] aux = line.split(":");
                        if (aux[0].equals(splittedList[1]) && aux[1].equals(splittedList[2])) {
                            return true;
                        }

                        line = br.readLine();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                }
            }

            return false;
        }

        private boolean isClientOn(String username) {
            for (TCP c : clientes) {
                if (c.getCli().getUsername().equals(username)) {
                    return true;
                }
            }

            return false;
        }

        private void executarComandoDownload(String[] splittedList) {

        }

        private void executarComandoUpload(String[] splittedList) {
            // CRIAR FICHEIRO  upload:create_file:fileName
            File f;
            if (splittedList[1].equals("create_file")) {
                f = new File(defaultFolder + File.separator + myUsername + File.separator + splittedList[2]);
                if (!f.exists()) {
                    try {
                        f.createNewFile();

                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject("<File_Created>");
                        outputStream.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        private void executarComandoMakedir(String[] splitted) {
            // Create another dir in user directory     makedir:dir
            File f = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[1]);
            if (!f.isDirectory() && !f.exists()) {
                CreateDirectory(defaultFolder + File.separator + myUsername, splitted[1]);

                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject("<Dir_Created>");
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void executarComandoChangeDir(String[] splitted) {
            File f = null;
            DirectoryInfo dinfo = new DirectoryInfo(new ArrayList<>(), new ArrayList<>());

            if (!splitted[1].equals(File.separator)) {
                f = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[1] + File.separator);
            } else {
                f = new File(defaultFolder + File.separator + myUsername + File.separator);
            }

            String[] names = f.list();

            for (String name : names) {
                try {
                    if (new File(f.getCanonicalPath() + name).isDirectory()) {
                        dinfo.getDir().add(name);
                    } else {
                        dinfo.getFicheirosName().add(name);
                    }
                } catch (IOException ex) {
                }
            }

            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(dinfo);
                out.flush();
            } catch (IOException ex) {
            }
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
