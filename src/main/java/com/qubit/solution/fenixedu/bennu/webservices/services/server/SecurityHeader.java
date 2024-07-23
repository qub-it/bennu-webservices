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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceServerConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.tools.keystore.KeyStoreWorker;

public class SecurityHeader {

    private static final String SUN_JCE_PROVIDER = "SunJCE";

    final private WebServiceServerConfiguration configuration;
    final private String username;
    final private String password;
    final private String nonce;
    final private String timestamp;
    final private byte[] sessionKey;

    public SecurityHeader(WebServiceServerConfiguration configuration, String username, String password, String nonce,
            String timestamp) {
        super();
        this.configuration = configuration;
        this.username = username;
        this.password = password;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.sessionKey = getSessionKey();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        try {
            return decryptWithSessionKey(this.password);
        } catch (Throwable t) {
            return null;
        }
    }

    public String getTimestamp() {
        try {
            return decryptWithSessionKey(this.timestamp);
        } catch (Throwable t) {
            return null;
        }
    }

    private String decryptWithSessionKey(String content)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        SecretKey originalKey = new SecretKeySpec(this.sessionKey, 0, this.sessionKey.length, "AES");
        Cipher cipher = getSessionCipherInstance();
        cipher.init(Cipher.DECRYPT_MODE, originalKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(content)), "UTF-8");
    }

    protected static Cipher getSessionCipherInstance()
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        return Cipher.getInstance("AES", SUN_JCE_PROVIDER);
    }

    private byte[] decryptWithPrivateKey(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {

        Cipher cipher = getPrivateKeyCipherInstance();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(Base64.getDecoder().decode(cipherText));
    }

    protected static Cipher getPrivateKeyCipherInstance()
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        return Cipher.getInstance("RSA", SUN_JCE_PROVIDER);
    }

    private byte[] getSessionKey() {
        DomainKeyStore domainKeyStore = this.configuration.getDomainKeyStore();
        KeyStoreWorker helper = domainKeyStore.getHelper();
        PrivateKey privateKey =
                (PrivateKey) helper.getKey(this.configuration.getAliasForPrivateKey(),
                        this.configuration.getPasswordForPrivateKey());

        try {
            return decryptWithPrivateKey(this.nonce, privateKey);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private boolean isTimestampValid() {
        DateTimeFormatter forPattern = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            DateTime parseDateTime = forPattern.parseDateTime(getTimestamp());
            Interval interval = new Interval(new DateTime().minusMinutes(5), new DateTime().plusMinutes(5));
            return interval.contains(parseDateTime);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private boolean isUserCredentialsValid() {
        return this.configuration.validate(getUsername(), getPassword());
    }

    public boolean isValid() {
        System.out.println("Timestamp valid: " + isTimestampValid());
        System.out.println("User credentials valid: " + isUserCredentialsValid());
        return isTimestampValid() && isUserCredentialsValid();
    }

}
