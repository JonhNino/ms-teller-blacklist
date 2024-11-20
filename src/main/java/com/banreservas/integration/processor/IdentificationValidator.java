package com.banreservas.integration.processor;

import com.banreservas.integration.fault.SoapFaultBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class IdentificationValidator implements Processor {
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            Map<String, Object> body = exchange.getIn().getBody(Map.class);
            Map<String, String> request = (Map<String, String>) body.get("request");

            String identificacion = request.get("Identificacion");
            String tipoIdentificacion = request.get("TipoIdentificacion");

            validateIdentification(identificacion, tipoIdentificacion);

            exchange.setProperty("validationPassed", true);
        } catch (Exception e) {
            // Propagar la excepción original para que sea manejada por el ErrorHandler
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    private void validateIdentification(String identificacion, String tipoIdentificacion) {
        if (identificacion == null || identificacion.trim().isEmpty() ||
                tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de identificación y el número es requerido para continuar la operación");
        }

        identificacion = identificacion.trim();

        if (!ALPHANUMERIC_PATTERN.matcher(identificacion).matches()) {
            throw new IllegalArgumentException("La identificación solo puede contener caracteres alfanuméricos");
        }

        switch (tipoIdentificacion) {
            case "Cedula":
            case "Pasaporte":
            case "RNC":
            case "GrupoEconomico":
                break;
            default:
                throw new IllegalArgumentException("Tipo de identificación no válido: " + tipoIdentificacion);
        }
    }
}