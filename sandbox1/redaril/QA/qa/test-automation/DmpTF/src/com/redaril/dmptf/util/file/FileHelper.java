package com.redaril.dmptf.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.fail;

public class FileHelper {
    private static Logger LOG;
    private static FileHelper instance = new FileHelper();

    public static FileHelper getInstance() {
        return instance;
    }

    private FileHelper() {
        LOG = LoggerFactory.getLogger(FileHelper.class);
    }

    public String getDataWithParams(String scriptName, List<String> params) {
        String line;
        String data = "";
        File scriptFile = new File(scriptName);
        if (scriptFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(scriptFile));
                while ((line = bufferedReader.readLine()) != null) {
                    for (int i = 0; i < params.size(); i++) {

                        if (line.contains("{" + i + "}")) {
                            line = line.replace("{" + i + "}", params.get(i));
                        }
                    }
                    data = data.concat(line.trim() + " ");
                }
                bufferedReader.close();
            } catch (IOException e) {
                LOG.error("Can't get data from file. Filename = " + scriptFile);
                fail("Can't get data from file. Filename = " + scriptFile);
            }
        } else {
            LOG.error("Can`t find file: " + scriptName);
            fail("Can`t find file: " + scriptName);
        }
        LOG.debug(data);
        return data;
    }

    public String getDataWithoutParams(String scriptName) {
        String line;
        String data = "";
        File scriptFile = new File(scriptName);
        if (scriptFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(scriptFile));
                while ((line = bufferedReader.readLine()) != null) {
                    data = data.concat(line.trim() + " ");
                }
                bufferedReader.close();
            } catch (IOException e) {
                LOG.error("Can't get script from file. Filename = " + scriptName);
                fail("Can't get script from file. Filename = " + scriptName);
            }
        } else {
            LOG.error("Can`t find file: " + scriptName);
        }
        return data.trim();
    }

    public void createFile(String filename, List<String> source) {
        try {
            Writer output;
            File file = new File(filename);
            output = new BufferedWriter(new FileWriter(file));
            for (String aSource : source) {
                output.write(aSource + "\n");
            }
            output.close();
        } catch (IOException e) {
            LOG.error("Can't create file " + filename + " at local machine. Exception = " + e.getLocalizedMessage());
        }
    }

    public void copyFile(String source, String dest) {
        FileChannel sourcechannel = null;
        FileChannel destchanell = null;
        try {
            File sourceFile = new File(source);
            File destFile = new File(dest);
            sourcechannel = new FileInputStream(sourceFile).getChannel();
            destchanell = new FileOutputStream(destFile).getChannel();
            destchanell.transferFrom(sourcechannel, 0, sourcechannel.size());
            sourcechannel.close();
            destchanell.close();
        } catch (IOException e) {
            LOG.error("Can't create file " + dest + " at local machine. Exception = " + e.getLocalizedMessage());
        }


    }

    public void deleteFile(String filename) {
        File f1 = new File(filename);
        f1.delete();
    }

    public List<String> getDataFromFile(String filename) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> data = new ArrayList<String>();
            String strLine;
            while ((strLine = br.readLine()) != null) {
                data.add(strLine);
            }
            in.close();
            return data;
        } catch (IOException e) {
            LOG.error("Can't read data from file. Exception = " + e.getLocalizedMessage());
            fail("Can't read data from file. Exception = " + e.getLocalizedMessage());
        }
        return null;
    }

    public List<String> getDataFromFileAfterCriteria(String filename, String criteria) {
        List<String> data = getDataFromFile(filename);
        return data.subList(data.lastIndexOf(criteria), data.size());
    }

    //return hashmap<number_of_string, string>
    public HashMap<Integer, String> findAtFile(String filename, String toFind) {
        try {
            HashMap<Integer, String> find = new HashMap<Integer, String>();
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> data = new ArrayList<String>();
            String strLine;
            Integer i = 0;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(toFind)) {
                    find.put(i, strLine);
                }
                data.add(strLine);
                i++;
            }
            in.close();
            return find;
        } catch (IOException e) {
            LOG.error("Can't read data from file. Exception = " + e.getLocalizedMessage());
            fail("Can't read data from file. Exception = " + e.getLocalizedMessage());
        }
        return null;
    }

    public void putIntoFile(String filename, HashMap<Integer, String> map) {
        try {

            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> listFile = new ArrayList<String>();
            String strLine;
            Integer i = 0;
            while ((strLine = br.readLine()) != null) {
                if (map.get(i) != null) {
                    listFile.add(map.get(i));
                } else {
                    listFile.add(strLine);
                }
                i++;
            }
            in.close();
            File file = new File(filename);
            file.delete();
            createFile(filename, listFile);
        } catch (IOException e) {
            LOG.error("Can't write data into file. Exception = " + e.getLocalizedMessage());
            fail("Can't write data into file. Exception = " + e.getLocalizedMessage());
        }
    }

    public void findAndReplaceStringAtFile(String filename, String toFind, String toReplace) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            List<String> sourceFile = new ArrayList<String>();
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(toFind)) {
                    strLine = toReplace;
                }
                sourceFile.add(strLine);
            }
            in.close();
            createFile(filename, sourceFile);
        } catch (IOException e) {
            LOG.error("Can't read data from file. Exception = " + e.getLocalizedMessage());
            fail("Can't read data from file. Exception = " + e.getLocalizedMessage());
        }
    }

    public void findAndReplaceAtFile(String filename, String toFind, String toReplace) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            List<String> sourceFile = new ArrayList<String>();
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(toFind)) {
                    strLine = strLine.replace(toFind, toReplace);
                }
                sourceFile.add(strLine);
            }
            in.close();
            createFile(filename, sourceFile);
        } catch (IOException e) {
            LOG.error("Can't read data from file. Exception = " + e.getLocalizedMessage());
            fail("Can't read data from file. Exception = " + e.getLocalizedMessage());
        }
    }

    public Long getFileSize(String filename) {
        File file = new File(filename);
        return file.length();
    }
}
