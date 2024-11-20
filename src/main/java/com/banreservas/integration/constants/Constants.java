package com.banreservas.integration.constants;

public class Constants {

    // Rutas principales
    public static final String DIRECT_PREPARE_REQUEST = "direct:prepareRequest";
    public static final String DIRECT_PROCESS_PARALLEL = "direct:processParallelRequests";

    // Rutas de servicios
    public static final String DIRECT_CALL_DEFRAUDADORES = "direct:callDefraudadores";
    public static final String DIRECT_CALL_RESTRINGIDO = "direct:callRestringido";

    // Rutas comunes HTTP
    public static final String DIRECT_PREPARE_HTTP_REQUEST = "direct:prepareHttpRequest";
    public static final String DIRECT_PROCESS_HTTP_RESPONSE = "direct:processHttpResponse";

}
