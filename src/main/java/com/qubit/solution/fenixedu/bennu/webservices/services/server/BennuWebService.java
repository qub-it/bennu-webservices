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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceServerConfiguration;

public abstract class BennuWebService {

    private static final Logger logger = LoggerFactory.getLogger(BennuWebService.class);
    static final String SECURITY_HEADER = "SECURITY_HEADER";

    private static Set<Class<? extends BennuWebService>> REGISTERED_WEBSERVICES = new HashSet<Class<? extends BennuWebService>>();
    private WebServiceServerConfiguration configuration;

    @Resource
    private WebServiceContext wsContext;

    public BennuWebService() {
        super();
        REGISTERED_WEBSERVICES.add(this.getClass());
    }

    protected void logDebug(String message) {
        logger.debug(message);
    }

    protected void logWarning(String message) {
        logger.warn(message);
    }

    protected void logInfo(String message) {
        logger.info(message);
    }

    public static Set<Class<? extends BennuWebService>> getAvailableWebServices() {
        return Collections.unmodifiableSet(REGISTERED_WEBSERVICES);
    }

    protected WebServiceServerConfiguration getWebServiceServerConfiguration() {
        if (configuration == null) {
            configuration = WebServiceServerConfiguration.readByImplementationClass(getClass().getName());
        }
        return configuration;
    }

    public String getKeyStoreName() {
        return getWebServiceServerConfiguration().getDomainKeyStore().getName();
    }

    public boolean isActive() {
        return getWebServiceServerConfiguration().isActive();
    }

    protected WebServiceContext getWsContext() {
        return this.wsContext;
    }

    protected SecurityHeader getSecurityHeader() {
        return (SecurityHeader) wsContext.getMessageContext().get(SECURITY_HEADER);
    }
}
