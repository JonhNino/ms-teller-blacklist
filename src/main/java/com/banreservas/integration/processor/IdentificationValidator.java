package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class IdentificationValidator implements Processor {
    // Patrón para cédula dominicana (11 dígitos)
    private static final Pattern CEDULA_PATTERN = Pattern.compile("^\\d{11}$");

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        Map<String, String> request = (Map<String, String>) body.get("request");

        String identificacion = request.get("Identificacion");
        String tipoIdentificacion = request.get("TipoIdentificacion");

        validateIdentification(identificacion, tipoIdentificacion);

        // Si la validación pasa, podemos agregar un header o propiedad
        exchange.setProperty("validationPassed", true);
    }

    private void validateIdentification(String identificacion, String tipoIdentificacion) throws Exception {
//        if (identificacion == null || tipoIdentificacion == null) {
//            throw new IllegalArgumentException("Identificación y tipo de identificación son requeridos");
//        }

        if ("Cedula".equals(tipoIdentificacion)) {
            if (!CEDULA_PATTERN.matcher(identificacion).matches()) {
                throw new IllegalArgumentException("Formato de cédula inválido");
            }
            // Aquí podrías agregar validación adicional del dígito verificador si lo necesitas
        } else {
            throw new IllegalArgumentException("Tipo de identificación no soportado: " + tipoIdentificacion);
        }
    }
}
