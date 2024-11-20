//package com.banreservas.integration.processor;
//
//import org.apache.camel.Exchange;
//import org.apache.camel.Processor;
//import org.apache.cxf.binding.soap.SoapFault;
//import jakarta.enterprise.context.ApplicationScoped;
//import javax.xml.namespace.QName;
//
//@ApplicationScoped
//public class SoapFaultProcessor implements Processor {
//
//    @Override
//    public void process(Exchange exchange) {
//        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
//
//        SoapFault soapFault = createSoapFault(cause);
//        exchange.getMessage().setBody(soapFault);
//        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
//    }
//
//    private SoapFault createSoapFault(Exception cause) {
//        SoapFault fault = new SoapFault(
//                "El contenido del mensaje SOAP no puede estar vacío",
//                new QName("http://banreservas.com/integration", "ValidationError")
//        );
//
//        fault.setRole("client");
//        fault.setDetail(createDetailElement());
//
//        return fault;
//    }
//
//    private org.w3c.dom.Element createDetailElement() {
//        // Aquí puedes agregar detalles adicionales al error si lo necesitas
//        return null;
//    }
//}