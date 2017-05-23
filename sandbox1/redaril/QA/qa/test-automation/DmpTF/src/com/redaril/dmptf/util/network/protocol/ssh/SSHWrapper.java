package com.redaril.dmptf.util.network.protocol.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static org.junit.Assert.fail;

//this class uses to get ETL log from remote server
public class SSHWrapper {

    private static Logger LOG;
    private Session session;
    private final static Integer delay = 60;
    private String keyFile;
    private String login;
    private String password;
    private String host;

    public String getHost() {
        return host;
    }

    public SSHWrapper(String host) {
        this.host = host;
        LOG = LoggerFactory.getLogger(SSHWrapper.class);
        login = "root";
    }

    public SSHWrapper(String host, String keyFile) {
        this.host = host;
        LOG = LoggerFactory.getLogger(SSHWrapper.class);
        this.keyFile = keyFile;
        login = "root";
    }

    public SSHWrapper(String host, String login, String password) {
        this.host = host;
        LOG = LoggerFactory.getLogger(SSHWrapper.class);
        this.login = login;
        this.password = password;
    }

    //source - which file try to find
    //target - which file should create at local machine
    //pathfile - where try to find at remote machine
    //like - if true..try to find by exactly matching
    //       if false..try to find by not exactly matching
    public Boolean getFile(String source, String target, String pathFile, Boolean like) {
        try {
            ChannelSftp sftpChannel = getSFTPChannel();
            Vector<ChannelSftp.LsEntry> list;
            checkFileExist(source, pathFile, true, like);
            if (like) list = sftpChannel.ls(pathFile + source);
            else list = sftpChannel.ls(pathFile + source + "*");
            //if we have more than 1 log file, it's a mistake
            if (list.isEmpty()) {
                LOG.error("File was not found.");
                return false;
            } else if (list.size() > 1) {
                LOG.error("Environment configured incorrectly. There are several files found by criteria");
                return false;
            } else {
                //copy etl log into local file
                sftpChannel.get(pathFile + list.get(0).getFilename(), target);
                sftpChannel.disconnect();
                return true;
            }

        } catch (SftpException e) {
            LOG.error("Can't get file " + source + " by sftp.");
            return false;
        }

    }

    //method which wait while file is available/unavailable depends of Boolean isAvail
    //isAvail=true, wait until file will be available
    //isAvail=false, wait until file will be unavailable
    public boolean checkFileExist(String filename, String pathFile, Boolean isAvail, Boolean like) {
        LOG.debug("Check available(unavailable)file = " + filename);
        Vector<ChannelSftp.LsEntry> list = null;
        Boolean isFind = !isAvail;
        Integer i = 1;
        ChannelSftp sftpChannel = null;
        while (!isFind.equals(isAvail) && i < delay) {
            try {
                sftpChannel = getSFTPChannel();
                if (like) {
                    list = sftpChannel.ls(pathFile + filename);
                    LOG.info("Try to find file = " + pathFile + filename + ", wait " + i + " sec");
                } else {
                    list = sftpChannel.ls(pathFile + filename + "*");
                    LOG.info("Try to find file = " + pathFile + filename + "*" + ", wait " + i + " sec");
                }
                isFind = list.size() > 0;
                sftpChannel.disconnect();
            } catch (SftpException e) {
                isFind = false;
            } finally {
                i++;
                try {
                    Thread.sleep(1000);//sometimes system may works too slowly,so we should make a pause
                } catch (InterruptedException e1) {
                    LOG.error("Get InterruptedException while waiting for file.");
                }
            }
        }
        if (!isFind.equals(isAvail)) {
            LOG.error("checkFileExist can't find file.");
            return false;
        }
        else return true;
    }

    private Session getSession() {
        if (session == null || !session.isConnected()) {
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(login, host, 22);
                if (keyFile != null) {  //login by keyFile
                    jsch.addIdentity(keyFile);
                } else if (password != null) {   //login by login and password
                    session.setPassword(password);
                } else {                                      //login by default root 812redaril
                    session.setPassword("812redaril");
                }
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
            } catch (JSchException e) {
                LOG.error("Can't connect to server = " + host + ". Exception = " + e.getMessage());
            }
        }
        return session;
    }

    private ChannelSftp getSFTPChannel() {
        ChannelSftp sftpChannel;
        int attempt = 0;
        while (attempt < 3) {
            try {
                sftpChannel = (ChannelSftp) getSession().openChannel("sftp");
                sftpChannel.connect();
                return sftpChannel;
            } catch (JSchException e) {
                session.disconnect();
                attempt++;
            }
        }
        LOG.error("Can't connect to host by SFTP.");
        fail("Can't connect to host by SFTP.");
        return null;

    }

    private ChannelExec getExecChannel() {
        ChannelSftp sftpChannel;
        int attempt = 0;
        while (attempt < 3) {
            try {
                ChannelExec channel = (ChannelExec) getSession().openChannel("exec");
                return channel;
            } catch (JSchException e) {
                session.disconnect();
                attempt++;
            }
        }
        LOG.error("Can't connect to host by exec.");
        fail("Can't connect to host by exec.");
        return null;

    }

    public void putFile(String source, String target, String pathFile) {
        int attempt = 0;
        boolean isExecuted = false;
        while (attempt < 3 && isExecuted == false) {
            try {
                ChannelSftp sftpChannel = getSFTPChannel();
                sftpChannel.put(source, pathFile + target);
                isExecuted = true;
            } catch (SftpException e) {
                session.disconnect();
                attempt++;
            }
        }
        if (attempt >= 3) {
            LOG.error("Can't put file" + target + ". Path = " + pathFile);
            fail("Can't put file " + target + ". Path = " + pathFile);
        }
    }

    public void executeCommand(String command, @Nullable Integer wait) {
        int attempt = 0;
        boolean isExecuted = false;
        while (attempt < 3 && isExecuted == false) {
            try {
                ChannelExec channel = getExecChannel();
                InputStream in = channel.getErrStream();
                channel.setCommand(command);
                channel.setInputStream(null);
                channel.setErrStream(System.err);
                channel.connect();
                if (wait!= null)wait(wait);
                channel.disconnect();
                isExecuted = true;
            } catch (JSchException e) {
                session.disconnect();
                attempt++;
            } catch (IOException e) {
                session.disconnect();
                attempt++;
            }
        }
        if (attempt >= 3) {
            LOG.error("Can't execute command " + command);
            fail("Can't execute command" + command);
        }
    }

    public void tearDown(String filename) {
        LOG.info("Delete local files.");
        Boolean isDelete = (new File(filename)).delete();
        if (!isDelete) {
            LOG.error("Can not delete local files.");
        }
    }

    private static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
