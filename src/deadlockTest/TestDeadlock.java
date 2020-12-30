package deadlockTest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestDeadlock {

    public static void main(String[] args) {

//        //  Copy files into scheduler directory
        try {
            String currentDirectory = System.getProperty("user.dir");
            copyFilesToSchedulerDirectory(
                    currentDirectory,
                    SchedulerCodeManager.getSchedulerCodeWithNoPrint(),
                    PlasmaInterfaceCodeManager.getPlasmaInterfaceCodeWithNoPrint()
            );
        } catch(IOException e) {
            //TODO: warn user of error
        }
    }

    private static void copyFilesToSchedulerDirectory(String currentDirectory, String schedulerCode, String plasmaInterfaceCode) throws IOException {

        //String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDirectory);
        //get parent directory
        int index = currentDirectory.lastIndexOf("/");
        String parentPath = currentDirectory.substring(0, index);
        System.out.println("Parent directory: " + parentPath);
        String schedulerDirPath = parentPath + "/scheduler";
        System.out.println("Scheduler directory: " + schedulerDirPath);
        //save scheduler path
        String schedulerFilePath = schedulerDirPath + "/scheduler.go";
        System.out.println("Scheduler file path: " + schedulerFilePath);
        //save plasmaInterface path
        String plasmaInterfPath = schedulerDirPath + "/plasmaInterface.go";
        System.out.println("PlasmaInterface file path: " + plasmaInterfPath);
        // copy file scheduler.go
        FileWriter fileWriter = new FileWriter(schedulerFilePath);
        PrintWriter gravarArq = new PrintWriter(fileWriter);
        gravarArq.println(schedulerCode);
        fileWriter.close();
        //copy file plasmaInterface.go
        fileWriter = new FileWriter(plasmaInterfPath);
        gravarArq = new PrintWriter(fileWriter);
        gravarArq.println(plasmaInterfaceCode);
        fileWriter.close();
    }


}
