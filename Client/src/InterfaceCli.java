public interface InterfaceCli 
{
    // métodos do Modelo
    public boolean register(String username, String password);
    public boolean login(String username, String password);
    public boolean logout(String username);
    public boolean copyFile();
    public boolean moveFile();
    //public void changeWorkingDirectory();
    //public void getWorkingDirContent();
   // public void getWorkingDirPath();
    //public void getFileContent();
    public boolean removeFile();
    //public void makeDir();
}
