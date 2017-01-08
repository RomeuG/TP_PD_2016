
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileServer 
{
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
            tHeartBeat.setDaemon(true);
            tHeartBeat.start();
        } catch (SocketException | UnknownHostException e) {
            System.out.println("Erro " + e);
        }

        try {
            tAtende = new AtendeClientes();
            tAtende.start();
        } catch (IOException e) {
            System.out.println("Erro " + e);
        }

        // Encerrar todas as Threads a correr
        try {
            tAtende.join();
        } catch (InterruptedException e) {
            System.out.println("Erro " + e);
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
                    System.out.println("Erro " + ex);
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
            serverSocket.setSoTimeout(30000);
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
                } catch (IOException e) {
                    System.out.println("Erro " + e);
                }
            }

            System.out.println("[Thread Atendimento Clientes] Server is not accepting new clients anymore");

            // Warn all clients
            for (int i = 0; i < clientThreads.size(); i++) {
                try {
                    clientThreads.get(i).setRunning(false);
                    clientThreads.get(i).join();
                } catch (InterruptedException ex) {
                    System.out.println("Erro " + ex);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Erro " + e);
            }
        }
    }

    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread {

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
                                        
                                    case "cp":
                                        executarComandoCopiar(splitted);
                                        break;
                                        
                                    case "mv":
                                        executarComandoMover(splitted);
                                        break;
                                        
                                    case "rm":
                                        executarComandoRemove(splitted);
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
                                System.out.println("Erro a tentar fechar o socket: " + ex);
                            }
                        }

                        if (socket.isClosed() || socket == null) {
                            break;
                        }
                    } catch (SocketException e) {
                        System.out.println("Cliente saiu");
                        break;
                    } catch (IOException | ClassNotFoundException ex) {
                        System.out.println("Erro " + ex);
                    }
                }
            //}
                
                System.out.println("VOU SAIR");
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
                    System.out.println("Erro " + e);
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
                    System.out.println("Erro " + e);
                }
            }
        }

        private void removeCliente(String username) {
            for (TCP c : clientes) {
                if (c.getCli().getUsername().equals(username)) {
                    clientes.remove(c);
                    return;
                }
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

            System.out.println("Vou executar logout "+myUsername);
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
                System.out.println("Erro " + ex);
            } catch (UnknownHostException ex) {
                System.out.println("Erro " + ex);
            } catch (IOException ex) {
                System.out.println("Erro " + ex);
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
                    myUsername = splittedList[1];
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("<Regist_Success>");
                    out.flush();
                    
                    // Adiciona um novo objecto do tipo TCP à lista
                    clientes.add(new TCP(socket, cli));
                    cli.setIp(socket.getInetAddress().getHostAddress());
                    cli.setPorto(socket.getPort());
                    
                    msg.getClientesOn().add(cli);

                    try {
                        DatagramSocket _s = new DatagramSocket();

                        ByteArrayOutputStream bout = null;
                        ObjectOutputStream outs = null;
                        DatagramPacket sendP;

                        _s.setSoTimeout(30000);

                        bout = new ByteArrayOutputStream();
                        outs = new ObjectOutputStream(bout);
                        outs.writeObject(msg);
                        outs.flush();

                        sendP = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName("127.0.0.1"), PORT_UDP);

                        _s.send(sendP);
                    } catch (UnknownHostException | NumberFormatException | SocketTimeoutException e) {
                        System.out.println("Erro " + e);
                        return;
                    } catch (IOException e) {
                        System.out.println("Erro " + e);
                        return;
                    }

                    // Create user directory
                    CreateDirectory(defaultFolder, cli.getUsername());
                } else {
                    // Regista-se SEM sucesso
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("<Regist_Failed>");
                    out.flush();
                }
            } catch (IOException ex) {
                System.out.println("Erro " + ex);
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
                    System.out.println("Erro " + ex);
                } catch (IOException ex) {
                    System.out.println("Erro " + ex);
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
                    System.out.println("Erro " + ex);
                } catch (IOException ex) {
                    System.out.println("Erro " + ex);
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
            // download:pathFile
            File file = new File(defaultFolder + File.separator + myUsername + File.separator + splittedList[1]);

            if (file.isFile()) {
                try {
                    // Get the size of the file
                    long length = file.length();

                    ObjectOutputStream ob = new ObjectOutputStream(socket.getOutputStream());
                    Long size = length;
                    ob.writeObject(size);
                    ob.flush();
                    
                    FileInputStream requestedFileInputStream = null;
                    requestedFileInputStream = new FileInputStream(file.getCanonicalPath());
                    System.out.println("Ficheiro " + file.getCanonicalPath() + " aberto para leitura.");

                    int nbytes;
                    byte []fileChunck = new byte[1024];
                    OutputStream out = socket.getOutputStream();

                    while ((nbytes = requestedFileInputStream.read(fileChunck)) > 0) {
                        out.write(fileChunck, 0, nbytes);
                        out.flush();
                    }

                    System.out.println("Transferencia concluida");
                } catch (FileNotFoundException e) {
                    System.out.println("Erro " + e);
                } catch (IOException e) {
                    System.out.println("Erro " + e);
                }
            }
            else {
                ObjectOutputStream ob;
                try {
                    ob = new ObjectOutputStream(socket.getOutputStream());
                    Long size = 0L;
                    ob.writeObject(size);
                    ob.flush();
                } catch (IOException ex) {
                    System.out.println("Erro " + ex);
                }
            }
        }

        private void executarComandoUpload(String[] splittedList) {
            // CRIAR FICHEIRO  upload:create_file:fileName
            File f = null;
            if (splittedList[1].equals("create_file")) {
                f = new File(defaultFolder + File.separator + myUsername + File.separator + splittedList[2]);
                if (!f.exists()) {
                    try {
                        f.createNewFile();

                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject("<File_Created>");
                        outputStream.flush();
                    } catch (IOException ex) {
                        System.out.println("Erro " + ex);
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
                    System.out.println("Erro " + e);
                }
            }
        }

        private void executarComandoChangeDir(String[] splitted) {
            File f = null;
            DirectoryInfo dinfo = new DirectoryInfo(new ArrayList<>(), new ArrayList<>());

            if (!splitted[1].equals(File.separator))
                f = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[1] + File.separator);
            else
                f = new File(defaultFolder + File.separator + myUsername + File.separator);

            String[] names = f.list();

            File f2 = null;
            
            if(names != null) {
                for (String name : names) {
                    f2 = new File(f.getPath()+File.separator+name);
                
                    if (f2.isDirectory())
                        dinfo.getDir().add(name);
                    else
                        dinfo.getFicheirosName().add(name);
                }
            }

            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(dinfo);
                out.flush();
            } catch (IOException ex) {
                System.out.println("Erro " + ex);
            }
        }

        private void executarComandoMover(String[] splitted) {
            // mv:origem:destino
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] arr = splitted[1].split(pattern);
            
            File source = new File(defaultFolder+File.separator+myUsername+File.separator+splitted[1]);
            File dest = new File(defaultFolder+File.separator + myUsername + File.separator +splitted[2]+File.separator+arr[arr.length-1]);
            
            if (!source.isDirectory() && source.exists())
            {
                Path src = Paths.get(source.getPath());
                Path dst = Paths.get(dest.getPath());
            
                try {
                    Files.move(src, dst, REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("Erro " + ex);
                }
            }
        }

        private void executarComandoCopiar(String[] splitted) {
            // cp:origem:destino
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] arr = splitted[1].split(pattern);
            
            File source = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[1]);
            File dest = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[2]+File.separator+arr[arr.length-1]);
            
            if (!source.isDirectory() && source.exists())
            {
                Path src = Paths.get(source.getPath());
                Path dst = Paths.get(dest.getPath());
            
                try {
                    Files.copy(src, dst, REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("Erro " + ex);
                }
            }
        }

        private void executarComandoRemove(String[] splitted) {
            // rm:path
            File f = new File(defaultFolder + File.separator + myUsername + File.separator + splitted[1]);
            
            if (!f.isDirectory() && f.exists())
                f.delete();
        }
        
        private void executarComandoExit() {
            setRunning(false);
        }
    }
}
