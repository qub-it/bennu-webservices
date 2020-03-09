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

package com.qubit.solution.fenixedu.bennu.webservices.domain.webservice;

import org.fenixedu.bennu.core.domain.Bennu;

public abstract class WebServiceConfiguration extends WebServiceConfiguration_Base {

    protected WebServiceConfiguration() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public boolean isImplementationClassAvailable() {
        try {
            Class.forName(getImplementationClass());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public void delete() {
        if (isImplementationClassAvailable()) {
            throw new IllegalStateException(
                    "Can only delete configurations when the implementation class does not exists in the classpath");
        }
        setRootDomainObject(null);
        setDomainKeyStore(null);
        super.deleteDomainObject();
    }

    public static <T extends WebServiceConfiguration> T readByImplementationClass(String implementationClass) {
        return (T) Bennu.getInstance().getWebserviceConfigurationsSet().stream()
                .filter(configuration -> configuration.getImplementationClass().equals(implementationClass)).findFirst()
                .orElse(null);
    }

    public boolean isUsingWSSecurity() {
        return getAuthenticationLevel() != null && getAuthenticationLevel().isUsingWSSecurity();
    }

    public boolean isAuthenticatioNeeded() {
        return getAuthenticationLevel() != null && getAuthenticationLevel() != WebServiceAuthenticationLevel.NONE;
    }
}
