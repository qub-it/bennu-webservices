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

package com.qubit.solution.fenixedu.bennu.webservices.ui.management.webservicesServers;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceServerConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.ui.BennuWebservicesController;
import com.qubit.solution.fenixedu.bennu.webservices.ui.WebservicesBaseController;

@SpringFunctionality(app = BennuWebservicesController.class, title = "label.title.management.webservices",
        accessGroup = "#managers")
@RequestMapping("/webservices/management/webservicesservers/webserviceserverconfiguration")
public class WebServiceServerConfigurationController extends WebservicesBaseController {

    @RequestMapping
    public String home(Model model) {
        return "forward:/webservices/management/webservicesservers/webserviceserverconfiguration/";
    }

    private WebServiceServerConfiguration getWebServiceServerConfiguration(Model m) {
        return (WebServiceServerConfiguration) m.asMap().get("webServiceServerConfiguration");
    }

    private void setWebServiceServerConfiguration(WebServiceServerConfiguration webServiceServerConfiguration, Model m) {
        m.addAttribute("webServiceServerConfiguration", webServiceServerConfiguration);
    }

    @RequestMapping(value = "/")
    public String search(
            @RequestParam(value = "implementationclass", required = false) java.lang.String implementationClass,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "domainkeystore", required = false) com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore,
            Model model) {
        List<WebServiceServerConfiguration> searchwebserviceserverconfigurationResultsDataSet =
                filterSearchWebServiceServerConfiguration(implementationClass, active, domainKeyStore);

        model.addAttribute("searchwebserviceserverconfigurationResultsDataSet", searchwebserviceserverconfigurationResultsDataSet);
        model.addAttribute("WebServiceServerConfiguration_domainKeyStore_options",
                new ArrayList<com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore>(Bennu.getInstance()
                        .getDomainKeyStoresSet()));
        return "webservices/management/webservicesservers/webserviceserverconfiguration/search";
    }

    private List<WebServiceServerConfiguration> getSearchUniverseSearchWebServiceServerConfigurationDataSet() {
        return new ArrayList<WebServiceServerConfiguration>(WebServiceServerConfiguration.readAll());
    }

    private List<WebServiceServerConfiguration> filterSearchWebServiceServerConfiguration(java.lang.String implementationClass,
            Boolean active, com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore) {

        return getSearchUniverseSearchWebServiceServerConfigurationDataSet()
                .stream()
                .filter(webServiceServerConfiguration -> implementationClass == null
                        || implementationClass.length() == 0
                        || (webServiceServerConfiguration.getImplementationClass() != null
                                && webServiceServerConfiguration.getImplementationClass().length() > 0 && webServiceServerConfiguration
                                .getImplementationClass().toLowerCase().contains(implementationClass.toLowerCase())))
                .filter(webServiceServerConfiguration -> active == null || webServiceServerConfiguration.isActive() == active)
                .filter(webServiceServerConfiguration -> domainKeyStore == null
                        || domainKeyStore == webServiceServerConfiguration.getDomainKeyStore()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/search/update/{oid}")
    public String processSearchToUpdateAction(@PathVariable("oid") WebServiceServerConfiguration webServiceServerConfiguration,
            Model model) {
        return "redirect:/webservices/management/webservicesservers/webserviceserverconfiguration/update/"
                + webServiceServerConfiguration.getExternalId();
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") WebServiceServerConfiguration webServiceServerConfiguration, Model model) {
        List<WebServiceAuthenticationLevel> values =
                new ArrayList<WebServiceAuthenticationLevel>(
                        Arrays.asList(com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel
                                .values()));
        values.remove(WebServiceAuthenticationLevel.CUSTOM);
        values.remove(WebServiceAuthenticationLevel.PASSWORD);

        if (!webServiceServerConfiguration.isCustomAuthenticationSupported()) {
            values.remove(WebServiceAuthenticationLevel.BASIC_AUTH_CUSTOM);
            values.remove(WebServiceAuthenticationLevel.WS_SECURITY_CUSTOM);
            addInfoMessage(
                    "Custom authentication not available because " + webServiceServerConfiguration.getImplementationClass()
                            + " does not implement public boolean static validate(String username, String password)", model);
        }

        model.addAttribute("authenticationLevelValues", values);
        model.addAttribute("WebServiceServerConfiguration_domainKeyStore_options",
                new ArrayList<com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore>(Bennu.getInstance()
                        .getDomainKeyStoresSet()));
        setWebServiceServerConfiguration(webServiceServerConfiguration, model);
        return "webservices/management/webservicesservers/webserviceserverconfiguration/update";
    }

    @RequestMapping(value = "/update/{oid}/entries/{selectedKeyStore}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody List<String> requestKeyStoreEntries(
            @PathVariable("oid") WebServiceServerConfiguration webServiceServerConfiguration,
            @PathVariable("selectedKeyStore") DomainKeyStore selectedDomainKeyStore, Model model) {
        return selectedDomainKeyStore.getAvailableKeyAlias();
    }

//  
    @RequestMapping(value = "/update/{oid}", method = RequestMethod.POST)
    public String update(
            @PathVariable("oid") WebServiceServerConfiguration webServiceServerConfiguration,
            @RequestParam(value = "active", required = false) boolean active,
            @RequestParam(value = "authenticationlevel", required = false) com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel authenticationLevel,
            @RequestParam(value = "domainkeystore", required = false) com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore,
            @RequestParam(value = "aliasforprivatekey", required = false) java.lang.String aliasForPrivateKey, @RequestParam(
                    value = "passwordforprivatekey", required = false) java.lang.String passwordForPrivateKey, @RequestParam(
                    value = "serviceusername", required = false) java.lang.String serviceUsername, @RequestParam(
                    value = "servicepassword", required = false) java.lang.String servicePassword, Model model) {

        setWebServiceServerConfiguration(webServiceServerConfiguration, model);

        if (!webServiceServerConfiguration.isCustomAuthenticationSupported()
                && (authenticationLevel == WebServiceAuthenticationLevel.CUSTOM
                        || authenticationLevel == WebServiceAuthenticationLevel.BASIC_AUTH_CUSTOM || authenticationLevel == WebServiceAuthenticationLevel.WS_SECURITY_CUSTOM)) {
            addErrorMessage(
                    "Webservice does not support custom authentication, must implement public static boolean validate(String username, String password) method",
                    model);
            return update(webServiceServerConfiguration, model);
        } else if (authenticationLevel == WebServiceAuthenticationLevel.WS_SECURITY || authenticationLevel == WebServiceAuthenticationLevel.WS_SECURITY_CUSTOM) {
            boolean valid = false;
            try {
                Key key = domainKeyStore.getHelper().getKey(aliasForPrivateKey, passwordForPrivateKey);
                valid = key != null;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (!valid) {
                addErrorMessage("Problems accessing key, maybe the password is wrong?", model);
                return update(webServiceServerConfiguration, model);
            }
        }

        updateWebServiceServerConfiguration(active, authenticationLevel, domainKeyStore, aliasForPrivateKey,
                passwordForPrivateKey, serviceUsername, servicePassword, model);

        return "redirect:/webservices/management/webservicesservers/webserviceserverconfiguration/";

    }

    @Atomic
    public void updateWebServiceServerConfiguration(boolean active,
            com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel authenticationLevel,
            com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore,
            java.lang.String aliasForPrivateKey, java.lang.String passwordForPrivateKey, java.lang.String serviceUsername,
            java.lang.String servicePassword, Model m) {

        WebServiceServerConfiguration webServiceServerConfiguration = getWebServiceServerConfiguration(m);
        webServiceServerConfiguration.setActive(active);
        webServiceServerConfiguration.setAuthenticationLevel(authenticationLevel);
        webServiceServerConfiguration.setDomainKeyStore(domainKeyStore);
        webServiceServerConfiguration.setAliasForPrivateKey(aliasForPrivateKey);
        webServiceServerConfiguration.setPasswordForPrivateKey(passwordForPrivateKey);
        webServiceServerConfiguration.setServiceUsername(serviceUsername);
        webServiceServerConfiguration.setServicePassword(servicePassword);
    }

}
