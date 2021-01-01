package deadlockTest;

import java.io.File;
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
        try {
            //  Execute compile program
            String currentDirectory = System.getProperty("user.dir");
            execProgramAsChildProcess(currentDirectory + "/./" + "compile");
            //    Execute model internally looking for deadlock message
            String programName = getProgramName(currentDirectory);
            execProgramAsChildProcess(currentDirectory + "/./" + programName);
            System.out.println("Nome do programa: " + programName);
            //    Copy regular scheduler files into scheduler directory
            copyFilesToSchedulerDirectory(
                    currentDirectory,
                    SchedulerCodeManager.getSchedulerCode(),
                    PlasmaInterfaceCodeManager.getPlasmaInterfaceCode()
            );
            //    Re-execute compile program
            execProgramAsChildProcess(currentDirectory + "/./" + "compile");
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            //TODO: warn user of error
        }
    }

    private static String getProgramName(String dirPath) {
        File dir = new File(dirPath);
        if (dir.listFiles() == null){
            return null;
        }
        for (File file : dir.listFiles()) {
            if (file.getName().matches(".*[.]go")) {
                return file.getName().split("[.]go")[0];
            }
        }
        return null;
    }

    private static void execProgramAsChildProcess(String newProgram) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(new String[] { newProgram, "" });
        exec.waitFor();
        System.out.println(exec.exitValue());
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
