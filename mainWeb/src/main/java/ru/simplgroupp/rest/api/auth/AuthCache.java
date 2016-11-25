package ru.simplgroupp.rest.api.auth;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Storage for authentication artifacts
 */
@SessionScoped
public class AuthCache implements Serializable {
    private AuthConfig config;

    private UUID passPhrase;
    private Date passPhraseDate;
    private String checkCode;

    private void newPhrase() {
        this.passPhrase = UUID.randomUUID();
        this.passPhraseDate = new Date();
    }

    /**
     * Return actual pass phrase as string. Request rebuild pass phrase if it requested by user at first time or expired.
     * @return String representation of pass phrase.
     */
    public String getPassPhrase() {
        if (!isActual(this.passPhraseDate)) {
            newPhrase();
        }
        return passPhrase.toString();
    }

    // return clone (prevent mutate of original date) for passphrase create date
    public Date getPassPhraseDate() {
        return this.passPhraseDate != null ? new Date(this.passPhraseDate.getTime()) : this.passPhraseDate;
    }

    /**
     * Check is current pass phrase valid for provided date.
     * @param date date for which pass phrase mast be valid.
     * @return true if valid and false if not valid
     */
    public boolean isActual(Date date) {
        return !(date == null || new Date().getTime()-date.getTime() > this.config.getPassPhraseAge());
    }

    public AuthCache() {}

    @Inject
    public AuthCache(AuthConfig config) {
        this.config = config;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
