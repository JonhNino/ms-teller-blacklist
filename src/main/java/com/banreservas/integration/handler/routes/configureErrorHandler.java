/*
package com.banreservas.integration.handler.routes;

import com.banreservas.integration.fault.SoapFaultBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;

public class configureErrorHandler() {
    private void configureErrorHandler() {
        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(2)
                .redeliveryDelay(1000)
                .logRetryAttempted(true));

        onException(Exception.class)
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
*/
