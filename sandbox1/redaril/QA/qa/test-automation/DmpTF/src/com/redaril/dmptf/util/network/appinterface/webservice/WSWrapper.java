package com.redaril.dmptf.util.network.appinterface.webservice;

import com.raasnet.model.adserving.ServiceRegister;
import com.raasnet.util.RedArilApplication;
import com.redaril.dmp.service.*;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;


public class WSWrapper {

    private static Logger LOG;
    private final static String LogSystemProperty = "DmptfLogFile";
    private final static String PATH_CONFIG = "config" + File.separator;
    protected final static String FILE_PROPERTIES_ENV = "app.properties";
    protected static ConfigurationLoader config;
    private static UserManagementService userManagementService;
    private static CategoryService categoryService;
    private static CoreService coreService;
    private static EntityService entityService;
    private static PixelService pixelService;
    private static QualifierService qualifierService;


    public WSWrapper(String env) {
        LOG = LoggerFactory.getLogger(WSWrapper.class);
        Properties extraProps = new Properties();
        config = new ConfigurationLoader(PATH_CONFIG + env + ".properties");
        ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + "app.properties");
        extraProps.setProperty("dmp.webservice.wsdl.location", config.getProperty("webservice.wsdl.location").replace("{port}", configApp.getProperty("httpPort")));
        RedArilApplication.start("TestApp", "localhost", extraProps, new String[]{"appContextProperties.xml", "appContextRedArilCore.xml"});
        LogConfigurer.initLogback();
    }

    public CategoryService getCategoryService() {
        if (categoryService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            categoryService = serviceRegister.getService(CategoryService.class);
        }
        return categoryService;
    }

    public CoreService getCoreService() {
        if (coreService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            coreService = serviceRegister.getService(CoreService.class);
        }
        return coreService;
    }

    public EntityService getEntityService() {
        if (entityService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            entityService = serviceRegister.getService(EntityService.class);
        }
        return entityService;
    }

    public PixelService getPixelService() {
        if (pixelService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            pixelService = serviceRegister.getService(PixelService.class);
        }
        return pixelService;
    }

    public QualifierService getQualifierService() {
        if (qualifierService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            qualifierService = serviceRegister.getService(QualifierService.class);
        }
        return qualifierService;
    }

    public UserManagementService getUserManagementService() {
        if (userManagementService == null) {
            ServiceRegister serviceRegister = RedArilApplication.getBean("ServiceRegister");
            userManagementService = serviceRegister.getService(UserManagementService.class);
        }
        return userManagementService;
    }

}