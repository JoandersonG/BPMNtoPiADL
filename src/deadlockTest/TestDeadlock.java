package deadlockTest;

import java.io.*;

public class TestDeadlock {

public static String performDeadlockTest(String workingDirectory) {
//        //  Copy files into scheduler directory
        try {
            copyFilesToSchedulerDirectory(
                    workingDirectory,
                    SchedulerCodeManager.getSchedulerCodeWithNoPrint(),
                    PlasmaInterfaceCodeManager.getPlasmaInterfaceCodeWithNoPrint()
            );
        } catch(IOException e) {
            //TODO: warn user of error
            return "-1";
        }
        try {
            // Change permissions of compile program
            Process exec = Runtime.getRuntime().exec("chmod +x "+ workingDirectory + "/compile");
            exec.waitFor();

            //  Execute compile program
            execProgramAsChildProcess(workingDirectory + "/./" + "compile");
            //    Execute model internally looking for deadlock message
            String programName = getProgramName(workingDirectory);
            String result = execProgramAsChildProcess(workingDirectory + "/./" + programName);
            System.out.println("Result:\n " + result);
            //    Copy regular scheduler files into scheduler directory
            copyFilesToSchedulerDirectory(
                    workingDirectory,
                    SchedulerCodeManager.getSchedulerCode(),
                    PlasmaInterfaceCodeManager.getPlasmaInterfaceCode()
            );
            //    Re-execute compile program
            execProgramAsChildProcess(workingDirectory + "/./" + "compile");
            return result;
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            //TODO: warn user of error
            return "-1";
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

    private static String execProgramAsChildProcess(String newProgram) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(new String[] { newProgram, "" });
        exec.waitFor();
        InputStreamReader isr = new InputStreamReader(exec.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        System.out.println(exec.exitValue());
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.matches("d.*") || line.matches("n.*")) {
                System.out.println(line);
                sb.append(line);
            }
        }
        if (sb.toString().equals("")) {
            return "Não foi detectado nenhum deadlock no modelo";
        }
        isr.close();
        reader.close();
        return sb.toString();
    }

    private static void copyFilesToSchedulerDirectory(String workingDirectory, String schedulerCode, String plasmaInterfaceCode) throws IOException {

        //String workingDirectory = System.getProperty("user.dir");
        System.out.println("Current directory: " + workingDirectory);
        //get parent directory
        int index = workingDirectory.lastIndexOf("/");
        String parentPath = workingDirectory.substring(0, index);
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
