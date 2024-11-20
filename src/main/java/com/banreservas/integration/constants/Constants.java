package com.banreservas.integration.constants;

public class Constants {
    // Add to Constants.java
    public static final String ROUTE_REST_APP_BLACKLIST = "direct:blacklist-verification";
    public static final String ROUTE_ERROR_BLACKLIST = "direct:blacklist-error";

    // Add to ConstantsKey.java
    public static final String PRO_IDENTIFICATION = "identification";
    public static final String PRO_IDENTIFICATION_TYPE = "identificationType";
    public static final String ROUTE_ERROR_400 = "direct:error400";
    public static final String CODE_BAD_REQUEST = "400";
    /* Ruta por si llega ocurrir algun error */
    public static final String ROUTE_ERROR_500 = "direct:error-500";
    public static final String ROUTE_ERROR_REST_500 = "direct:error-rest-500";

    public static final String HEADER_CODE_RESPONSE = "CODE_RESPONSE";
    public static final String HEADER_MESSAGE_RESPONSE = "MESSAGE_RESPONSE";
    public static final String CODE_INTERNAL_ERROR = "500";
    public static final String MESSAGE_INTERNAL_ERROR = "Internal server error";

}
