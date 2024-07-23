package com.qubit.solution.fenixedu.bennu.webservices.services.server;

import org.junit.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityHeaderTest {

    @Test
    public void testGetPrivateKeyCipherInstanceExists()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        assertDoesNotThrow(SecurityHeader::getPrivateKeyCipherInstance);
        assertNotNull(SecurityHeader.getPrivateKeyCipherInstance());
    }

    @Test
    public void testGetSessionCipherInstanceExists()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        assertDoesNotThrow(SecurityHeader::getSessionCipherInstance);
        assertNotNull(SecurityHeader.getSessionCipherInstance());
    }

}