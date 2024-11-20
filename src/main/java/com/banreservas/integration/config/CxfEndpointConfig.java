package com.banreservas.integration.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Clase de configuración que produce y configura un endpoint CXF para servicios
 * SOAP en Apache Camel.
 *
 * @author Angel Gonzalez
 * @since 21/10/2024
 * @version 1.0
 */
@ApplicationScoped
public class CxfEndpointConfig {

    @ConfigProperty(name = "blacklist.service.soap.address")
    private String address;

    /**
     * Este método es utilizado para generar un endpoint de CXF que se configura con
     * una dirección (establecida en el campo {@code address}), un formato de datos {@link DataFormat#PAYLOAD}
     * y habilita las características necesarias en el endpoint, incluyendo el manejo de cuerpos vacíos.
     * El {@link CamelContext} proporcionado se asocia con el endpoint para gestionar
     * el ciclo de vida del mismo dentro de la aplicación Camel.
     *
     * @param camelContext El contexto de Camel que será asociado al endpoint.
     * @return El objeto {@link CxfEndpoint} configurado y listo para su uso.
     */
    @Produces
    @ApplicationScoped
    @Named("cxfEndpoint")
    public CxfEndpoint cxfEndpoint(CamelContext camelContext) {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setAddress(address);
        cxfEndpoint.setCamelContext(camelContext);
        cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);
        cxfEndpoint.setLoggingFeatureEnabled(true);
        return cxfEndpoint;
    }
}