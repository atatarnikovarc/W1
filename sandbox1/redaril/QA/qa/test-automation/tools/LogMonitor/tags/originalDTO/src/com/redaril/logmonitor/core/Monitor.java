package com.redaril.logmonitor.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.redaril.logmonitor.core.file.FileMonitorDTO;
import com.redaril.logmonitor.util.Helper;
import com.redaril.logmonitor.util.config.Config;

public class Monitor extends Observable implements Runnable {
	private static final Logger logger = Logger.getLogger(Monitor.class.getName());
	private File file;
	private FileMonitorDTO fileMonitorDTO;
	
	public Monitor(String env, String path) {
		file = new File(path);
		fileMonitorDTO = new FileMonitorDTO(env, path);
	}

	public void run() {
		long previousLength = 0;
		while (true) {
			try {
				if (file.canRead()) {
					long currentLength = file.length();
					if (currentLength > previousLength) {// read difference in
															// length,
															// previousLenght
															// File.length()
						String change = getChange(previousLength, currentLength);
						checkChange(change);
						previousLength = currentLength;
					} else if (currentLength < previousLength) {// read from the
																// beginning,
																// previous
																// length to 0
						previousLength = 0;
						String change = getChange(0, currentLength);
						checkChange(change);
					}
				} else {// file inaccessible, stop the thread
					setChanged();
					notifyObservers(fileMonitorDTO);
					clearChanged();
					break;
				}
				Thread.sleep(Config.getInstance().getMonitorInterval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "Can't sleep for a while ");
				e.printStackTrace();
			}
		}
	}

	private void checkChange(String change) {
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
			message.setSubject("LogMonitor: " + fileMonitorDTO.getEnvName()
					+ " - " + getFileName());
			message.setText("The following event happens: \n" + body);
			Transport.send(message);
		} catch (MessagingException mex) {
			logger.log(Level.SEVERE, "Can't send an e-mail ");
			mex.printStackTrace();
		}
		logger.log(Level.SEVERE, "Messages were sent successfuly ");
	}

	private void updateMessageWithRecepients(MimeMessage message) {
		Set<Entry<Object, Object>> emails = Config.getInstance().getEmail()
				.entrySet();
		Iterator<Entry<Object, Object>> iterator = emails.iterator();

		while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			try {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(entry.getKey().toString()));
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "An Address error ");
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "Messaging error ");
				e.printStackTrace();
			}
		}
	}

	private boolean isErrorEvent(String change) {
		return isCollectionContains(change, Helper.getKeywords()) &&
				!isCollectionContains(change, Helper.getExclusionKeywords());
	}
	
	private boolean isCollectionContains(String str, Collection<String> collect) {
		Iterator<String> iterator = collect.iterator();

		while (iterator.hasNext())
			if (str.toLowerCase().contains(iterator.next().toLowerCase()))
				return true;

		return false;
	}

	public String getFileName() {
		return fileMonitorDTO.getFileName();
	}

	private String getChange(long from, long to) {
		byte[] buffer = new byte[(int) (to - from)];
		try {
			RandomAccessFile file = new RandomAccessFile(this.file, "r");
			file.seek(from);
			file.read(buffer);
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Can't find file " + this.file.getAbsolutePath());
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Some I\\O error happens to file " + this.file.getAbsolutePath());
			e.printStackTrace();
		}

		return new String(buffer);
	}
}
