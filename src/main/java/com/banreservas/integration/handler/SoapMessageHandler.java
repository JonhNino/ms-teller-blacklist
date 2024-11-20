package com.banreservas.integration.handler;


import com.banreservas.integration.fault.SoapFaultBuilder;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import java.util.List;
import java.util.Map;

public class SoapMessageHandler {
    private static final String BANRESERVAS_NS = "http://banreservas.com/integration";

    public static void validateMessage(Message message) {
        if (isEmptyMessage(message)) {
            throw createEmptyMessageFault();
        }
    }

    private static boolean isEmptyMessage(Message message) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        if (hasEmptyContentLength(headers)) {
            return true;
        }

        return message.getContent(java.io.InputStream.class) == null;
    }

    private static boolean hasEmptyContentLength(Map<String, List<String>> headers) {
        if (headers != null) {
            List<String> contentLengthHeader = headers.get("Content-Length");
            return contentLengthHeader != null &&
                    !contentLengthHeader.isEmpty() &&
                    "0".equals(contentLengthHeader.get(0));
        }
        return false;
    }

    private static Fault createEmptyMessageFault() {
        return SoapFaultBuilder.createValidationFault(
                "El mensaje SOAP está vacío o mal formado",
                "SOAP-ERR-001",
                BANRESERVAS_NS,
                400
        );
    }
}