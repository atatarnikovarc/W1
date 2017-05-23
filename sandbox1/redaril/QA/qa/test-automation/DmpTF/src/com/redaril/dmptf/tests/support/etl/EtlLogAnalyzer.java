package com.redaril.dmptf.tests.support.etl;

import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.model.Field;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.util.text.RegExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class EtlLogAnalyzer {
    private static Logger LOG;
    private static RegExp reg;

    public EtlLogAnalyzer() {
        LOG = LoggerFactory.getLogger(EtlLogAnalyzer.class);
        reg = new RegExp();
    }

    public Boolean checkLog(String filename, ETLLog pattern) {
        BufferedReader reader = null;
        File file = new File(filename);
        Boolean isChecked;
        Boolean isCheckedRegExp;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            String[] logArray = null;
            Field field;
            //check every string of log to containing  necessary string
            while ((line = reader.readLine()) != null) {
                isChecked = true;
                logArray = line.split("\\t");
                if (logArray.length != pattern.getSize()) {
                    LOG.error("Size of log != size of pattern");
                    LOG.info("ETL check FAILED");
                    isChecked = false;
                }
                Record record = pattern.getRecord();
                for (int i = 0; i < record.getSize(); i++) {
                    field = record.get(i);
                    if (!field.getType().equalsIgnoreCase("JSON")) {
                        if (field.getValue() == null) {
                            isCheckedRegExp = reg.check(logArray[field.getNumber() - 1], field.getType());
                            if (!isCheckedRegExp)
                                isChecked = false;
                        } else {
                            if (!logArray[field.getNumber() - 1].contains(field.getValue())) {
                                isChecked = false;
                            }
                        }
                    } else {
                        String toFind = field.getValue();
                        String source = logArray[field.getNumber() - 1];
                        if (toFind == null) {
                            isChecked = reg.check(logArray[field.getNumber() - 1], field.getType());
                        } else {
                            String[] arrayToFind = toFind.split(",");
                            String[] arraySource = source.substring(1, source.length() - 1).split(",");
                            boolean isFind;
                            for (String strFind : arrayToFind) {
                                isFind = false;
                                for (String anArraySource : arraySource) {
                                    if (anArraySource.contains(strFind)) {
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    isChecked = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isChecked) {
                    LOG.info("Find a record = " + line);
                    LOG.info("ETL check PASSED");
                    return true;
                }
            }
            LOG.info("ETL check FAILED");
            return false;
        } catch (FileNotFoundException e) {
            LOG.error("Can't find local log file");
            return false;
        } catch (IOException e) {
            LOG.error("Can't open local log file");
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("Can't close file", e);
                }
            }
        }
    }
}