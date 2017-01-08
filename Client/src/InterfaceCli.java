public interface InterfaceCli 
{
    // métodos do Modelo
    public boolean register(String username, String password);
    public boolean login(String username, String password);
    public boolean logout();
    public void copyFile(String src, String dst);
    public void moveFile(String src, String dst);
    public boolean removeFile(String path);
    public DirectoryInfo changeWorkingDirectory(String path);
    public boolean getFileContent(String path);
    public boolean makeDir(String dir);
    public boolean createFile(String fileName);
}
