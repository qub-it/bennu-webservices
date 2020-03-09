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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import pt.ist.fenixframework.Atomic;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;
import com.qubit.solution.fenixedu.bennu.webservices.ui.WebservicesBaseController;

@BennuSpringController(value = DomainKeyStoreController.class)
@RequestMapping("/webservices/management/keystores/uploadkeystore")
public class UploadKeyStoreController extends WebservicesBaseController {

	@RequestMapping(value = "/{oid}")
	public String uploadkeystore(
			@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {

		model.addAttribute("domainKeyStore", domainKeyStore);
		return "webservices/management/keystores/uploadkeystore";
	}

	@RequestMapping(value = "/upload/{oid}", method = RequestMethod.POST)
	public String uploadkeystoreToUpload(
			@PathVariable("oid") DomainKeyStore domainKeyStore,
			@RequestParam(value = "keyStoreFile", required = false) MultipartFile keyStoreFile,
			Model model) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			StreamUtils.copy(keyStoreFile.getInputStream(),
					byteArrayOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = byteArrayOutputStream.toByteArray();
		changeKeystore(domainKeyStore, content);

		return "redirect:/webservices/management/keystores/domainkeystore/read/"
				+ domainKeyStore.getExternalId();
	}

	@RequestMapping(value = "/cancel/{oid}")
	public String uploadkeystoreToCancel(
			@PathVariable("oid") DomainKeyStore domainKeyStore, Model model) {
		return "redirect:/webservices/management/keystores/domainkeystore/read/"
				+ domainKeyStore.getExternalId();
	}

	@Atomic
	private void changeKeystore(DomainKeyStore domainKeyStore, byte[] content) {
		domainKeyStore.setBinaryContent(content);
	}
}
