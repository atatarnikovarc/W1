package com.redaril.logmonitor.core.file;

import com.redaril.logmonitor.core.environment.Environment;
import com.redaril.logmonitor.core.monitor.Monitor;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 4/11/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileHelper {
    private final static Logger logger = Logger.getLogger(FileHelper.class);

    private Environment environment;
    private final static String availMonitors = "logs" + File.separator + "availableMonitors.log";
    private final static String inaccessFiles = "logs" + File.separator + "inaccessFiles.log";

    public String getAvailMonitors() { return availMonitors;}

    public String getInaccessFiles() { return inaccessFiles; }

    public FileHelper(Environment environment) {
        this.environment = environment;
    }

    private Boolean isMonitorLogged(String env, String fullFileName) {
        try {
            FileInputStream fstream = new FileInputStream(availMonitors);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            Boolean isFind = false;
            while (((strLine = br.readLine()) != null) && !isFind) {
                if (strLine.contains(env + " " + fullFileName)) {
                    isFind = true;
                }
            }
            in.close();
            return isFind;
        } catch (IOException e) {
            logger.info("Can't read data from file = " + availMonitors + ". Exception = " + e.getLocalizedMessage());
            return false;
        }

    }

    public static void deleteRecordFromAvMonitors(String toDelete) {
        try {
            FileInputStream fstream = new FileInputStream(availMonitors);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> sourceFile = new ArrayList<String>();
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (!strLine.contains(toDelete)) {
                    sourceFile.add(strLine);
                }
            }
            in.close();
            Writer output;
            File file = new File(availMonitors);
            output = new BufferedWriter(new FileWriter(file));
            for (String aSource : sourceFile) {
                output.write(aSource + "\n");
            }
            output.close();
        } catch (IOException e) {
            logger.info("Can't read data from file = " + availMonitors + ". Exception = " + e.getLocalizedMessage());
        }

    }

    //method which log info about available log-files
    //String env, String fullFileName - info which we want to log into file
    //isAdd = true We add record info file
    //isAdd = false We delete record from file
    public void logAvailableMonitor(String env, String fullFileName, Boolean isAdd) {
        File file = new File(availMonitors);
        try {
            if (isAdd) { //if we add record
                if (file.exists()) {
                    Boolean isLogged = isMonitorLogged(env, fullFileName);
                    if (!isLogged) {
                        FileWriter fstream = new FileWriter(availMonitors, true);
                        Writer output = new BufferedWriter(fstream);
                        output.write(env + " " + fullFileName + "\n");
                        output.close();
                    }
                } else {
                    Writer output = new BufferedWriter(new FileWriter(file));
                    output.write(env + " " + fullFileName + "\n");
                    output.close();
                }
            } else {  //if we delete record
                if (file.exists()) {
                    deleteRecordFromAvMonitors(env + " " + fullFileName);

                }
            }
        } catch (IOException e) {
            logger.error("Can't save data into file " + availMonitors + " . Exception = " + e.getLocalizedMessage());
        }
    }

    public void updateFileListIntoFile(Object lock) {
        //open for write
        //remove all content
        //put line by line actual files
        try {
            FileWriter fstream = new FileWriter("." + File.separator + "logs" + File.separator +
                    "logsUnderMonitoring.log");
            BufferedWriter out = new BufferedWriter(fstream);
            synchronized (lock) {
                for (Monitor monitor : environment.getMonitors()) {
                    out.write(monitor.getEnvName() + ", " +
                            monitor.getFileName() + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
            logger.error("Can't update file list: " + e.getMessage());
        }
    }
}
