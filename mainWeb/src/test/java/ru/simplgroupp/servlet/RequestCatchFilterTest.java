package ru.simplgroupp.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class RequestCatchFilterTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private RequestCache requestCache;

    private RequestCatchFilter filter;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        this.filter = new RequestCatchFilter(this.requestCache);
    }

    @Test
    public void shouldCacheRequest() {
        try {
            this.filter.doFilter(this.request, this.response, this.filterChain);
        } catch (IOException | ServletException e) {
            fail();
        }

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        verify(this.requestCache).setRequest(captor.capture());

        assertSame(this.request, captor.getValue());
    }

    @Test
    public void shouldSendProcessingToNextHandler() {
        try {
            this.filter.doFilter(this.request, this.response, this.filterChain);
        } catch (IOException | ServletException e) {
            fail();
        }

        try {
            verify(this.filterChain, times(1)).doFilter(request, response);
        } catch (IOException | ServletException e) {
            fail();
        }
    }
}
