package ru.simplgroupp.webapp.managernew.rest.api;

import java.util.List;

public class JsonPartialResult<E> {
    private List<E> items;
    private boolean more;

    public JsonPartialResult(List<E> items, boolean more) {
        this.items = items;
        this.more = more;
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
