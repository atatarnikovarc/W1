package com.redaril.logmonitor.core.monitor;

import com.redaril.logmonitor.core.analysis.EventController;
import com.redaril.logmonitor.util.Helper;
import com.redaril.logmonitor.util.config.Config;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 4/11/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class MonitorHelper {
    private static final Logger logger = Logger.getLogger(MonitorHelper.class);
    private Monitor monitor;

    MonitorHelper(Monitor monitor) {
        this.monitor = monitor;
    }

    public String getChange(long from, long to) {
        byte[] buffer = new byte[(int) (to - from)];
        RandomAccessFile randomAccessFile = null;
        File file = this.monitor.getFile();
        try {
            if (file == null)
                throw new NullPointerException("file is null");
            randomAccessFile = new RandomAccessFile(file, "r");
            if (randomAccessFile ==null)
                throw new NullPointerException("random access file is null");

            randomAccessFile.seek(from);
            randomAccessFile.read(buffer);
        } catch (FileNotFoundException e) {
            logger.log(Level.ERROR, "Can't find file " + file.getAbsolutePath());
            logger.debug("stopping " + this.monitor.getFileName() + " monitor, file is not accessible");
            this.monitor.stop();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Some I\\O error happens to file " + file.getAbsolutePath());
            logger.debug("stopping " + this.monitor.getFileName() + " monitor, file is not accessible");
            this.monitor.stop();
        } catch (NullPointerException npe) {
            logger.debug("stopping " + this.monitor.getFileName() + " monitor, file is not accessible");
            this.monitor.stop();
        }
        try {
            if (file != null && randomAccessFile != null)
                randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new String(buffer);
    }

    public void checkChange(String change) {
        if (change.length() < Config.getInstance().getMaxChangeSize() &&
                Boolean.parseBoolean(Config.getInstance().getAllowEmailSending())) {
            if (isErrorEvent(change))
                sendEmail(change);
        }
    }

    private void sendEmail(String body) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", Config.getInstance()
                .getMailServerHost());
        properties.setProperty("mail.smtp.port", Config.getInstance().getMailServerPort());
        properties.setProperty("mail.smtp.user", Config.getInstance().getMailUser());
        properties.setProperty("mail.smtp.password", Config.getInstance().getMailPassword());

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.getInstance()
                    .getMailFrom()));
            updateMessageWithRecepients(message);
            message.setSubject("LogMonitor: " + this.monitor.getEnvName()
                    + " - " + this.monitor.getFileName());
            message.setText("======SUMMARY: \n\n\n" + getSummary(body) + "\n\n\n\n\n" + "======FULL TEXT:\n\n\n" + body);
            Transport.send(message);
        } catch (MessagingException mex) {
            logger.log(Level.WARN, "Can't send an e-mail ");
            mex.printStackTrace();
        }
    }

    private void updateMessageWithRecepients(MimeMessage message) {
        Set<Map.Entry<Object, Object>> emails = Config.getInstance().getEmail()
                .entrySet();

        for (Map.Entry<Object, Object> entry : emails) {
            try {
                String email = entry.getKey().toString();
                if (!isEmailForbidden(email))
                    message.addRecipient(Message.RecipientType.TO,
                            new InternetAddress(email));
            } catch (AddressException e) {
                logger.log(Level.WARN, "An Address error ");
                e.printStackTrace();
            } catch (MessagingException e) {
                logger.log(Level.WARN, "Messaging error ");
                e.printStackTrace();
            }
        }
    }

    private boolean isEmailForbidden(String email) {
        Set<Map.Entry<Object, Object>> envRecepient = Config.getInstance().getEnvRecipient().entrySet();
        Vector forbiddenEnvs = new Vector(10);

        for (Map.Entry<Object, Object> entry: envRecepient) {
            if (this.monitor.getEnvName().contains(entry.getKey().toString())) {
                forbiddenEnvs.addAll(Arrays.asList(entry.getValue().toString().split(";")));
            }
        }
        return forbiddenEnvs.contains(email);
    }

    private String getSummary(String body) {
        String lowerBody = body.toLowerCase();
        Vector<String> keywords = Helper.getKeywords();
        final int summaryShift = 500;
        int exceptionStart = 0;

        logger.debug("body: " + lowerBody);
        for (String word: keywords) {
            logger.debug("keyword: " + word);
            exceptionStart = lowerBody.indexOf(word);
            if (exceptionStart >= 0)
                break;
        }

        logger.debug("exceptionStart: " + exceptionStart);
        if (exceptionStart == -1)
            return " NO SUMMARY";

        int start = exceptionStart - summaryShift, end = exceptionStart + summaryShift;

        if (start < 0)
            start = 0;
        if (end > lowerBody.length())
            end = lowerBody.length() - 1;

        return getFormattedSummary(body.substring(start, end));
    }

    private String getFormattedSummary(String summary) {

        return summary;
    }

    private boolean isErrorEvent(String change) {
        boolean keywordContains = isCollectionContains(change, Helper.getKeywords());
        boolean exclusionContains = isCollectionContains(change, Helper.getExclusionKeywords());
        boolean result = keywordContains && !exclusionContains;
        //logger.log(Level.WARN, "==========change: " + change);
        //Collection<String> c = Helper.getExclusionKeywords();
        //for (String a: c) {
        //    logger.log(Level.WARN, "exclusionkeywords : " );
        //}

        //logger.log(Level.WARN, "==========result : " + result + " keywordContains: " + keywordContains + " exclusionContains: " + exclusionContains);
        return result;
    }

    private boolean isCollectionContains(String str, Collection<String> collect) {

        for (String aCollect : collect) {
//            Pattern pattern = Pattern.compile(aCollect, Pattern.CASE_INSENSITIVE);
//            if (pattern.matcher(str).find()) return true;
            if (str.toLowerCase().contains(aCollect.toLowerCase())) {
                //logger.info("String contains keyword: " + aCollect);
                return true;
            }
        }
        return false;
    }
}
