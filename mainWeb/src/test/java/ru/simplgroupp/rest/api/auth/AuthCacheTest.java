package ru.simplgroupp.rest.api.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class AuthCacheTest {
    @Mock private AuthConfig config;

    private AuthCache cache;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        when(this.config.getPassPhraseAge()).thenReturn(24 * 60 * 60 *1000); // 24 hours by default

        this.cache = new AuthCache(this.config);
    }

    @Test
    public void shouldReturnPassPhraseAtFirstRequest() {
        assertNotNull(this.cache.getPassPhrase());
        assertNotNull(this.cache.getPassPhraseDate());
    }

    @Test
    public void shouldSetCreateDateOnPhraseGeneratePhase() {
        assertNull(this.cache.getPassPhraseDate());

        this.cache.getPassPhrase();

        assertNotNull(this.cache.getPassPhraseDate());
    }

    @Test
    public void shouldReturnSamePhraseForAllRequestsWhilePhraseNotExpired() {
        final String basePhrase = this.cache.getPassPhrase();
        final Date baseDate = this.cache.getPassPhraseDate();

        assertThat(basePhrase, is(this.cache.getPassPhrase()));
        assertThat(baseDate, is(this.cache.getPassPhraseDate()));
    }

    @Test
    public void shouldRebuildPhraseOnExpiration() {
        final String basePhrase = this.cache.getPassPhrase();
        final Date baseDate = this.cache.getPassPhraseDate();

        System.out.println(baseDate);

        when(this.config.getPassPhraseAge()).thenReturn(-1);

        assertThat(basePhrase, not(equalTo(this.cache.getPassPhrase())));
        assertThat(baseDate, not(equalTo(this.cache.getPassPhraseDate())));
    }
}
