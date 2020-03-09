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
package com.qubit.solution.fenixedu.bennu.webservices.ui.management.keystores;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;
import com.qubit.solution.fenixedu.bennu.webservices.services.server.BennuWebService;
import com.qubit.solution.fenixedu.bennu.webservices.ui.BennuWebservicesController;
import com.qubit.solution.fenixedu.bennu.webservices.ui.WebservicesBaseController;

@SpringFunctionality(app = BennuWebservicesController.class, title = "label.title.management.keystores",
        accessGroup = "#managers")
@RequestMapping("/webservices/management/keystores/domainkeystore")
public class DomainKeyStoreController extends WebservicesBaseController {

    @RequestMapping
    public String home(Model model) {
        return "redirect:/webservices/management/keystores/domainkeystore/";
    }

    private DomainKeyStore getDomainKeyStore(Model m) {
        return (DomainKeyStore) m.asMap().get("domainKeyStore");
    }

    private void setDomainKeyStore(DomainKeyStore domainKeyStore, Model m) {
        m.addAttribute("domainKeyStore", domainKeyStore);
    }

    @Atomic
    public void deleteDomainKeyStore(DomainKeyStore domainKeyStore) {
        domainKeyStore.delete();
    }

    @Atomic
    public void deleteDomainKeyStores(List<DomainKeyStore> domainKeyStore) {
        for (DomainKeyStore keyStore : domainKeyStore) {
            keyStore.delete();
        }
    }

    @RequestMapping(value = "/")
    public String search(@RequestParam(value = "name", required = false) java.lang.String name, Model model) {
        List<DomainKeyStore> searchdomainkeystoreResultsDataSet = filterSearchDomainKeyStore(name);
        model.addAttribute("searchdomainkeystoreResultsDataSet", searchdomainkeystoreResultsDataSet);
        return "webservices/management/keystores/domainkeystore/search";
    }

    private List<DomainKeyStore> getSearchUniverseSearchDomainKeyStoreDataSet() {
        return new ArrayList<DomainKeyStore>(Bennu.getInstance().getDomainKeyStoresSet());
    }

    private List<DomainKeyStore> filterSearchDomainKeyStore(java.lang.String name) {

        return getSearchUniverseSearchDomainKeyStoreDataSet()
                .stream()
                .filter(domainKeyStore -> name == null
                        || name.length() == 0
                        || (domainKeyStore.getName() != null && domainKeyStore.getName().length() > 0 && domainKeyStore.getName()
                                .toLowerCase().contains(name.toLowerCase()))).collect(Collectors.toList());
    }

    @RequestMapping(value = "/search/deleteSelected", method = RequestMethod.POST)
    public String processSearchToDeleteSelected(@RequestParam("domainKeyStores") List<DomainKeyStore> domainKeyStores, Model model) {
        deleteDomainKeyStores(domainKeyStores);
        return "redirect:/webservices/management/keystores/domainkeystore/";
    }

    @RequestMapping(value = "/search/view/{oid}")
    public String processSearchToViewAction(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {

        return "redirect:/webservices/management/keystores/domainkeystore/read" + "/" + domainKeyStore.getExternalId();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        return "webservices/management/keystores/domainkeystore/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestParam(value = "name", required = false) java.lang.String name, @RequestParam(value = "password",
            required = false) java.lang.String password,
            @RequestParam(value = "passwordVerification", required = false) java.lang.String passwordVerification, Model model) {

        if (password != null && !password.equals(passwordVerification)) {
            addErrorMessage("Password and password verification did not match", model);
            return "webservices/management/keystores/domainkeystore/create";
        }

        DomainKeyStore domainKeyStore = createDomainKeyStore(name, password);
        model.addAttribute("domainKeyStore", domainKeyStore);

        return "redirect:/webservices/management/keystores/domainkeystore/read/" + getDomainKeyStore(model).getExternalId();
    }

    @Atomic
    public DomainKeyStore createDomainKeyStore(java.lang.String name, java.lang.String password) {
        DomainKeyStore domainKeyStore = new DomainKeyStore(name, password);
        return domainKeyStore;
    }

    @RequestMapping(value = "/read/{oid}")
    public String read(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {

        if (!domainKeyStore.isAbleToOpenKeyStore()) {
            addWarningMessage("Unable to open keystore. The most common problem is that the password is incorrect", model);
        }
        setDomainKeyStore(domainKeyStore, model);
        return "webservices/management/keystores/domainkeystore/read";
    }

    @RequestMapping(value = "/deleteEntry/{oid}")
    public String deleteEntry(@PathVariable("oid") DomainKeyStore domainKeyStore,
            @RequestParam(value = "alias", required = false) String alias, Model model) {

        deleteEntry(domainKeyStore, alias);
        return "redirect:/webservices/management/keystores/domainkeystore/read/" + domainKeyStore.getExternalId();
    }

    @Atomic
    private void deleteEntry(DomainKeyStore domainKeyStore, String alias) {
        domainKeyStore.deleteEntry(alias);
    }

    @RequestMapping(value = "/read/{oid}/downloadkeystore")
    public void processReadToDownloadKeystore(@PathVariable("oid") DomainKeyStore domainKeyStore, HttpServletResponse response) {
        InputStream is = domainKeyStore.getKeyStoreFile().getStream();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + domainKeyStore.getName() + ".jks");
        try {
            StreamUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/read/{oid}/uploadkeystore")
    public String processReadToUploadKeystore(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {
        setDomainKeyStore(domainKeyStore, model);
        return "redirect:/webservices/management/keystores/uploadkeystore/" + domainKeyStore.getExternalId();
    }

    @RequestMapping(value = "/read/{oid}/addcertificate")
    public String processReadToAddCertificate(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {
        setDomainKeyStore(domainKeyStore, model);
        return "redirect:/webservices/management/keystores/uploadcertificate/" + getDomainKeyStore(model).getExternalId();
    }

    @RequestMapping(value = "/read/{oid}/addkey")
    public String processReadToAddKey(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {
        setDomainKeyStore(domainKeyStore, model);
        return "redirect:/webservices/management/keystores/uploadkey/" + getDomainKeyStore(model).getExternalId();
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {
        setDomainKeyStore(domainKeyStore, model);
        if (!domainKeyStore.isAbleToOpenKeyStore()) {
            addWarningMessage(
                    "Unable to open keystore. The most common problem is that the password is incorrect, hence when doing password update we'll only change in the system and not in the keystore itself. You can use this to fix the password problem.",
                    model);
            model.addAttribute("noOldPasswordRequest", true);
        }

        return "webservices/management/keystores/domainkeystore/update";
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") DomainKeyStore domainKeyStore,
            @RequestParam(value = "name", required = false) java.lang.String name, @RequestParam(value = "oldPassword",
                    required = false) java.lang.String oldPassword,
            @RequestParam(value = "password", required = false) java.lang.String password, @RequestParam(
                    value = "passwordVerification", required = false) java.lang.String passwordVerification, Model model) {

        setDomainKeyStore(domainKeyStore, model);

        if (domainKeyStore.isAbleToOpenKeyStore() && oldPassword != null && !domainKeyStore.getPassword().equals(oldPassword)) {
            addErrorMessage("Password incorrect", model);
            return "webservices/management/keystores/domainkeystore/update";
        }
        if (password != null && !password.equals(passwordVerification)) {
            addErrorMessage("Password and password verification did not match", model);
            if (!domainKeyStore.isAbleToOpenKeyStore()) {
                addWarningMessage(
                        "Unable to open keystore. The most common problem is that the password is incorrect, hence when doing password update we'll only change in the system and not in the keystore itself. You can use this to fix the password problem.",
                        model);
                model.addAttribute("noOldPasswordRequest", true);
            }
            return "webservices/management/keystores/domainkeystore/update";
        }

        updateDomainKeyStore(name, oldPassword, password, model);
        return "redirect:/webservices/management/keystores/domainkeystore/read/" + getDomainKeyStore(model).getExternalId();
    }

    @Atomic
    public void updateDomainKeyStore(java.lang.String name, java.lang.String oldPassword, java.lang.String password, Model m) {
        DomainKeyStore domainKeyStore = getDomainKeyStore(m);
        domainKeyStore.setName(name);
        if (password != null && oldPassword != null && !oldPassword.equals(password)) {
            domainKeyStore.changePassword(oldPassword, password);
        }
    }

}
