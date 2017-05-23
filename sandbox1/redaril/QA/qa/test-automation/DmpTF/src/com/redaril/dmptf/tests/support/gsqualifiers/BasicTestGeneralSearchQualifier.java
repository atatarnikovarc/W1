package com.redaril.dmptf.tests.support.gsqualifiers;

import com.redaril.dmptf.tests.support.pip.base.BaseSeleniumTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import com.redaril.dmptf.util.text.RegExp;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.RussianStemmer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.fail;

abstract public class BasicTestGeneralSearchQualifier extends BaseSeleniumTest {

    private static Logger LOG;
    protected static final String PATH_CONFIG = "config" + File.separator;
    protected static HashMap<String, String> dataSourceMap = new HashMap<String, String>();
    protected static String logFile;
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader config;
    protected static ConfigurationLoader configEnv;
    protected static HttpUnitWrapper session;
    protected final static String LogSystemProperty = "DmptfLogFile";
    protected static String ENV;
    protected static String configID;
    protected static OracleWrapper oracledmp;
    protected static WSHelper wsHelper;
    protected final static String pathToSql = "data" + File.separator + "qualifiers" + File.separator + "gs" + File.separator;
    protected final static String getQualifiersByDateSQL = pathToSql + "getQualifiersByDate.sql";
    protected final static String getQualifierTypeSQL = pathToSql + "getQualifierType.sql";
    protected final static String getCategorySQL = pathToSql + "getCategory.sql";
    protected final static String qualifierType = "ENABLED";
    protected static int qualifierTypeId = 0;
    protected GeneralSearchDomain domain;
    protected List<GeneralSearchQualifier> failedQualifiers;
    protected static List<GeneralSearchDomain> domains = new ArrayList<GeneralSearchDomain>();
    protected List<GeneralSearchDomain> failedDomains = new ArrayList<GeneralSearchDomain>();
    protected static List<GeneralSearchDomain> invalidDomains = new ArrayList<GeneralSearchDomain>();
    protected static List<GeneralSearchQualifier> qualifiers;
    protected static boolean isSetup;
    protected static String cstUrl;
    protected static String ucookie = "u";
    protected static String universalUrl;
    private static EnglishStemmer stemmer;
    private static RussianStemmer stemmerRus;
    protected HashMap<String, String> driverInfo = new HashMap<String, String>();
    protected final static String FILE_PROPERTIES_DATES = "date.properties";


    protected void submitForm(String baseUrl, int attempt) {
    }

    protected static void reloadPartners(String env) {
        JMXWrapper jmxWrapper = new JMXWrapper(env, configID, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    protected void getAllGeneralSearchQualifiers() {
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_DATES);
        String begin = config.getProperty("begin");
        String end = config.getProperty("end");
        if (begin.equalsIgnoreCase("") || end.equalsIgnoreCase("")) {
            begin = DateWrapper.getPreviousDateDDMMYYYY(-14);
            end = DateWrapper.getPreviousDateDDMMYYYY(1);
        } else {
            if (begin.length() != 10) {
                LOG.info("Begin Date has wrong format(should be DD.MM.YYYY).");
                LOG.info("Begin Date uses default value = 01.01.2001");
                begin = "01.01.2001";
            }

            if (end.length() != 10) {
                LOG.info("End Date has wrong format(should be DD.MM.YYYY).");
                LOG.info("End Date uses default value = 31.12.2099");
                end = "31.12.2099";
            }
        }

        LOG.info("| Begin date: " + begin);
        LOG.info("| End date: " + end);
        if (qualifiers == null) qualifiers = new ArrayList<GeneralSearchQualifier>();
        LOG.debug("DMP DB connection OK");
        if (qualifiers.size() == 0) {
            try {
                List<String> params = new ArrayList<String>();
                String script;
                ResultSet rset;
                if (qualifierTypeId == 0) getQualifierType();
                params.add(Integer.toString(qualifierTypeId));
                params.add(begin);
                params.add(end);
                script = FileHelper.getInstance().getDataWithParams(getQualifiersByDateSQL, params);
                LOG.info(script);
                rset = oracledmp.executeSelect(script);
                Map<Long, Long> mapDataSourceToPixel = new HashMap<Long, Long>();
                int i = 0;
                while (rset.next()) {
                    GeneralSearchQualifier qualifier = new GeneralSearchQualifier();
                    qualifier.setId(Long.parseLong(rset.getString(1)));
                    qualifier.setParameter(rset.getString(2));
                    qualifier.setExternalId(Long.parseLong(rset.getString(3)));
                    qualifier.setRegex(rset.getString(4));
                    qualifier.setDataSourceId(Long.parseLong(rset.getString(5)));
                    if (rset.getString(6).equalsIgnoreCase("1")) qualifier.setStemmed(true);
                    else qualifier.setStemmed(false);
//                    List<String> paramToCat = new ArrayList<String>();
//                    paramToCat.add(rset.getString(3));
//                    paramToCat.add(rset.getString(5));
//                    script = FileHelper.getInstance().getDataWithParams(getCategorySQL, paramToCat);
//                    ResultSet rsetCats = oracledmp.executeSelect(script);
//                    while (rsetCats.next()) {
//                        qualifier.setLinkedCategoryId(Long.parseLong(rsetCats.getString(1)));
//                        qualifier.setLinkedCategoryName(rsetCats.getString(2));
//                    }
//                    rsetCats.close();
//                    if (mapDataSourceToPixel.containsKey(qualifier.getDataSourceId())) {
//                        qualifier.setPixelId(mapDataSourceToPixel.get(qualifier.getDataSourceId()));
//                    } else {
//                        String pixelId = oracledmp.getPixelIDbyDataSource(String.valueOf(qualifier.getDataSourceId()));
//                        if (pixelId == null) {
//                            pixelId = wsHelper.createDataPixel(String.valueOf(qualifier.getDataSourceId()));
//                            reloadPartners(ENV);
//                        }
//                        mapDataSourceToPixel.put(qualifier.getDataSourceId(), Long.parseLong(pixelId));
//                        qualifier.setPixelId(mapDataSourceToPixel.get(qualifier.getDataSourceId()));
//                    }
                    qualifiers.add(qualifier);
                    i++;
                }
                rset.close();
                oracledmp.closeStatement();
                //add categories
                for (GeneralSearchQualifier gs : qualifiers) {
                    List<String> paramToCat = new ArrayList<String>();
                    paramToCat.add(String.valueOf(gs.getExternalId()));
                    paramToCat.add(String.valueOf(gs.getDataSourceId()));
                    script = FileHelper.getInstance().getDataWithParams(getCategorySQL, paramToCat);
                    rset = oracledmp.executeSelect(script);
                    while (rset.next()) {
                        gs.setLinkedCategoryId(Long.parseLong(rset.getString(1)));
                        gs.setLinkedCategoryName(rset.getString(2));
                    }
                    rset.close();
                    oracledmp.closeStatement();
                }
                //add pixels
                for (GeneralSearchQualifier gs : qualifiers) {
                    if (mapDataSourceToPixel.containsKey(gs.getDataSourceId())) {
                        gs.setPixelId(mapDataSourceToPixel.get(gs.getDataSourceId()));
                    } else {
                        String pixelId = oracledmp.getPixelIDbyDataSource(String.valueOf(gs.getDataSourceId()));
                        if (pixelId == null) {
                            pixelId = wsHelper.createDataPixel(String.valueOf(gs.getDataSourceId()));
                            reloadPartners(ENV);
                        }
                        mapDataSourceToPixel.put(gs.getDataSourceId(), Long.parseLong(pixelId));
                        gs.setPixelId(mapDataSourceToPixel.get(gs.getDataSourceId()));
                    }
                }
                rset.close();
                oracledmp.closeStatement();


                LOG.info("Found " + qualifiers.size() + " qualifiers.");
            } catch (SQLException e) {
                LOG.error("Can't get general search qualifiers. Exception: " + e.getMessage());
            }
        }
    }

    protected void getOneGeneralSearchQualifiers() {
        if (qualifiers == null) qualifiers = new ArrayList<GeneralSearchQualifier>();
        try {
            ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_DATES);
            String begin = config.getProperty("begin");
            String end = config.getProperty("end");
            if (begin.equalsIgnoreCase("") || end.equalsIgnoreCase("")) {
                begin = DateWrapper.getPreviousDateDDMMYYYY(-14);
                end = DateWrapper.getPreviousDateDDMMYYYY(1);
            }
            List<String> params = new ArrayList<String>();
            String script;
            ResultSet rset;
            if (qualifierTypeId == 0) getQualifierType();
            params.add(Integer.toString(qualifierTypeId));
            params.add(begin);
            params.add(end);
            script = FileHelper.getInstance().getDataWithParams(getQualifiersByDateSQL, params);
            rset = oracledmp.executeSelect(script);
            Map<Long, Long> mapDataSourceToPixel = new HashMap<Long, Long>();
            int i = 0;
            while (rset.next() && i < 1) {
                GeneralSearchQualifier qualifier = new GeneralSearchQualifier();
                qualifier.setId(Long.parseLong(rset.getString(1)));
                qualifier.setParameter(rset.getString(2));
                qualifier.setExternalId(Long.parseLong(rset.getString(3)));
                qualifier.setRegex(rset.getString(4));
                qualifier.setDataSourceId(Long.parseLong(rset.getString(5)));
                if (rset.getString(6).equalsIgnoreCase("1")) qualifier.setStemmed(true);
                else qualifier.setStemmed(false);
                List<String> paramToCat = new ArrayList<String>();
                paramToCat.add(rset.getString(3));
                paramToCat.add(rset.getString(5));
                script = FileHelper.getInstance().getDataWithParams(getCategorySQL, paramToCat);
                ResultSet rsetCats = oracledmp.executeSelect(script);
                while (rsetCats.next()) {
                    qualifier.setLinkedCategoryId(Long.parseLong(rsetCats.getString(1)));
                    qualifier.setLinkedCategoryName(rsetCats.getString(2));
                }
                rsetCats.close();
                if (mapDataSourceToPixel.containsKey(qualifier.getDataSourceId())) {
                    qualifier.setPixelId(mapDataSourceToPixel.get(qualifier.getDataSourceId()));
                } else {
                    String pixelId = oracledmp.getPixelIDbyDataSource(String.valueOf(qualifier.getDataSourceId()));
                    if (pixelId == null) {
                        pixelId = wsHelper.createDataPixel(String.valueOf(qualifier.getDataSourceId()));
                        reloadPartners(ENV);
                    }
                    mapDataSourceToPixel.put(qualifier.getDataSourceId(), Long.parseLong(pixelId));
                    qualifier.setPixelId(mapDataSourceToPixel.get(qualifier.getDataSourceId()));
                }
                i++;
                qualifiers.add(qualifier);
            }
            rset.close();
            oracledmp.closeStatement();
            LOG.info("Found " + qualifiers.size() + " qualifiers.");
        } catch (SQLException e) {
            LOG.error("Can't get general search qualifiers. Exception: " + e.getMessage());
        }

    }

    protected void getDomainsByScript(String getDomainsSQL) {
        try {
            List<String> params = new ArrayList<String>();
            String script;
            ResultSet rset;
            if (qualifierTypeId == 0) getQualifierType();
            params.add(Integer.toString(qualifierTypeId));
            script = FileHelper.getInstance().getDataWithParams(getDomainsSQL, params);
            rset = oracledmp.executeSelect(script);
            int err = 0;
            while (rset.next()) {
                GeneralSearchDomain domain = new GeneralSearchDomain();
                domain.setId(rset.getString(1));
                domain.setRegex(rset.getString(2));
                domain.setRequestUrl(rset.getString(3));
                domain.setBaseUrl(rset.getString(4));
                domain.setSearchRegex(rset.getString(5));
                if (domain.getRequestUrl() == null || !domain.getRequestUrl().contains("http:")) {
                    LOG.error("Wrong requestUrl of domain. Regex = " + domain.getRegex() + " ID = " + domain.getId() + " . RequestUrl = " + domain.getRequestUrl());
                    err++;
                    invalidDomains.add(domain);
                } else domains.add(domain);
            }
            LOG.info("Found " + domains.size() + " valid domains.");
            if (err > 0) LOG.error("Found " + err + " invalid domains.");
            rset.close();
            oracledmp.closeStatement();
        } catch (SQLException e) {
            LOG.error("Can't get general search domains. Exception: " + e.getNextException());
            fail("Can't get general search domains. Exception: " + e.getNextException());
        }
    }

    protected List<GeneralSearchDomain> getDomainsByEqual(String regex, String getDomainsSQL) {
        if (domains == null) domains = new ArrayList<GeneralSearchDomain>();
        if (domains.size() == 0) getDomainsByScript(getDomainsSQL);
        List<GeneralSearchDomain> list = new ArrayList<GeneralSearchDomain>();
        for (GeneralSearchDomain domain : domains) {
            if (domain.getRegex().equalsIgnoreCase(regex)) list.add(domain);
        }
        if (list.size() == 0) LOG.error("Can't find any domains with regex = " + regex);
        return list;
    }

    private void getQualifierType() {
        if (qualifierTypeId == 0) {
            try {
                List<String> params = new ArrayList<String>();
                params.add(qualifierType);
                String script = FileHelper.getInstance().getDataWithParams(getQualifierTypeSQL, params);
                ResultSet rset = oracledmp.executeSelect(script);
                while (rset.next()) {
                    qualifierTypeId = Integer.parseInt(rset.getString(1));
                }
                rset.close();
                oracledmp.closeStatement();
            } catch (SQLException e) {
                LOG.error("Can't get qualifierTypeId. Exception: " + e.getMessage());
            }
        }
    }

    private boolean isVowel(Character n) {
        return n == 'a' || n == 'e' || n == 'i' || n == 'o' || n == 'u' || n == 'A' || n == 'E' || n == 'I' || n == 'O' || n == 'U' || n == 'y' || n == 'Y';
    }

    protected String getQueryByRegex(String regex, boolean isStemmed) {
        String reg = "";
        //get words
        char[] arr = regex.toCharArray();
        char ch;
        int first = -1;
        int last = -1;
        String str = "";
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.length; i++) {

            ch = arr[i];
            //find begin or end of word
            if (ch == '[' || ch == ']' || ch == '{' || ch == '}' || ch == '*' || ch == '&' || ch == '[' || ch == '~') {
                if (first == -1) first = i;
                else last = i;
            }
            if (last > -1 && (last - first > 1)) {//if we find end of word, and length of word>0, so we think that it's real word
                for (int k = 1; k < last - first; k++) { //our word consist of chars between first and last chars
                    str = str + arr[first + k];
                }
                first = last;
                last = -1;
            }
            if (!str.equalsIgnoreCase("")) {//add founded word into list
                list.add(str);
                str = "";
            }
            if (last > -1 && (last - first < 2)) {//if we find end of word, and length of word=0, so we think that it's not a word, f.e. *~
                first = last;
                last = -1;
            }


        }
        if (list.isEmpty()) {
            LOG.error("Regex = " + regex + ". Can't find words.");
        }

        if (!isStemmed) {

            if (regex.contains("[") && regex.contains("]")) {
                //inexact matching
                for (String word : list) {
                    if (reg.equalsIgnoreCase("")) reg = "qwerty" + word;
                    else reg = reg + " " + word;
                }
            } else {
                for (String word : list) {
                    if (reg.equalsIgnoreCase("")) reg = word;
                    else reg = reg + " " + word;
                }
            }
        } else {
            String word;
            for (String s : list) {
                if (s.contains("-") || s.contains(".") || s.length() < 3) word = s;
                else if (!RegExp.checkValueByRegex(".*\\p{InCyrillic}+.*", s)) word = getWordByStem(s.toLowerCase());
                else word = getRusWordByStem(s.toLowerCase());
                if (word == null) return null;
                if (reg.equalsIgnoreCase("")) reg = word;
                else reg = reg + " " + word;
            }
        }
        LOG.info("Stemmed = " + isStemmed + ", Phrase from DB = " + regex + " . Query = " + reg);
        return reg;
    }

    private String getWordByStem(String stem) {
        String newWord;
        List<String> list = Arrays.asList("s", "ic", "ance", "ence", "able", "ible", "ate", "ive", "ize", "iti", "al", "ism", "ion", "er", "ous", "ant", "ent", "ment", "ement", "ies", "sses", "ed", "eed", "ing", "abli", "aliti", "biliti", "tional", "ational", "alism", "ation", "ization", "izer", "ator", "iveness", "fulness",
                "icate", "ative", "alize", "ical", "ful", "ness", "e");
        for (String added : list) {
            newWord = stem + added;
            stemmer.setCurrent(newWord);
            stemmer.stem();
            if (stem.equalsIgnoreCase(stemmer.getCurrent())) {
                return newWord;
            }
        }
        if (!isVowel(stem.charAt(stem.length() - 1))) {
            newWord = stem + stem.charAt(stem.length() - 1);
            stemmer.setCurrent(newWord);
            stemmer.stem();
            if (stem.equalsIgnoreCase(stemmer.getCurrent())) {
                return newWord;
            }
        }
        newWord = stem;
        stemmer.setCurrent(newWord);
        stemmer.stem();
        if (stem.equalsIgnoreCase(stemmer.getCurrent())) {
            return newWord;
        }
        LOG.error("Can't find unstemmmed word for stem = " + stem);
        return null;
    }

    private String getRusWordByStem(String stem) {
        String newWord;
        List<String> list = Arrays.asList("в", "ив", "ыв", "вши", "ивши", "ывши", "вшись", "ившись", "ывшись", "ее", "ие", "ое", "ые", "ими", "ыми", "ей", "ий", "ой", "ый", "ем", "им",
                "ом", "ым", "его", "ого", "ему", "ому", "их", "ых", "ею", "ою", "ую", "юю", "ая", "яя", "ла", "ила", "ыла", "на", "ена", "ете", "ите", "йте", "ейте", "уйте", "ли", "или", "ыли",
                "й", "ей", "уй", "л", "ил", "ыл", "ем", "им", "ым", "н", "ен", "ло", "ило", "ыло", "но", "ено", "нно", "ет", "ует", "ит", "ыт", "ют", "уют", "ят", "ны", "ены", "ть", "ить", "ыть",
                "ешь", "ишь", "ю", "ую");
        for (String added : list) {
            newWord = stem + added;
            stemmerRus.setCurrent(newWord);
            stemmerRus.stem();
            if (stem.equalsIgnoreCase(stemmerRus.getCurrent())) {
                return newWord;
            }
        }
        LOG.error("Can't find unstemmmed word for stem = " + stem);
        return null;
    }

    protected void baseSetup() {
        if (!isSetup) {
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(BasicTestGeneralSearchQualifier.class);
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            String baseDomain = configApp.getProperty("baseDomain");
            String port = configApp.getProperty("httpPort");
            config = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            cstUrl = "http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=";
            universalUrl = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
            oracledmp = new OracleWrapper(ENV, "dmp");
            session = new HttpUnitWrapper();
            stemmer = new EnglishStemmer();
            stemmerRus = new RussianStemmer();
            LOG.info("- PROPERTIES -------------------------------");
            LOG.info("| Start time: " + DateWrapper.getDateWithTime(0));
            LOG.info("| ENV: " + ENV);
            LOG.info("| ConfigID: " + configID);
            isSetup = true;
        }
    }

    //at request we should have string "[param]" which we change to real value from regex
    protected boolean checkQualifier(GeneralSearchQualifier qualifier, String request) {
        String query = getQueryByRegex(qualifier.getRegex(), qualifier.isStemmed());
        if (query == null) {
            return false;
        }
        session.deleteAllCookies();
        String ndl = request.replace("[param]", query);
        String encodedNdl = null;
        try {
            //encode to "1 speaker 1"=="1+speaker+1"
            encodedNdl = URLEncoder.encode(ndl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Can't encode ndl. Exception = " + e.getLocalizedMessage());
            fail("Can't encode ndl. Exception = " + e.getLocalizedMessage());
        }
        int i = 0;
        boolean isFind = false;
        String text;
        session.goToUrl(universalUrl + qualifier.getPixelId() + "&ndr=" + encodedNdl);
        String cookie = session.getCookieValueByName(ucookie);
        while (i < 3 && !isFind) {
            text = session.getResponsePage(cstUrl + cookie);
            isFind = text.contains(String.valueOf(qualifier.getLinkedCategoryName()));
            if (!isFind) wait(1000);
            i++;
        }
        if (isFind) {
            //LOG.info("UniversalRequest = " + universalUrl + qualifier.getPixelId() + "&ndr=" + encodedNdl);
            return true;
        } else {
            LOG.error("--- Failed QualifierId = " + qualifier.getId() + " ---");
            LOG.error("CategoryId = " + qualifier.getLinkedCategoryId());
            LOG.error("UCookie = " + cookie);
            LOG.error("query = " + query);
            LOG.error("ndr = " + ndl);
            LOG.error("UniversalRequest = " + universalUrl + qualifier.getPixelId() + "&ndr=" + encodedNdl);
            LOG.error("Check userModel at url = " + cstUrl + cookie);
            LOG.error("--- END ---");
            return false;
        }
    }

    protected void checkQualifiers() {
        failedQualifiers = new ArrayList<GeneralSearchQualifier>();
        int i = 0;
        int count = qualifiers.size();
        String request = domain.getRequestUrl();
        for (GeneralSearchQualifier qualifier : qualifiers) {
            LOG.debug("-----Check " + i + " qualifier from " + count + ".------");
            boolean isChecked = checkQualifier(qualifier, request.replace("[domain]", domain.getRegex()));
            if (!isChecked) {
                failedQualifiers.add(qualifier);
                LOG.info(i + 1 + ". Qualifier ID = " + qualifier.getId() + " FAILED.");
            } else LOG.info(i + 1 + ". Qualifier ID = " + qualifier.getId() + " PASSED.");
            i++;
        }
        if (failedQualifiers.size() != 0) {
            int k = 1;
            for (GeneralSearchQualifier qualifier : failedQualifiers) {
                LOG.info(k + ". Failed Qualifier ID = " + qualifier.getId() + ", DataSourceId = " + qualifier.getDataSourceId() + ", Category Id = " + qualifier.getLinkedCategoryId() + ", PixelId = " + qualifier.getPixelId());
                k++;
            }
        }
    }

    private boolean checkNdl(String baseUrl, GeneralSearchDomain domain) {
        int attempt = 0;
        String exampleUrl = "localhost";
        String curUrl;
        boolean isChecked = false;
        while (!isChecked && attempt < 3) {
            isChecked = false;
            try {
                webDriverWrapper.getPage("http://" + exampleUrl);
            } catch (WebDriverException e) {
                LOG.error("Browser get exception = " + e.getLocalizedMessage());
                LOG.info("Try to restart Selenium Webdriver");
                webDriverWrapper.tearDown();
                webDriverWrapper = new WebDriverWrapper(ENV);
                webDriverWrapper.getDriver(driverInfo, null, null);
                webDriverWrapper.elementFinder.setDriver(webDriverWrapper.getCurrentDriver());
                webDriverWrapper.getPage("http://" + exampleUrl);
            }
            webDriverWrapper.deleteAllCookies();
            submitForm(baseUrl, attempt);
            curUrl = webDriverWrapper.getCurrentUrl();
            if (!curUrl.equals("") && !curUrl.contains("localhost")) {
                String url = curUrl;
                String regex = domain.getSearchRegex();
                //remove protocol from url
                if (url.contains("/") && url.contains(":")) {
                    url = url.substring(url.indexOf(":") + 3);
                    //remove domain from url
                    url = url.substring(url.indexOf("/"));
                } else return false;
                isChecked = RegExp.checkValueByRegex(regex, url);
            }
            attempt++;
        }
        return isChecked;
    }

    protected void checkDomains() {
        int i = 0;
        boolean isChecked;
        for (GeneralSearchDomain domain : domains) {
            if (domain.getBaseUrl() == null || !domain.getBaseUrl().contains("http")) {
                LOG.error("Wrong baseUrl of domain. Regex = " + domain.getRegex() + " ID = " + domain.getId() + " . RequestUrl = " + domain.getRequestUrl());
                invalidDomains.add(domain);
            } else {
                String baseUrl = domain.getBaseUrl();
                isChecked = checkNdl(baseUrl, domain);
                if (!isChecked) {
                    webDriverWrapper.deleteAllCookies();
                    String curUrl = webDriverWrapper.getCurrentUrl();
                    LOG.info(i + 1 + ". Domain " + domain.getRegex() + " ID = " + domain.getId() + " FAILED. Found URL = " + curUrl);
                    failedDomains.add(domain);
                } else LOG.info(i + 1 + ". Domain " + domain.getRegex() + " ID = " + domain.getId() + " PASSED.");
            }
            i++;
        }
        if (failedDomains.size() != 0) {
            int k = 1;
            LOG.error("======Failed domains");
            for (GeneralSearchDomain domain : failedDomains) {
                LOG.info(k + ". Failed Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl() + ", BaseUrl = " + domain.getBaseUrl());
                k++;
            }
        }
        if (invalidDomains.size() != 0) {
            int k = 1;
            LOG.error("======Invalid domains");
            for (GeneralSearchDomain domain : invalidDomains) {
                LOG.info(k + ". Invalid Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl() + ", BaseUrl = " + domain.getBaseUrl());
                k++;
            }
        }
    }
}
