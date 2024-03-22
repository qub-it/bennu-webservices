/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu bennu-webservices.
 *
 * FenixEdu bennu-webservices is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu bennu-webservices is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu bennu-webservices.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.qubit.solution.fenixedu.bennu.webservices.services.server;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceServerConfiguration;
import com.sun.xml.ws.transport.Headers;

public class BennuWebServiceHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String WSSE_NS_URI = "http://schemas.xmlsoap.org/ws/2002/12/secext";
    private static final QName QNAME_WSSE_SECURITY = new QName(WSSE_NS_URI, "Security");
    private static final QName QNAME_WSSE_USERNAME_TOKEN = new QName(WSSE_NS_URI, "UsernameToken");
    private static final QName QNAME_WSSE_USERNAME = new QName(WSSE_NS_URI, "Username");
    private static final QName QNAME_WSSE_PASSWORD = new QName(WSSE_NS_URI, "Password");
    private static final QName QNAME_WSSE_NONCE = new QName(WSSE_NS_URI, "Nonce");
    private static final QName QNAME_WSSE_CREATED = new QName(WSSE_NS_URI, "Created");

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // for response message only, true for outbound messages, false for inbound
        if (!isRequest) {
            try {

                WebServiceServerConfiguration configuration = getWebServiceServerConfiguration(
                        ((com.sun.xml.ws.api.server.WSEndpoint) context.get("com.sun.xml.ws.api.server.WSEndpoint"))
                                .getImplementationClass().getName());

                SOAPMessage soapMsg = context.getMessage();
                SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
                SOAPHeader soapHeader = soapEnv.getHeader();

                if (!configuration.isActive()) {
                    generateSOAPErrorMessage(soapMsg, "Sorry webservice is disabled at application level!");
                }

                if (configuration.isAuthenticatioNeeded()) {

                    if (configuration.isUsingWSSecurity()) {
                        if (soapHeader == null) {
                            generateSOAPErrorMessage(soapMsg, "No header in message, unabled to validate security credentials");
                        }

                        String username = null;
                        String password = null;
                        String nonce = null;
                        String created = null;

                        Iterator<Node> childElements = soapHeader.getChildElements(QNAME_WSSE_SECURITY);
                        if (childElements.hasNext()) {
                            Node securityElement = childElements.next();
                            if (securityElement instanceof SOAPElement) {
                                Iterator<Node> usernameTokens =
                                        ((SOAPElement) securityElement).getChildElements(QNAME_WSSE_USERNAME_TOKEN);
                                if (usernameTokens.hasNext()) {
                                    Node node = usernameTokens.next();
                                    if (node instanceof SOAPElement) {
                                        SOAPElement usernameToken = (SOAPElement) node;
                                        username = ((SOAPElement) usernameToken.getChildElements(QNAME_WSSE_USERNAME).next())
                                                .getValue();
                                        password = ((SOAPElement) usernameToken.getChildElements(QNAME_WSSE_PASSWORD).next())
                                                .getValue();
                                        nonce = ((SOAPElement) usernameToken.getChildElements(QNAME_WSSE_NONCE).next())
                                                .getValue();
                                        created = ((SOAPElement) usernameToken.getChildElements(QNAME_WSSE_CREATED).next())
                                                .getValue();
                                    }
                                }
                            }
                        }
                        if (username == null || password == null || nonce == null || created == null) {
                            generateSOAPErrorMessage(soapMsg, "Missing information, unabled to validate security credentials");
                        }

                        SecurityHeader securityHeader = new SecurityHeader(configuration, username, password, nonce, created);
                        if (!securityHeader.isValid()) {
                            generateSOAPErrorMessage(soapMsg, "Invalid credentials");
                        } else {
                            context.put(BennuWebService.SECURITY_HEADER, securityHeader);
                            context.setScope(BennuWebService.SECURITY_HEADER, Scope.APPLICATION);
                        }
                    } else {
                        com.sun.xml.ws.transport.Headers httpHeader = (Headers) context.get(MessageContext.HTTP_REQUEST_HEADERS);
                        String username = null;
                        String password = null;
                        List<String> list = httpHeader.get("authorization");
                        if (list != null) {
                            for (String value : list) {
                                if (value.startsWith("Basic")) {
                                    String[] split = value.split(" ");
                                    try {
                                        String decoded = new String(Base64.getDecoder().decode(split[1]), "UTF-8");
                                        String[] split2 = decoded.split(":");
                                        if (split2.length == 2) {
                                            username = split2[0];
                                            password = split2[1];
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if (username == null || password == null) {
                            generateSOAPErrorMessage(soapMsg, "Missing information, unabled to validate security credentials");
                        }

                        if (!configuration.validate(username, password)) {
                            generateSOAPErrorMessage(soapMsg, "Invalid credentials");
                        }
                    }
                }

            } catch (SOAPException e) {
                System.err.println(e);
            }
        }

        // continue other handler chain
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    private void generateSOAPErrorMessage(SOAPMessage msg, String reason) {
        try {
            SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
            SOAPFault soapFault = soapBody.addFault();
            soapFault.setFaultString(reason);
            throw new SOAPFaultException(soapFault);
        } catch (SOAPException e) {
        }
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private WebServiceServerConfiguration getWebServiceServerConfiguration(String classname) {
        return WebServiceServerConfiguration.readByImplementationClass(classname);
    }
}
