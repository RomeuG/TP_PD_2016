
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class testes {
    public static void main(String[] args) throws IOException {
        File f = new File("E:\\ISEC\\3º ano\\1º semestre\\PDist\\TP\\TP_PD_2016\\Client\\build\\classes\\teratrea");
        if(f.exists()) {
            System.out.println("Estou aqui");
            FileWriter fileWriter = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("username:pass");
            pw.close();
        }
        else{
            f.createNewFile();
            FileWriter fileWriter = new FileWriter(f);
            fileWriter.write("username:pass");
           
        }
    }
}
