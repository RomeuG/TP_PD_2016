public interface InterfaceCli 
{
    // métodos do Modelo
    public void register(String username, String password);
    public void login(String username, String password);
    public void logout();
    public void copyFile();
    public void moveFile();
    //public void changeWorkingDirectory();
    //public void getWorkingDirContent();
   // public void getWorkingDirPath();
    //public void getFileContent();
    public void removeFile();
    //public void makeDir();
}
