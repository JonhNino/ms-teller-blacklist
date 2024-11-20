package com.banreservas.integration.fault;

import org.apache.cxf.interceptor.Fault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

            //Element detail = createDetailElement(doc, errorCode, message, namespace);

            Fault fault = new Fault(new Exception(message));
            fault.setStatusCode(statusCode);
           // fault.setDetail(detail);
            fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));

            return fault;
        } catch (Exception e) {
            return createBasicFault(message, statusCode);
        }
    }

   /* private static Element createDetailElement(Document doc, String errorCode, String message, String namespace) {
        Element detail = doc.createElementNS(namespace, "ns:ValidationError");

        Element errorCodeElement = doc.createElement("errorCode");
        errorCodeElement.setTextContent(errorCode);

        Element errorMessage = doc.createElement("errorMessage");
        errorMessage.setTextContent(message);

        detail.appendChild(errorCodeElement);
        detail.appendChild(errorMessage);

        return detail;
    }*/

    private static Fault createBasicFault(String message, int statusCode) {
        Fault fault = new Fault(new Exception(message));
        fault.setStatusCode(statusCode);
        fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));
        return fault;
    }
}