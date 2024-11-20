package com.banreservas.integration.handler.routes;

import com.banreservas.integration.fault.SoapFaultBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;


public class ErrorHandlerConfig {
    private static final String NAMESPACE = "http://tempuri.org/";

    public static void configureErrorHandler(RouteBuilder routeBuilder) {
        routeBuilder.errorHandler(routeBuilder.defaultErrorHandler()
                .maximumRedeliveries(2)
                .redeliveryDelay(1000)
                .logRetryAttempted(true));

        // Manejo de excepciones de validación (400 Bad Request)
        routeBuilder.onException(IllegalArgumentException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error de validación: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            cause.getMessage(),
                            "VAL-400",
                            NAMESPACE,
                            400
                    );
                });

        // Manejo de excepciones de conexión (503 Service Unavailable)
        routeBuilder.onException(java.net.ConnectException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error de conexión: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            "Servicio no disponible: " + cause.getMessage(),
                            "SERVICE_UNAVAILABLE",
                            "http://banreservas.com/integration/faults",
                            503
                    );
                });

        // Manejo de excepciones por defecto (500 Internal Server Error)
        routeBuilder.onException(Exception.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error interno: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            "Error interno del servidor: " + cause.getMessage(),
                            "INTERNAL_ERROR",
                            "http://banreservas.com/integration/faults",
                            500
                    );
                });
    }
}