package com.redaril.dmptf.util.network.appinterface.rest;

import com.google.common.collect.Lists;
import com.redaril.dmp.model.meta.DataOwner;
import com.redaril.dmp.model.meta.DataSource;
import com.redaril.dmp.model.meta.dto.DataConsumerDTO;
import com.redaril.dmp.model.meta.dto.DataSaleDTO;
import com.redaril.dmp.model.meta.dto.DataSaleDataSourceDTO;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 20.12.12
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class RestWrapper {
    private static RestTemplate rest;
    private static Logger LOG;
    private final static String LogSystemProperty = "DmptfLogFile";
    private final static String PATH_CONFIG = "config" + File.separator;
    protected final static String FILE_PROPERTIES_REST = "rest.properties";
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader config;
    private final static String logFile = "rest.log";
    private String env;
    private final static int loopCount = 3;
    private static int port;

    public RestWrapper(String env) {
        FileHelper.getInstance().deleteFile("output" + File.separator + "logs" + File.separator + logFile);
        System.setProperty(LogSystemProperty, logFile);
        ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + "app.properties");
        port = Integer.valueOf(configApp.getProperty("httpPort"));
        LOG = LoggerFactory.getLogger(RestWrapper.class);
        config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
        this.env = env;
        rest = new RestTemplate();
    }

    public DataSaleDTO createDataSale(DataOwner dataOwner, @Nullable Set<DataConsumerDTO> consumers, DataSource dataSource) {
        DataSaleDTO dataSale = new DataSaleDTO();
        dataSale.setConsumers(consumers);
        dataSale.setDataOwnerId(dataOwner.getId());
        dataSale.setExpired(DateWrapper.getDate(1));
        dataSale.setName("auto" + DateWrapper.getRandom());
        DataSaleDataSourceDTO ddDTO = new DataSaleDataSourceDTO();
        ddDTO.setId(dataSource.getId());
        ddDTO.setName(dataSource.getName());
        ddDTO.setPrice(new BigDecimal(1));
        Set<DataSaleDataSourceDTO> setDDDTO = new HashSet<DataSaleDataSourceDTO>();
        setDDDTO.add(ddDTO);
        dataSale.setDataSourcePrices(setDDDTO);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("dataOwnerId", dataOwner.getId().toString());
        ConfigurationLoader configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_REST);
        String ip = configEnv.getProperty(env + "dmpmodel");
        boolean isExec = false;
        int i = 0;
        ResponseEntity entity = null;
        DataSaleDTO newDS = null;
        while (!isExec & i < loopCount) {
            try {


                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
                headers.set("my-name", "3690570");
                // HttpEntity<DataSaleDTO> request = new HttpEntity<DataSaleDTO>(dataSale, headers);
                //MultiValueMap<String, DataSaleDTO> map = new LinkedMultiValueMap<String, DataSaleDTO>();
                //map.add("dataSale", dataSale);
                HttpEntity<DataSaleDTO> request = new HttpEntity<DataSaleDTO>(dataSale, headers);
                AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
                AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
                AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
                ObjectMapper mapper = new ObjectMapper().
                        setAnnotationIntrospector(pair).
                        setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL).              //JsonInclude.Include.NON_NULL
                        configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false).       //SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false
                        configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false).         //SerializationFeature.WRITE_EMPTY_JSON_ARRAYS
                        configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false).
                        configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false).
                        configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true).
                        configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false).
                        configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).
                        configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                        /*.configure(SerializationFeature.INDENT_OUTPUT, true)*/;
                MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
                messageConverter.setObjectMapper(mapper);
                List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
                messageConverters.add(messageConverter);
                rest.setMessageConverters(messageConverters);
                entity = rest.postForEntity("http://" + ip + ":" + port + "/dmpmodel/api/drm/dataSale/?dataOwnerId={dataOwnerId}", request, DataSaleDTO.class, vars);
                newDS = (DataSaleDTO) entity.getBody();
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataSale. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newDS == null) {
            fail("Can't create DataSale.");
        } else LOG.info("DataSale was created successfully. Id = " + newDS.getId());
        return newDS;
    }
}
