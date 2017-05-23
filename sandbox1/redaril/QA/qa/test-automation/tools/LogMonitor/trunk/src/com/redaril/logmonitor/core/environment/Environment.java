package com.redaril.logmonitor.core.environment;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.redaril.logmonitor.core.file.FileHelper;
import com.redaril.logmonitor.core.monitor.Monitor;
import org.apache.log4j.Logger;

import com.redaril.logmonitor.util.Helper;
import com.redaril.logmonitor.util.config.Config;

public class Environment implements Runnable, Observer {
    private final static Logger logger = Logger.getLogger(Environment.class);

    private Vector<Monitor> monitors = new Vector<Monitor>(10);
    private FileHelper fileHelper;

    final Object lock = new Object();

    public Vector<Monitor> getMonitors() { return this.monitors; }

    public Environment() {
        fileHelper = new FileHelper(this);
        File file = new File(fileHelper.getAvailMonitors());
        file.delete();
        start();
    }

    //class object init method
    private void start() {
        Set<Entry<Object, Object>> envProperties = null;
        Properties env = null;

        //read envs
        while (envProperties == null) {
            env = Config.getInstance().getEnv();
            if (env.entrySet().size() > 0) {
                envProperties = env.entrySet();
            }
            Thread.yield();
        }

        //run all monitors for each env
        synchronized (env) {
            for (Entry<Object, Object> entry : envProperties) {
                runMonitors(entry.getKey().toString(), entry.getValue().toString(),
                        Helper.getLogFilenames(entry.getValue().toString()));
            }
        }
        logger.info("Initial monitors count: " + monitors.size());

        fileHelper.updateFileListIntoFile(this.lock);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(Config.getInstance().getRefreshLogFilelistInterval());
                removeNonEnvFiles();
                refreshFiles();
            } catch (InterruptedException e) {
                logger.error("Can't sleep for a while : " + e);
            } catch (RuntimeException e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    private void removeNonEnvFiles() {//stop a thread and remove from monitors list
        //get envs list
        //got through monitors list, checking whether envs list contains current env
        //if no - stop a thread, remove from monitors list
        //stopAndRemoveMonitor(null);
    }

    private void refreshFiles() {
        // get vector with filenames
        // compare two vectors
        // if new files - create new files vector
        // run monitors for new files vector

        // form actual files list
        Writer writerLog = null;
        try {
            File fileLog = new File(fileHelper.getInaccessFiles());

            List<Monitor> newMonitors = new ArrayList<Monitor>(5);
            boolean isFileAccessible = false;

            Properties envProps = Config.getInstance().getEnv();
            synchronized (envProps) {
                if (envProps.isEmpty()) return;
                writerLog = new BufferedWriter(new FileWriter(fileLog));
                Set<Entry<Object, Object>> envs = envProps.entrySet();
                for (Entry<Object, Object> env : envs) {
                    String envPath = env.getValue().toString();
                    String[] fileNames = Helper.getLogFilenames(envPath);
                    if (fileNames != null && fileNames.length != 0) {
                        List<String> currentKeyFiles = Arrays.asList(fileNames);
                        for (String currentKeyFile : currentKeyFiles) {
                            String fullFileName = env.getValue().toString()
                                    + File.separator + currentKeyFile;
                            newMonitors.add(new Monitor(env.getKey()
                                    .toString(), fullFileName, fullFileName));
                        }
                        isFileAccessible = true;
                    } else
                        writerLog.write(env + " " + envPath + "\n");
                }
            }

            if (isFileAccessible) {
                synchronized (lock) {
                    for (Monitor curr : this.monitors) {
                        for (int j = 0; j < newMonitors.size(); j++) {
                            Monitor newMonDTO = newMonitors.get(j);
                            if ((curr.getEnvName().equals(newMonDTO.getEnvName()))
                                    && (curr.getFileName().equals(newMonDTO.getFileName())))
                                newMonitors.remove(j);
                        }
                    }
                }
                if (!newMonitors.isEmpty()) {
                    // new log files happen
                    for (Monitor dto : newMonitors) {
                        // System.out.println("re-run monitor: " +
                        // dto.getFileName());
                        logger.warn("Create monitor for " + dto.getEnvName() + " " + dto.getFileName());
                        runOneMonitor(dto);
                    }
                    fileHelper.updateFileListIntoFile(this.lock);
                }
            }
        } catch (IOException e) {
            logger.warn("Can't write data into file = " + fileHelper.getInaccessFiles());
        } finally {
            if (writerLog != null) {
                try {
                    writerLog.close();
                } catch (IOException e) {
                    logger.error("Can't close file = " + fileHelper.getInaccessFiles());
                }
            }
        }
    }

    private void runMonitors(String env, String envPath, String[] files) {
        String fullFileName = null;
        if (files != null) {
            for (String file : files) {
                fullFileName = envPath + File.separator + file;
                runOneMonitor(env, fullFileName);
            }
        } else
            logger.warn(env + " log files are inaccessible, evnPath: " + envPath);
    }

    private void runOneMonitor(String env, String fullFileName) {
        Monitor mon = new Monitor(env, fullFileName, fullFileName);
        runOneMonitor(mon);
    }

    private void runOneMonitor(Monitor mon) {
        mon.addObserver(this);
        synchronized (lock) {
            monitors.add(mon);
        }
        mon.start();
        fileHelper.logAvailableMonitor(mon.getEnvName(), mon.getFileName(), true);
    }

    private void removeMonitor(Monitor dto) {
        synchronized (lock) {
            for (Iterator<Monitor> iterator = this.monitors.iterator(); iterator.hasNext(); ) {
                Monitor forDto = iterator.next();
                if ((forDto.getEnvName().equals(dto.getEnvName()))
                        && (forDto.getFileName().equals(dto.getFileName()))) {
                    forDto.done();
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // System.out.println("update envs: " + currentMonitors.size());
        Monitor dto = (Monitor) arg;
        removeMonitor(dto);
        // System.out.println("update envs: " + currentMonitors.size());
    }
}