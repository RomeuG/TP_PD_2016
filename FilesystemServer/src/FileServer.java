import Communication.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import java.util.Scanner;

public class FileServer 
{
    public int PORT_TCP = 7000;
    private String IP_TCP;
    
    private final String defaultFolder = "Storage";
    private List<TCP> clientes;
    
    // Serviço de Directoria (IP e Porto)
    public String IP_UDP = "192.168.1.67";
    public int PORT_UDP = 8000;
    
    public FileServer() {
        // Abrir/ Criar diretório principal do sistema de ficheiros
        if (CreateDirectory(".", defaultFolder))
            System.out.println("Root Created");
        
        clientes = new ArrayList<>();
    }
    
    /**
     * Este método corre a Thread para começar a aceitar clientes e 
     * a Thread para enviar um HeartBeat para o Serviço de Directoria
     * e reutiliza a thread principal para comandos.
     */
    public void start() {
        AtendeClientes tAtende = null;
        
        try {
            tAtende = new AtendeClientes();
            tAtende.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String adminMessage;
        do {
            Scanner sc = new Scanner(System.in);
            System.out.print(" >> ");
            adminMessage = sc.next();
        } while(!"exit".equals(adminMessage));
        
        
        // Encerrar todas as Threads a correr
        try {
            tAtende.setRunning(false);
            tAtende.join();
        } catch (InterruptedException e) {}
        
        System.out.println("[Remote Server] shutdown");
    }
    
    /* Create a Directory with specified 'dName' in path */
    private boolean CreateDirectory(String path, String dName) {
        File theDir = new File(path + "/" + dName);
        
        // if the directory does not exist, create it
        if (!theDir.exists())
          return theDir.mkdir();
        
        return true;
    }
    
    public synchronized void defineIP_TCP(String ipTCP) {
        IP_TCP = ipTCP;
    }

    /**
     * Thread -> Notification To Directory Service
     */
    class NotificationThread extends Thread
    {
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
            synchronized(running) {
                while(running) {
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
                    } catch (UnknownHostException e) {
                        System.out.println("Erro " + e);
                    } catch (NumberFormatException e) {
                        System.out.println("Erro " + e);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Erro " + e);
                    } catch (IOException e) {
                        System.out.println("Erro " + e);
                    }
                    
                    try {
                        running.wait(500);
                    } catch (InterruptedException ex) {}
                }
            }
        }
    }
    
    /**
     * Thread -> Accept new clients
     */
    class AtendeClientes extends Thread 
    {
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
            synchronized(running) {
                System.out.println("[Thread Atendimento Clientes] A aguardar novos clientes");
                
                NotificationThread nt;
                try {
                    nt = new NotificationThread();
                    nt.start();
                    nt.sleep(30000);
                } catch (SocketException | UnknownHostException | InterruptedException e) {
                }
                
                while(running) {
                    try {
                        Socket s = serverSocket.accept();
                        TCP t = new TCP(s);
                        clientes.add(t);
                        AtendeCliente atendeCliente = new AtendeCliente(t);
                        clientThreads.add(atendeCliente);
                        atendeCliente.start();
                        System.out.println( "Lancada thread para atender o cliente " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
                    } catch (IOException ex) {}
                    
                    try {
                        running.wait(500);
                    } catch (InterruptedException ex) {}
                }
            }
            
            System.out.println( "[Thread Atendimento Clientes] Server is not accepting new clients anymore");
            
            // Warn all clients
            for(int i=0; i<clientThreads.size(); i++) {
                try {
                    clientThreads.get(i).setRunning(false);
                    clientThreads.get(i).join();
                } catch (InterruptedException ex) {}
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {}
            
            // Warn Directory Service
            // ...
        }
    }
    
    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread implements Observer 
    {
        private TCP socket;
        private Boolean running;
        
        // Construtor
        public AtendeCliente(TCP socket) {
            this.socket = socket;
            running = true;
        }

        public synchronized void setRunning(Boolean running) {
            this.running = running;
        }
        
        @Override
        public void run() {
            synchronized(running) {
                while(running) {
                    String msg = (String) socket.receiveMessage();
                    String[] splitted = msg.split(":");
                    switch(splitted[0]) {
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
                    
                    try {
                        running.wait(500);
                    } catch (InterruptedException ex) {}
                }
                socket.sendMessage("exit");
                socket.close();
            }
        }
        
        private void executarComandoLogin(String[] splittedList) {
            // new MsgNewClient(null, null);
            // ...
        }
        
        private void executarComandoLogout(String[] splittedList) {
            
        }
        
        private void executarComandoRegistar(String[] splittedList) {
            
        }
        
        private void executarComandoDownload(String[] splittedList) {
            
        }
        
        private void executarComandoUpload(String [] splittedList) {
            
        }
        
        private void executarComandoExit() {
            setRunning(false);
        }
        
        private void sendUpdates() {
            // Enviar Lista de Clientes
            
            // Enviar Lista de Ficheiros
            
        }
        
        @Override
        public void update(Observable o, Object arg) {
            System.out.println(arg);
        }
    }
}
