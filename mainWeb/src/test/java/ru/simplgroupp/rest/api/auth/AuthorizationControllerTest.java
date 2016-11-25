package ru.simplgroupp.rest.api.auth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.servlet.RequestCache;
import ru.simplgroupp.transfer.Users;

import javax.servlet.ServletException;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class AuthorizationControllerTest {
    @Mock private AuthCache authCache;
    @Mock private RequestCache requestCache;
    @Mock private UsersBeanLocal usersBean;
    @Mock private Users user;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final String passPhrase = "PASSPHRASE";
    private final String loginToken = "LOGIN_TOKEN";
    private final String username = "USERNAME";
    private final String password = "PASSWORD";

    private AuthorizationController ctrl;

    @Before
    public  void beforeEach() {
        MockitoAnnotations.initMocks(this);

        when(this.usersBean.findUserByToken(eq(this.passPhrase), eq(this.loginToken), Matchers.any(Set.class))).thenReturn(this.user);
        when(this.user.getUserName()).thenReturn(this.username);
        when(this.user.getPassword()).thenReturn(this.password);

        when(this.authCache.getPassPhrase()).thenReturn(this.passPhrase);
        when(this.requestCache.isAuthenticated()).thenReturn(false);

        ctrl = new AuthorizationController(this.authCache, this.requestCache, this.usersBean);
    }

    @Test
    public void shouldReturnPassPhrase() {
        assertThat(passPhrase, is(this.ctrl.getTokenPasPhrase().getData()));
    }

    @Test
    public void shouldSkipAuthenticationAndReturnSuccessWhenAlreadyAuthenticated() {
        when(this.requestCache.isAuthenticated()).thenReturn(true);

        // result successfully
        assertTrue(this.ctrl.loginByToken(this.loginToken).getData().isSuccess());

        // authentication skipped
        try {
            verify(this.requestCache, never()).login(anyString(), anyString(), anyBoolean());
        } catch (ServletException e) {
            fail();
        }
    }

    @Test
    public void shouldSuccessAuthenticateTokenSuccessfullyValidated() {
        assertTrue(this.ctrl.loginByToken(this.loginToken).isSuccess());
        try {
            Mockito.verify(this.requestCache, Mockito.times(1)).login(this.username, this.password, false);
        } catch (ServletException e) {
            fail();
        }
    }

    @Test
    public void shouldNotAuthenticateWhenTokenValidationFailedAndThrowException() {
        when(this.usersBean.findUserByToken(eq(this.passPhrase), eq(this.loginToken), Matchers.any(Set.class))).thenReturn(this.user);

        try {
            doThrow(ServletException.class).when(this.requestCache).login(anyString(), anyString(), anyBoolean());
        } catch (ServletException e) {
            fail();
        }

        this.exception.expect(IllegalArgumentException.class);
        try {
            this.ctrl.loginByToken(this.loginToken);
        } finally {
            try {
                verify(this.requestCache, times(1)).login(anyString(), anyString(), anyBoolean());
            } catch (ServletException e) {
                fail();
            }
        }
    }

    @Test
    public void shouldRequestAuthenticatingWithoutPasswordHashing() {
        this.ctrl.loginByToken(this.loginToken);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        try {
            Mockito.verify(this.requestCache, Mockito.times(1)).login(anyString(), anyString(), captor.capture());
        } catch (ServletException e) {
            fail();
        }

        assertFalse(captor.getValue());
    }
}
