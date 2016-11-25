package ru.simplgroupp.rest.api.auth;

import java.io.Serializable;

public class LoginResult implements Serializable {
    private boolean success;

    public LoginResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
