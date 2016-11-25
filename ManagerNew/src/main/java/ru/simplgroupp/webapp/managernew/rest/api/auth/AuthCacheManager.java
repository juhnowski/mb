package ru.simplgroupp.webapp.managernew.rest.api.auth;

import javax.ejb.LocalBean;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Storage for authentication artifacts
 */
@SessionScoped
public class AuthCacheManager implements Serializable {
    private static final long serialVersionUID = 8920886843875112523L;
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

    public AuthCacheManager() {}

    @Inject
    public AuthCacheManager(AuthConfig config) {
        this.config = config;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
