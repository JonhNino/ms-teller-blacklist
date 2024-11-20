package com.banreservas.integration.processor;

import com.banreservas.integration.fault.SoapFaultBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

import static io.quarkus.arc.ComponentsProvider.LOG;

@ApplicationScoped
public class FinalResponseProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> aggregatedResponses = exchange.getIn().getBody(Map.class);

        // Log de la respuesta agregada
        LOG.info("Procesando respuesta final: " + aggregatedResponses);

        if (!isValidFinalResponse(aggregatedResponses)) {
            throw SoapFaultBuilder.createValidationFault(
                    "Error en la validación de las respuestas finales",
                    "VALIDATION_ERROR",
                    "http://banreservas.com/faults",
                    500
            );
        }

        exchange.getIn().setBody(aggregatedResponses);
    }

    private boolean isValidFinalResponse(Map<String, Object> responses) {
        try {
            if (responses == null) return false;
            Map<String, Object> defraudadoresResponse = (Map<String, Object>) responses.get("defraudadores");
            Map<String, Object> restringidoResponse = (Map<String, Object>) responses.get("restringido");

            return isValidResponse(defraudadoresResponse) && isValidResponse(restringidoResponse);
        } catch (Exception e) {
            LOG.error("Error validando respuesta final: " + e.getMessage());
            return false;
        }
    }

    private boolean isValidResponse(Map<String, Object> response) {
        if (response == null) return false;
        Map<String, Object> header = (Map<String, Object>) response.get("header");
        return header != null && Integer.valueOf(200).equals(header.get("responseCode"));
    }
}
