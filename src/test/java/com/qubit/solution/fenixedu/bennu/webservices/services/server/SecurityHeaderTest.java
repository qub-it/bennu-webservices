package com.qubit.solution.fenixedu.bennu.webservices.services.server;

import org.junit.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.Assert.assertNotNull;

public class SecurityHeaderTest {

    @Test
    public void testGetPrivateKeyCipherInstanceExists()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        assertNotNull(SecurityHeader.getPrivateKeyCipherInstance());
    }

    @Test
    public void testGetSessionCipherInstanceExists()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        assertNotNull(SecurityHeader.getSessionCipherInstance());
    }

}