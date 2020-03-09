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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;

public class WebServiceServerConfiguration extends WebServiceServerConfiguration_Base {

    protected WebServiceServerConfiguration() {
        super();
        setActive(false);
    }

    public WebServiceServerConfiguration(String implementationClass) {
        this();
        setImplementationClass(implementationClass);;
    }

    public boolean isActive() {
        return super.getActive();
    }

    private Method getValidationMethod() {
        Class implementationClass = null;
        Method m = null;
        try {
            implementationClass = Class.forName(getImplementationClass());
            m =
                    implementationClass != null ? implementationClass.getMethod("validate", new Class[] { String.class,
                            String.class }) : null;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
        }
        return m;
    }

    public boolean isCustomAuthenticationSupported() {
        Method validationMethod = getValidationMethod();
        return validationMethod != null && Modifier.isStatic(validationMethod.getModifiers())
                && validationMethod.getReturnType().equals(boolean.class);
    }

    public boolean validate(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Nor the username nor the pasword can be null");
        }

        boolean valid = false;
        switch (getAuthenticationLevel()) {
        case NONE:
            throw new IllegalStateException(
                    "This should not happen. If webservice is in authentication level None validation should not be performed");
        case PASSWORD:
        case WS_SECURITY:
        case BASIC_AUTH:
            valid = getServiceUsername().equals(username) && getServicePassword().equals(password);
            break;
        case BASIC_AUTH_CUSTOM:
        case WS_SECURITY_CUSTOM:
        case CUSTOM:
            Method validationMethod = getValidationMethod();
            try {
                valid = validationMethod != null && (boolean) validationMethod.invoke(null, new Object[] { username, password });
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        }

        return valid;
    }

    public static Collection<WebServiceServerConfiguration> readAll() {
        return Bennu.getInstance().getWebserviceConfigurationsSet().stream()
                .filter(configuration -> configuration.getClass() == WebServiceServerConfiguration.class)
                .map(WebServiceServerConfiguration.class::cast).collect(Collectors.toList());
    }

}
