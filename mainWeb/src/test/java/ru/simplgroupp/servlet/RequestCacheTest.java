package ru.simplgroupp.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RequestCacheTest {
    @Mock private HttpServletRequest request;

    private RequestCache requestCache;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        when(this.request.getRemoteUser()).thenReturn(null);

        this.requestCache = new RequestCache();
        this.requestCache.setRequest(this.request);
    }

    @Test
    public void shouldRequestLoginWithoutPasswordHashing() {
        final String username = "username";
        final String password = "userpass";

        try {
            this.requestCache.login(username, password, false);
        } catch (ServletException e) {
            fail();
        }

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        try {
            verify(this.request).login(anyString(), captor.capture());
        } catch (ServletException e) {
            fail();
        }

        assertEquals(password, captor.getValue());
    }

    @Test
    public void shouldRequestLoginWithPasswordHashing() {
        final String username = "username";
        final String password = "userpass";

        try {
            this.requestCache.login(username, password, true);
        } catch (ServletException e) {
            fail();
        }

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        try {
            verify(this.request).login(anyString(), captor.capture());
        } catch (ServletException e) {
            fail();
        }

        assertEquals(DigestUtils.md5Hex(password), captor.getValue());
    }

    @Test
    public void shouldIdentifyWhenUserLoginIn() {
        when(this.request.getRemoteUser()).thenReturn("username");
        assertTrue(this.requestCache.isAuthenticated());
    }

    @Test
    public void shouldIdentifyWhenUserNotLoginIn() {
        assertFalse(this.requestCache.isAuthenticated());
    }
}
