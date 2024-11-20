package com.banreservas.integration.handler.routes;

import com.banreservas.integration.fault.SoapFaultBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class ErrorHandlerConfig {

    public static void configureErrorHandler(RouteBuilder routeBuilder) {
        routeBuilder.errorHandler(routeBuilder.defaultErrorHandler()
                .maximumRedeliveries(2)
                .redeliveryDelay(1000)
                .logRetryAttempted(true));

        routeBuilder.onException(Exception.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error global: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            "Error en el procesamiento de la solicitud: " + cause.getMessage(),
                            "INTERNAL_ERROR",
                            "http://banreservas.com/integration/faults",
                            500
                    );
                });
    }
}