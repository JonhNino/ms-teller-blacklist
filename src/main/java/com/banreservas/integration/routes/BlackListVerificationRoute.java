package com.banreservas.integration.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BlackListVerificationRoute extends RouteBuilder {

    @Inject
    CxfEndpoint cxfEndpoint;

    private static final Logger LOGGER = Logger.getLogger(BlackListVerificationRoute.class);

    @Override
    public void configure() throws Exception {
        // Configuración de la ruta principal
        from(cxfEndpoint)
                .routeId("SoapServiceRoute")
                .log("Procesando solicitud SOAP")
                .unmarshal().jacksonXml()
                .log("Solicitud unmarshalled correctamente: ${body}")
                .end();
    }
}