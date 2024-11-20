package com.banreservas.integration.config;

import com.banreservas.integration.handler.java.SoapMessageHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CxfEndpointConfig {

    @ConfigProperty(name = "blacklist.service.soap.address")
    private String address;

    @Produces
    @ApplicationScoped
    @Named("cxfEndpoint")
    public CxfEndpoint cxfEndpoint(CamelContext camelContext) {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setAddress(address);
        cxfEndpoint.setCamelContext(camelContext);
        cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);

        configureEndpointProperties(cxfEndpoint);
        configureInterceptors(cxfEndpoint);
        configureLogging(cxfEndpoint);

        return cxfEndpoint;
    }

    private void configureEndpointProperties(CxfEndpoint cxfEndpoint) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", false);
        properties.put("exceptionMessageCauseEnabled", true);
        properties.put("allowEmptyInput", false);
     //   properties.put("validationEnabled", true);
        cxfEndpoint.setProperties(properties);
    }

    private void configureInterceptors(CxfEndpoint cxfEndpoint) {
        cxfEndpoint.getInInterceptors().add(
                new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
                    @Override
                    public void handleMessage(Message message) {
                        SoapMessageHandler.validateMessage(message);
                    }
                }
        );
    }

    private void configureLogging(CxfEndpoint cxfEndpoint) {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        cxfEndpoint.getFeatures().add(loggingFeature);
    }
}