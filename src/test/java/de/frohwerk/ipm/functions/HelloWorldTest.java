package de.frohwerk.ipm.functions;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import de.frohwerk.ipm.core.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class HelloWorldTest {
    @Test
    public void helloWorld() throws Exception {
        final JCoDestination destination = JCoDestinationManager.getDestination("Standard");
        final JcoServiceFactory jcoServiceFactory = new JcoServiceFactory(destination);
        final GreetingService greetingService = jcoServiceFactory.createService(GreetingService.class);
        final String result = greetingService.greeting("SAPJCO-Demo");
        logger.info("Greeting from the SAP Application Server: {}", result);
        assertThat(result, is(equalTo("Hallo SAPJCO-Demo")));
    }

    @JcoNamespace("Y_FS1776")
    interface GreetingService {
        @JcoFunction("TEST")
        @JcoProperty("GREETING")
        String greeting(@JcoImportParameter("NAME") final String name);
    }

    private final Logger logger = LoggerFactory.getLogger(HelloWorldTest.class);
}

