package org.hilel14.archie.enhabsor.core;

import io.jsonwebtoken.impl.crypto.MacProvider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hilel14.archie.enhabsor.core.storage.AwsBucketConnector;
import org.hilel14.archie.enhabsor.core.storage.SimpleStorageConnector;
import org.hilel14.archie.enhabsor.core.storage.StorageConnector;

/**
 *
 * @author hilel14
 */
public class Config {

    public static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private String archieEnv;
    private StorageConnector storageConnector;
    private Path workFolder;
    private List<String> validFileFormats;
    private List<String> validOcrFormats;
    private String convertCommand;
    private String tesseractCommand;
    private String convertImageCommand;
    private String convertPdfCommand;
    private String jmsQueueName;
    private BasicDataSource dataSource;
    private ActiveMQConnectionFactory jmsFactory;
    private SolrClient solrClient;
    private final Set<String> repositories = new HashSet<>();
    private Key key;

    public Config(Properties properties) throws Exception {
        initConfig(properties);
    }

    public Config() throws Exception {
        Properties properties = loadProperties();
        initConfig(properties);
    }

    private void initConfig(Properties properties) throws Exception {
        // general properties
        archieEnv = properties.getProperty("archie.environment");
        LOGGER.info("archieEnv = {}", archieEnv);
        validFileFormats = Arrays.asList(properties.getProperty("valid.file.formats").split(","));
        // jobs and tasks properties
        convertCommand = properties.getProperty("imagemagic.convert");
        tesseractCommand = properties.getProperty("tesseract");
        validOcrFormats = Arrays.asList(properties.getProperty("valid.ocr.formats").split(","));
        convertImageCommand = properties.getProperty("convert.image.preview");
        convertPdfCommand = properties.getProperty("convert.pdf.preview");
        // jms properties
        jmsQueueName = properties.getProperty("archie.jms.queue");
        createJdbcDataSource(properties);
        createJmsConnectionFactory(properties);
        solrClient = new HttpSolrClient.Builder(properties.getProperty("solr.base")).build();
        //jmsBrokerUrl = p.getProperty("archie.jms.broker");
        //jmsQueueName = p.getProperty("archie.jms.queue");
        createStorageConnector(properties);
        repositories.add("public");
        repositories.add("private");
        repositories.add("secret");
        key = MacProvider.generateKey();
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String name = "/archie.enhabsor.properties";
        LOGGER.info("Loading properties from classpath resource {}", name);
        try (InputStream in = Config.class.getResourceAsStream(name);) {
            properties.load(in);
        }
        return properties;
    }

    private void createJmsConnectionFactory(Properties props) {
        String brokerUrl = props.getProperty("archie.jms.broker");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        jmsFactory = factory;
    }

    private void createJdbcDataSource(Properties props) {
        dataSource = new BasicDataSource();
        getDataSource().setUrl(props.getProperty("archie.jdbc.url"));
        getDataSource().setDriverClassName(props.getProperty("archie.jdbc.driver"));
        getDataSource().setUsername(props.getProperty("archie.jdbc.user"));
        getDataSource().setPassword(props.getProperty("archie.jdbc.password"));
        getDataSource().setMinIdle(5);
        getDataSource().setMaxIdle(10);
        getDataSource().setMaxOpenPreparedStatements(100);
        LOGGER.info("Dastasource created for {}", props.getProperty("archie.jdbc.url"));
    }

    private void createStorageConnector(Properties props) throws Exception {
        workFolder = Paths.get(props.getProperty("work.folder"));
        switch (props.getProperty("storage.connector")) {
            case "simple":
                LOGGER.info("Creating simple storage connector");
                storageConnector = new SimpleStorageConnector();
                storageConnector.setup(props);
                break;
            case "aws":
                LOGGER.info("Creating AWS bucket storage connector");
                storageConnector = new AwsBucketConnector();
                storageConnector.setup(props);
                break;
            default:
                throw new IllegalArgumentException("unknown storage connector: " + props.getProperty("storage.connector"));
        }
    }

    /**
     * @return the validFileFormats
     */
    public List<String> getValidFileFormats() {
        return validFileFormats;
    }

    /**
     * @return the validOcrFormats
     */
    public List<String> getValidOcrFormats() {
        return validOcrFormats;
    }

    /**
     * @return the convertCommand
     */
    public String getConvertCommand() {
        return convertCommand;
    }

    /**
     * @return the tesseractCommand
     */
    public String getTesseractCommand() {
        return tesseractCommand;
    }

    /**
     * @return the convertImageCommand
     */
    public String getConvertImageCommand() {
        return convertImageCommand;
    }

    /**
     * @return the convertPdfCommand
     */
    public String getConvertPdfCommand() {
        return convertPdfCommand;
    }

    /**
     * @return the dataSource
     */
    public BasicDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return the jmsFactory
     */
    public ActiveMQConnectionFactory getJmsFactory() {
        return jmsFactory;
    }

    /**
     * @return the solrClient
     */
    public SolrClient getSolrClient() {
        return solrClient;
    }

    /**
     * @return the jmsQueueName
     */
    public String getJmsQueueName() {
        return jmsQueueName;
    }

    /**
     * @return the archieEnv
     */
    public String getArchieEnv() {
        return archieEnv;
    }

    /**
     * @return the storageConnector
     */
    public StorageConnector getStorageConnector() {
        return storageConnector;
    }

    /**
     * @return the repositories
     */
    public Set<String> getRepositories() {
        return repositories;
    }

    /**
     * @return the workFolder
     */
    public Path getWorkFolder() {
        return workFolder;
    }

    /**
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * @param solrClient the solrClient to set
     */
    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

}
