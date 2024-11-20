//package com.banreservas.integration.routes;
//
//import static com.banreservas.integration.constants.Constants.*;
//
//import org.apache.camel.LoggingLevel;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.Exchange;
//import org.apache.cxf.binding.soap.SoapFault;
//import org.apache.cxf.interceptor.Fault;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.ctc.wstx.exc.WstxEOFException;
//import org.apache.camel.model.RouteDefinition;
//
//import jakarta.enterprise.context.ApplicationScoped;
//
//@ApplicationScoped
//public class GlobalExceptionHandlingRoute extends RouteBuilder {
//
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlingRoute.class);
//
//    @ConfigProperty(name = "blacklist.service.soap.logging")
//    private boolean loggingService;
//
//    @Override
//    public void configure() throws Exception {
//        // Configuración para errores CXF
//        errorHandler(defaultErrorHandler()
//                .maximumRedeliveries(0)
//                .logHandled(true)
//                .logStackTrace(true));
//
//        // Interceptor para todos los mensajes entrantes
//        interceptFrom()
//                .process(exchange -> {
//                    if (exchange.getIn().getBody() == null) {
//                        throw new SoapFault("El cuerpo de la solicitud está vacío", SoapFault.FAULT_CODE_CLIENT);
//                    }
//                });
//
//        // Manejador específico para errores SOAP y XML
//        onException(SoapFault.class, WstxEOFException.class, Fault.class)
//                .handled(true)
//                .log(LoggingLevel.WARN, logger, "Request SOAP inválido: ${exception.message}")
//                .process(exchange -> {
//                    String customResponse = """
//                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//                       <soap:Body>
//                          <VerificarListasNegrasResponse xmlns="http://tempuri.org/">
//                             <VerificarListasNegrasResult>
//                                <codigoRespuesta>400</codigoRespuesta>
//                                <descripcion>El cuerpo de la solicitud SOAP está vacío o mal formado</descripcion>
//                                <resultado>false</resultado>
//                             </VerificarListasNegrasResult>
//                          </VerificarListasNegrasResponse>
//                       </soap:Body>
//                    </soap:Envelope>
//                    """;
//                    exchange.getMessage().setBody(customResponse);
//                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/xml;charset=UTF-8");
//                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
//                })
//                .markRollbackOnly();
//
//        // Manejador para excepciones generales
//        onException(Exception.class)
//                .handled(true)
//                .log(LoggingLevel.ERROR, logger, "Error general: ${exception.message}")
//                .process(exchange -> {
//                    String errorResponse = """
//                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//                       <soap:Body>
//                          <VerificarListasNegrasResponse xmlns="http://tempuri.org/">
//                             <VerificarListasNegrasResult>
//                                <codigoRespuesta>500</codigoRespuesta>
//                                <descripcion>Error interno del servidor</descripcion>
//                                <resultado>false</resultado>
//                             </VerificarListasNegrasResult>
//                          </VerificarListasNegrasResponse>
//                       </soap:Body>
//                    </soap:Envelope>
//                    """;
//                    exchange.getMessage().setBody(errorResponse);
//                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/xml;charset=UTF-8");
//                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
//                })
//                .markRollbackOnly();
//    }
//}