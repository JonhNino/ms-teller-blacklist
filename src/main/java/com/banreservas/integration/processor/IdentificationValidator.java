package com.banreservas.integration.processor;

import com.banreservas.integration.fault.SoapFaultBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class IdentificationValidator implements Processor {
    private static final Pattern CEDULA_PATTERN = Pattern.compile("^\\d{11}$");
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final int BAD_REQUEST_STATUS = 400;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            Map<String, Object> body = exchange.getIn().getBody(Map.class);
            Map<String, String> request = (Map<String, String>) body.get("request");

            String identificacion = request.get("Identificacion");
            String tipoIdentificacion = request.get("TipoIdentificacion");

            validateIdentification(identificacion, tipoIdentificacion);

            exchange.setProperty("validationPassed", true);
        } catch (IllegalArgumentException e) {
            throw SoapFaultBuilder.createValidationFault(
                    e.getMessage(),
                    "VAL-400",
                    NAMESPACE,
                    BAD_REQUEST_STATUS
            );
        }
    }

    private void validateIdentification(String identificacion, String tipoIdentificacion) {
        if (identificacion == null || tipoIdentificacion == null) {
            throw new IllegalArgumentException("Identificación y tipo de identificación son requeridos");
        }

        if ("Cedula".equals(tipoIdentificacion)) {
            if (!CEDULA_PATTERN.matcher(identificacion).matches()) {
                throw new IllegalArgumentException("Formato de cédula inválido");
            }
        } else {
            throw new IllegalArgumentException("Tipo de identificación no soportado: " + tipoIdentificacion);
        }
    }
}