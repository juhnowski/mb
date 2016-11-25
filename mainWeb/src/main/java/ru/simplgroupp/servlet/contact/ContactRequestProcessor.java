package ru.simplgroupp.servlet.contact;

import ru.simplgroupp.servlet.ContactCallbackServlet;

/**
 * Created by aro on 13.10.2014.
 */
public interface ContactRequestProcessor {
    ContactCallbackResponse process(ContactCallbackRequest callbackRequest, ContactCallbackServlet servlet);

}
