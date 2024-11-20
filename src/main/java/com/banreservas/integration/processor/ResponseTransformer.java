package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class ResponseTransformer implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // Obtener el body actual
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        Map<String, String> request = (Map<String, String>) body.get("request");

        // Crear nuevo mapa con el formato requerido
        Map<String, String> response = new HashMap<>();
        response.put("identificationNumber", request.get("Identificacion"));
        response.put("identificationType", request.get("TipoIdentificacion"));

        // Establecer el nuevo body
        exchange.getIn().setBody(response);
    }
}