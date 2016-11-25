package ru.simplgroupp.webapp.managernew.rest.api.auth;

import java.io.Serializable;

/**
 * Config for authentication process
 */
public class AuthConfig implements Serializable {
    private final int passPhraseAge = 10000;

    /**
     * Max available age of pass phrase. If pass phrase older than this age it is expired and must be rebuild on first
     * request.
     * @return Max age of pass phrase in mileseconds
     */
    public int getPassPhraseAge() {
        return passPhraseAge;
    }
}
