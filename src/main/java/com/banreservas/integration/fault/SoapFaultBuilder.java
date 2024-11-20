package com.banreservas.integration.fault;

import org.apache.cxf.interceptor.Fault;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SoapFaultBuilder {
    private static final String SOAP_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    public static Fault createValidationFault(String message, String errorCode, String namespace, int statusCode) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Fault fault = new Fault(new Exception(message));
            fault.setStatusCode(statusCode);
            fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));

            return fault;
        } catch (Exception e) {
            return createBasicFault(message, statusCode);
        }
    }

    private static Fault createBasicFault(String message, int statusCode) {
        Fault fault = new Fault(new Exception(message));
        fault.setStatusCode(statusCode);
        fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));
        return fault;
    }
}
