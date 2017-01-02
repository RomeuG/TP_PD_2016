public class FilesystemServer 
{
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Syntax: java FilesystemServer <nome_servidor>");
            return;
        }
        
        FileServer s = new FileServer(args[0]);
        s.startServer();
    }
}
