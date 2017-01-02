public interface InterfaceCli 
{
    // métodos do Modelo
    public boolean register(String username, String password);
    public boolean login(String username, String password);
    public boolean logout();
    public boolean copyFile();
    public boolean moveFile();
    public DirectoryInfo changeWorkingDirectory(String path);
    public void getWorkingDirContent();
   // public void getWorkingDirPath();
    public void getFileContent();
    public boolean removeFile();
    public boolean makeDir(String dir);
    public boolean createFile(String fileName);
}
