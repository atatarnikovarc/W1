package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB;

import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.google.GoogleRtbProtocol;
import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.openx.OpenXRtbProtocol;
import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.partners.PartnersProtocol;
import com.redaril.monitoring.MetricFactory;
import org.apache.commons.cli.*;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.JmxUtils;


/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 3/1/13
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunLoad {
    public static void main(String[] args) throws Exception {
        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("ip")
                .withDescription("endpoint ip address (default=127.0.0.1)")
                .hasArg()
                .withArgName("IP")
                .create());
        options.addOption(OptionBuilder.withLongOpt("port")
                .withDescription("endpoint port address (default=9400)")
                .hasArg()
                .withArgName("PORT")
                .create());
        options.addOption(OptionBuilder.withLongOpt("connections")
                .withDescription("number of concurrent connections on each thread (default=1)")
                .hasArg()
                .withArgName("COUNT")
                .create());
        options.addOption(OptionBuilder.withLongOpt("threads")
                .withDescription("number of threads (default=25)")
                .hasArg()
                .withArgName("COUNT")
                .create());
        options.addOption(OptionBuilder.withLongOpt("protocol")
                .withDescription("send request on exchange. Please use OPENX or GOOGLE (default=GOOGLE)")
                .hasArg()
                .withArgName("EXCHANGE")
                .create());
        options.addOption("h", "help", false, "print this message");

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar [jarname]", options);
            return;
        }
        String ip = "127.0.0.1";
        if (line.hasOption("ip")) {
            ip = line.getOptionValue("ip");
        }
        int port = 9400;
        if (line.hasOption("port")) {
            port = Integer.parseInt(line.getOptionValue("port"));
        }
        int numConnections = 1;
        if (line.hasOption("connections")) {
            numConnections = Integer.parseInt(line.getOptionValue("connections"));
        }
        int numThreads = 1;
        if (line.hasOption("threads")) {
            numThreads = Integer.parseInt(line.getOptionValue("threads"));
        }
        String exchange = "GOOGLE";
        if (line.hasOption("protocol")) {
            exchange = line.getOptionValue("protocol");
        }
        final RTBProtocol protocol;
        boolean closeConnection = false;
        if ("GOOGLE".equalsIgnoreCase(exchange)) {
            protocol = new GoogleRtbProtocol(ip);
        } else if ("OPENX".equalsIgnoreCase(exchange)) {
            protocol = new OpenXRtbProtocol(ip);
        } else if ("PIP".equalsIgnoreCase(exchange)) {
            protocol = new PartnersProtocol(ip);
            closeConnection = true;
        } else {
            System.out.println("Unrecognized exchange");
            return;
        }

        final Requester requester = new Requester(ip, port, numConnections, protocol, closeConnection);
        for (int i = 0; i < numThreads; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            requester.sendAndReceive(protocol.createBidRequest());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

    }

    static {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        MetadataMBeanInfoAssembler assembler = new MetadataMBeanInfoAssembler();
        assembler.setAttributeSource(new AnnotationJmxAttributeSource());
        exporter.setAssembler(assembler);

        MetadataNamingStrategy namingStrategy = new MetadataNamingStrategy();
        namingStrategy.setAttributeSource(new AnnotationJmxAttributeSource());
        exporter.setNamingStrategy(namingStrategy);

        exporter.setAutodetectModeName("AUTODETECT_ASSEMBLER");
        exporter.setRegistrationBehaviorName("REGISTRATION_REPLACE_EXISTING");
        exporter.setServer(JmxUtils.locateMBeanServer());
        MetricFactory.setExporter(exporter);

    }


}
