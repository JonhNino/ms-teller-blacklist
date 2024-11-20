package com.banreservas.integration.processor;

import com.banreservas.integration.fault.SoapFaultBuilder;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.HashMap;
import java.util.Map;

import static io.quarkus.arc.ComponentsProvider.LOG;

public class ResponseAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        try {
            Map<String, Object> defraudadoresResponse = oldExchange.getIn().getBody(Map.class);
            Map<String, Object> restringidoResponse = newExchange.getIn().getBody(Map.class);

            // Validar las respuestas
            if (!isValidResponse(defraudadoresResponse) || !isValidResponse(restringidoResponse)) {
                throw new Exception("Una o ambas respuestas son inválidas");
            }

            Map<String, Object> aggregatedResponse = new HashMap<>();
            aggregatedResponse.put("defraudadores", defraudadoresResponse);
            aggregatedResponse.put("restringido", restringidoResponse);

            oldExchange.getIn().setBody(aggregatedResponse);
            return oldExchange;

        } catch (Exception e) {
            // Log del error
            LOG.error("Error en agregación: " + e.getMessage());
            throw new RuntimeException("Error en agregación: " + e.getMessage(), e);
        }
    }

    private boolean isValidResponse(Map<String, Object> response) {
        if (response == null) return false;
        Map<String, Object> header = (Map<String, Object>) response.get("header");
        return header != null && Integer.valueOf(200).equals(header.get("responseCode"));
    }
}
