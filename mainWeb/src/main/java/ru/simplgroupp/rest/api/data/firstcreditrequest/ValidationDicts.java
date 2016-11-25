package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;
import java.util.Set;

public class ValidationDicts implements Serializable {
    public long fileSizeLimitB;
    public long fileSizeLimitMB;
    public Set<String> allowedMime;
    public Set<String> allowedExtensions;

    public long getFileSizeLimitB() {
        return fileSizeLimitB;
    }

    public void setFileSizeLimitB(long fileSizeLimitB) {
        this.fileSizeLimitB = fileSizeLimitB;
    }

    public long getFileSizeLimitMB() {
        return fileSizeLimitMB;
    }

    public void setFileSizeLimitMB(long fileSizeLimitMB) {
        this.fileSizeLimitMB = fileSizeLimitMB;
    }

    public Set<String> getAllowedMime() {
        return allowedMime;
    }

    public void setAllowedMime(Set<String> allowedMime) {
        this.allowedMime = allowedMime;
    }

    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(Set<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }
}
