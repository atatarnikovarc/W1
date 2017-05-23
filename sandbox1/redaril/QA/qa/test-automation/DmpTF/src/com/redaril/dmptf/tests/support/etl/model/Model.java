package com.redaril.dmptf.tests.support.etl.model;

import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Model {
    public ETLLog pattern;
    private static Logger LOG;

    public Model() {
        LOG = LoggerFactory.getLogger(Model.class);
    }

    public void writeXML() {
//        Record record = new Record();
//        Marshaller m;
//        Field field = new Field();
//        field.setType("String");
//        field.setValue("1");
//        field.setNumber(2);
//        field.setName("123");
//        record.add(field);
//
//        field = new Field();
//        field.setType("Integer");
//        field.setValue("2");
//        field.setNumber(3);
//        record.add(field);
//
//        ExchangeMappingCall log = new ExchangeMappingCall();
//        log.setSize(10);
//        log.setRecord(record);
//        JAXBContext context = null;
//        Writer w = null;
//        try {
//            context = JAXBContext.newInstance(ExchangeMappingCall.class);
//            m = context.createMarshaller();
//            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            m.marshal(log, System.out);
//            w = new FileWriter(filename);
//            m.marshal(log, w);
//            w.close();
//        } catch (JAXBException e) {
//            e.getMessage();
//        } catch (IOException e) {
//            e.getMessage();
//        }
    }

    public ETLLog getModel(String filename, Class classname) {

        try {
            JAXBContext context = JAXBContext.newInstance(classname);
            Unmarshaller um = context.createUnmarshaller();
            pattern = (ETLLog) um.unmarshal(new FileReader(filename));
            return pattern;
        } catch (JAXBException e) {
            LOG.error(e.getLocalizedMessage());
            return null;
        } catch (FileNotFoundException e) {
            LOG.error("Can't find file = " + filename);
            return null;
        }
    }
}