package main;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.sun.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RunXml {
	//переменные для хранения параметров
	private static String xmlFilename = "C:\\Documents and Settings\\Tatarnikov-A\\Рабочий стол\\data\\1253.xml";
	private static String outputXmlFileName = "C:\\Documents and Settings\\Tatarnikov-A\\Рабочий стол\\output\\1155.xml";
	private static String melodyName = "C:\\Documents and Settings\\Tatarnikov-A\\Рабочий стол\\data\\0000.wav";
	private static String bestPartName = "C:\\Documents and Settings\\Tatarnikov-A\\Рабочий стол\\data\\3321b.wav";
	private static String bestPartFile = "3321b.wav";
	private static String copyPath = "C:\\Documents and Settings\\Tatarnikov-A\\Рабочий стол\\output\\1\\";
	private static int startIndex = 1; //не может быть равен 0
	private static int total = 202;
	
	public RunXml() {
		
	}
	
	public static void main(String[] args) throws Exception {
	  RunXml.total = Integer.parseInt(args[0]);
	  RunXml.startIndex = Integer.parseInt(args[1]);
	  RunXml.copyPath = args[2];
	  RunXml.bestPartFile = args[3];
	  RunXml.bestPartName = args[4];
	  RunXml.melodyName = args[5];
	  RunXml.outputXmlFileName = args[6];
	  RunXml.xmlFilename = args[7];
	  	  	  
	  process();
	}

	private static void process() throws ParserConfigurationException,
			SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			FileNotFoundException {
		//открытие файла	
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		  dbf.setValidating(false);
		  DocumentBuilder db = dbf.newDocumentBuilder();
		  Document doc = db.parse(new File(xmlFilename));
		  
		  //подготовка узлов
		  NodeList nl = doc.getElementsByTagName("melody");
		  Node melody = nl.item(0);
		  Node newMelody = null;
		  Node inventory = doc.getDocumentElement();
		  
		  //копирование нужных узлов тега melody
		  for (int i = startIndex; i < total + startIndex; i++) {
		    newMelody = melody.cloneNode(true);
			newMelody.getChildNodes().item(1).setTextContent(RunXml.makeID(i));
			inventory.appendChild(newMelody);
		  }
		  //melody.getChildNodes().item(1).setTextContent(RunXml.makeID(total + startIndex));
		  //doc.removeChild(melody);
		  inventory.removeChild(melody);
	
		  //сохранение xml
		  TransformerFactory tFactory = TransformerFactory.newInstance();
		  Transformer transformer = tFactory.newTransformer();
		  transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputXmlFileName)));
		  
		  //очистка папки куда копируются мелодии архива
		  RunXml.cleanCopyFolder();
		  
		  //копирование файлов для архива
		  RunXml.copyFile(bestPartName, copyPath + bestPartFile);
		  for (int j = startIndex; j < total + startIndex; j++) 
			  RunXml.copyFile(melodyName, copyPath + RunXml.makeID(j) + ".wav");
	}
	
	//метод создания номера мелодии по индексу
	private static String makeID(int i) {
	  String iStr = new Integer(i).toString();
	  if (i < 10) {
	    return "000" + iStr;
	  } else if (i < 100) {
	    return "00" + iStr;
	  } else if (i < 1000) {
	    return "0" + iStr;
	  }
			
	  return iStr;
	}
	
	//метод копирования файла из точки А в точку Б
	private static void copyFile(String srFile, String dtFile){
	  try{
	    File f1 = new File(srFile);
	    File f2 = new File(dtFile);
	    InputStream in = new FileInputStream(f1);
	      
	    //For Append the file.
	    //OutputStream out = new FileOutputStream(f2,true);

	    //For Overwrite the file.
	    OutputStream out = new FileOutputStream(f2);

	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0){
	      out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	    //System.out.println("File copied.");
	  }
	  catch(FileNotFoundException ex){
	    System.out.println(ex.getMessage() + " in the specified directory.");
	    System.exit(0);
	  }
	  catch(IOException e){
	    System.out.println(e.getMessage());      
	  }
    }
	
	//удаление файлов из папки
	private static void cleanCopyFolder() {
		File directory = new File(RunXml.copyPath);
	
		File[] files = directory.listFiles();
		for (File file : files)
		// Delete each file
		if (!file.delete())
          // Failed to delete file
		  System.out.println("Failed to delete "+file);
    }
}
