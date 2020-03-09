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
package com.qubit.solution.fenixedu.bennu.webservices.tools.keystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;

import org.joda.time.DateTime;

public class KeyStoreWorker {

    public static String SUPPORTED_KEY_PAIR_TYPE = "PKCS12";
    public static String SUPPORTED_KEY_STORE_TYPE = "JKS";
    public static String SUPPORTED_CERTIFICATE_TYPE = "X.509";
    public static String SUPPORTED_MANAGER_TYPE = "SunX509";

    private final KeyStore keystore;
    private final IKeyStoreBinder domainKeyStore;

    public KeyStoreWorker(IKeyStoreBinder keyStore) {
        this.domainKeyStore = keyStore;
        KeyStore openedKeyStore = null;
        try {
            openedKeyStore = openKeyStore(domainKeyStore.getBinaryContent(), domainKeyStore.getPassword());;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        this.keystore = openedKeyStore;
    }

    public List<KeyStoreEntryRepresentation> getEntries() {
        List<KeyStoreEntryRepresentation> entries = new ArrayList<KeyStoreEntryRepresentation>();

        if (keystore != null) {
            try {
                Enumeration<String> aliases = this.keystore.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate certificate = this.keystore.getCertificate(alias);

                    String type = certificate.getType();
                    if (type.equals(SUPPORTED_CERTIFICATE_TYPE)) {
                        X509Certificate cert = (X509Certificate) certificate;
                        boolean isKey = this.keystore.isKeyEntry(alias);
                        boolean isCert = this.keystore.isCertificateEntry(alias);
                        entries.add(new KeyStoreEntryRepresentation(alias, new DateTime(cert.getNotBefore()), new DateTime(cert
                                .getNotAfter()), isKey, isCert));
                    }
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }

    public static boolean isAbleToOpenKeyStore(DomainKeyStore domainKeyStore) {
        try {
            openKeyStore(domainKeyStore.getBinaryContent(), domainKeyStore.getPassword());
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }

    }

    private static java.security.KeyStore openKeyStore(byte[] content, String password) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        java.security.KeyStore javaKeyStore = java.security.KeyStore.getInstance(SUPPORTED_KEY_STORE_TYPE);

        javaKeyStore.load(new ByteArrayInputStream(content), password == null ? null : password.toCharArray());
        return javaKeyStore;
    }

    private void writeBack() {
        writeBack(domainKeyStore.getPassword() != null ? this.domainKeyStore.getPassword() : null);
    }

    private void writeBack(String password) {
        checkKeystore();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            keystore.store(byteArrayOutputStream, password != null ? password.toCharArray() : null);
            domainKeyStore.setBinaryContent(byteArrayOutputStream.toByteArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkKeystore() {
        if (keystore == null) {
            throw new IllegalStateException(
                    "Seems keystore is not open! You cannot save info to the keystore if it's not open. Maybe the password configuration is wrong?");
        }
    }

    public void removeEntry(String alias) {
        checkKeystore();
        try {
            this.keystore.deleteEntry(alias);
            writeBack();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addKeyPair(String alias, byte[] keyContent, String keyPassword) {
        checkKeystore();

        Key key = getKey(alias, keyPassword);
        if (key != null) {
            throw new IllegalStateException("Key with alias: " + alias + " already present in keystore: "
                    + this.domainKeyStore.getName());
        }
        if (keyPassword == null || keyPassword.length() == 0) {
            throw new IllegalArgumentException("Must define a password for key");
        }
        try {
            KeyStore keystore = KeyStore.getInstance(SUPPORTED_KEY_PAIR_TYPE);
            keystore.load(new ByteArrayInputStream(keyContent), keyPassword.toCharArray());
            String internalAlias = keystore.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) keystore.getKey(internalAlias, keyPassword.toCharArray());
            Certificate[] certificateChain = keystore.getCertificateChain(internalAlias);
            this.keystore.setKeyEntry(alias, privateKey, keyPassword.toCharArray(), certificateChain);
            writeBack();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    private Certificate loadCertificate(byte[] certificateContent) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance(SUPPORTED_CERTIFICATE_TYPE);
            Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(certificateContent));
            return certificate;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void addCertificate(String alias, byte[] certificateFile) {
        checkKeystore();
        Certificate certificate = getCertificate(alias);
        if (certificate != null) {
            throw new IllegalStateException("Certificate with alias: " + alias + " already present in keystore: "
                    + this.domainKeyStore.getName());
        }

        certificate = loadCertificate(certificateFile);
        try {
            this.keystore.setCertificateEntry(alias, certificate);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        writeBack();
    }

    public KeyManagerFactory getKeyManagerFactoryNeededForSSL(String remoteCertificate) {
        checkKeystore();
        try {
            KeyStore keyStore = KeyStore.getInstance(SUPPORTED_KEY_STORE_TYPE);
            keyStore.load(null, null);
            Certificate certificate = this.keystore.getCertificate(remoteCertificate);
            keyStore.setCertificateEntry("certificate", certificate);
            int i = 0;
            Certificate[] certificateChain = this.keystore.getCertificateChain(remoteCertificate);
            if (certificateChain != null) {
                for (Certificate certificateChainElement : certificateChain) {
                    keyStore.setCertificateEntry("certificateChain-" + i, certificateChainElement);
                }
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(SUPPORTED_MANAGER_TYPE);
            kmf.init(keyStore, null);

            return kmf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create key manager factory", e);
        }
    }

    public TrustManagerFactory getTrustManagerFactoryNeededForSSL() {
        checkKeystore();
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(SUPPORTED_MANAGER_TYPE);
            tmf.init(this.keystore);
            return tmf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Trust Manager Factory", e);
        }
    }

    public Key getKey(String alias, String password) {
        checkKeystore();
        try {
            return this.keystore.getKey(alias, password != null ? password.toCharArray() : null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve key with alias: " + alias, e);
        }
    }

    public Certificate getCertificate(String alias) {
        checkKeystore();
        try {
            return this.keystore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Unable to retrieve certificate with alias: " + alias, e);
        }
    }

    public boolean isCertificate(String alias) {
        checkKeystore();
        try {
            return this.keystore.isCertificateEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isKey(String alias) {
        checkKeystore();
        try {
            return this.keystore.isKeyEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void changePassword(String newPassword) {
        writeBack(newPassword);
    }

    public static byte[] emptyKeyStore(String password) {
        try {
            KeyStore keystore = KeyStore.getInstance(SUPPORTED_KEY_STORE_TYPE);
            keystore.load(null, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            keystore.store(byteArrayOutputStream, password.toCharArray());
            return byteArrayOutputStream.toByteArray();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
