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

package com.qubit.solution.fenixedu.bennu.webservices.domain.keystore;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;

import com.qubit.solution.fenixedu.bennu.webservices.tools.keystore.IKeyStoreBinder;
import com.qubit.solution.fenixedu.bennu.webservices.tools.keystore.KeyStoreWorker;

public class DomainKeyStore extends DomainKeyStore_Base implements IKeyStoreBinder {

    protected DomainKeyStore() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public DomainKeyStore(String name, String password) {
        this();
        setName(name);
        if (password == null) {
            throw new IllegalStateException("Password cannot be null!");
        }
        super.setPassword(password);
        setBinaryContent(KeyStoreWorker.emptyKeyStore(password));
    }

    @Override
    public void setKeyStoreFile(KeyStoreFile keyStoreFile) {
        throw new UnsupportedOperationException("Please use setContent");
    }

    @Override
    public void setPassword(String password) {
        throw new UnsupportedOperationException("Please use changePassword instead!");
    }

    @Override
    public byte[] getBinaryContent() {
        return getKeyStoreFile().getContent();
    }

    @Override
    public void setBinaryContent(byte[] content) {
        KeyStoreFile keyStoreFile = getKeyStoreFile();
        if (keyStoreFile != null) {
            super.setKeyStoreFile(null);
            keyStoreFile.delete();
        }
        KeyStoreFile newKeyStoreFile = new KeyStoreFile(this, content);
        super.setKeyStoreFile(newKeyStoreFile);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (isAbleToOpenKeyStore()) {
            if (!getPassword().equals(oldPassword)) {
                throw new IllegalAccessError("Invalid password, unable to change");
            }
            KeyStoreWorker keyStoreWorker = new KeyStoreWorker(this);
            keyStoreWorker.changePassword(newPassword);
            super.setPassword(newPassword);
        } else {
            super.setPassword(newPassword);
        }
    }

    public KeyStoreWorker getHelper() {
        return new KeyStoreWorker(this);
    }

    public void delete() {
        KeyStoreFile keyStoreFile = getKeyStoreFile();
        super.setKeyStoreFile(null);
        if (keyStoreFile != null) {
            keyStoreFile.delete();
        }
        setRootDomainObject(null);
        super.deleteDomainObject();
    }

    public boolean isAbleToOpenKeyStore() {
        return KeyStoreWorker.isAbleToOpenKeyStore(this);
    }

    public void deleteEntry(String alias) {
        getHelper().removeEntry(alias);
    }

    public List<String> getAvailableKeyAlias() {
        return getHelper().getEntries().stream().filter(entry -> entry.isKey()).map(entry -> entry.getAlias())
                .collect(Collectors.toList());
    }

    public List<String> getAvailableCertificateAlias() {
        return getHelper().getEntries().stream().filter(entry -> entry.isCertificate()).map(entry -> entry.getAlias())
                .collect(Collectors.toList());
    }

    public static DomainKeyStore readByName(final String name) {
        return Bennu.getInstance().getDomainKeyStoresSet().stream().filter(keyStore -> keyStore.getName().equals(name))
                .findFirst().orElse(null);
    }
}
