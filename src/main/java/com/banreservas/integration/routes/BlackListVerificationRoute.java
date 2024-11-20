package com.banreservas.integration.routes;

import com.banreservas.integration.constants.Constants;
import com.banreservas.integration.fault.SoapFaultBuilder;
import com.banreservas.integration.handler.routes.ErrorHandlerConfig;
import com.banreservas.integration.processor.FinalResponseProcessor;
import com.banreservas.integration.processor.IdentificationValidator;
import com.banreservas.integration.processor.ResponseAggregationStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.interceptor.Fault;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BlackListVerificationRoute extends RouteBuilder {

    @Inject
    CxfEndpoint cxfEndpoint;
    @ConfigProperty(name = "service.defraudadores.url")
    String defraudadoresUrl;

    @ConfigProperty(name = "service.restringido.url")
    String restringidoUrl;
    private static final Logger LOGGER = Logger.getLogger(BlackListVerificationRoute.class);

    @Override
    public void configure() throws Exception {
        // Error handler global
        ErrorHandlerConfig.configureErrorHandler(this);

        // Ruta principal SOAP
        from(cxfEndpoint)
                .routeId("SoapServiceRoute")
                .log(LoggingLevel.INFO, "Iniciando procesamiento de solicitud SOAP: ${body}")
                .to(Constants.DIRECT_PREPARE_REQUEST)
                .to(Constants.DIRECT_PROCESS_PARALLEL)
                .log(LoggingLevel.INFO, "Procesamiento completado exitosamente");

        // Ruta de preparación de la solicitud
        from(Constants.DIRECT_PREPARE_REQUEST)
                .routeId("RequestPreparationRoute")
                .unmarshal().jacksonXml()
                .log(LoggingLevel.INFO, "Solicitud unmarshalled: ${body}")
                .process(new IdentificationValidator())
                .log(LoggingLevel.INFO, "Validación completada: ${body}")
                .process(new com.banreservas.integration.routes.ResponseTransformer())
                .log(LoggingLevel.INFO, "Transformación completada: ${body}");

        // Ruta de procesamiento paralelo
        from(Constants.DIRECT_PROCESS_PARALLEL)
                .routeId("ParallelProcessingRoute")
                .multicast()
                .parallelProcessing(true)
                .stopOnException()
                .aggregationStrategy(new ResponseAggregationStrategy())
                .to(Constants.DIRECT_CALL_DEFRAUDADORES, Constants.DIRECT_CALL_RESTRINGIDO)
                .end()
                .log(LoggingLevel.INFO, "Respuestas agregadas: ${body}")
                .process(new FinalResponseProcessor());

        // Ruta para llamada a defraudadores
        from(Constants.DIRECT_CALL_DEFRAUDADORES)
                .routeId("DefraudadoresRoute")
                .doTry()
                .to(Constants.DIRECT_PREPARE_HTTP_REQUEST)
                .setHeader("ServiceName", constant("defraudadores"))
                .to("http:" + defraudadoresUrl + "?bridgeEndpoint=true")
                .to(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error en llamada a defraudadores: ${exception.message}")
                .process(exchange -> {
                    throw SoapFaultBuilder.createValidationFault(
                            "Error en el servicio de defraudadores",
                            "DEFRAUDADORES_ERROR",
                            "http://banreservas.com/integration/faults",
                            500
                    );
                });

        // Ruta para llamada a restringido
        from(Constants.DIRECT_CALL_RESTRINGIDO)
                .routeId("RestringidoRoute")
                .doTry()
                    .to(Constants.DIRECT_PREPARE_HTTP_REQUEST)
                    .setHeader("ServiceName", constant("restringido"))
                    .to("hptt:" + restringidoUrl + "?bridgeEndpoint=true")
                    .to(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .doCatch(Exception.class)
                    .log(LoggingLevel.ERROR, "Error en llamada a restringido: ${exception.message}")
                    .process(exchange -> {
                        throw SoapFaultBuilder.createValidationFault(
                                "Error en el servicio de restringido",
                                "RESTRINGIDO_ERROR",
                                "http://banreservas.com/integration/faults",
                                500
                        );
                    });

        // Ruta para preparar request HTTP
        from(Constants.DIRECT_PREPARE_HTTP_REQUEST)
                .routeId("HttpRequestPreparationRoute")
                .marshal().json()
                .log(LoggingLevel.INFO, "Iniciando llamada a ${header.ServiceName} con body: ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        // Ruta para procesar respuesta HTTP
        from(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .routeId("HttpResponseProcessingRoute")
                .unmarshal().json()
                .log(LoggingLevel.INFO, "Respuesta de ${header.ServiceName} recibida: ${body}");
    }
}