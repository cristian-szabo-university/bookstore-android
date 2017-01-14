package com.videogamelab.webservices;

import org.ksoap2.SoapFault;

public class BookstoreException extends Exception {

    private BookstoreFault fault;

    public BookstoreException(String code, String error) {
        fault = new BookstoreFault(code, error);
    }

    public BookstoreException(SoapFault soap) {
        if (soap == null) {
            return;
        }

        if (soap.faultcode.compareTo(BookstoreFault.ERROR_CLIENT) == 0 ||
                soap.faultcode.compareTo(BookstoreFault.ERROR_SERVER) == 0) {
            fault = new BookstoreFault(soap.faultcode, soap.faultstring);
        } else {
            fault = new BookstoreFault(BookstoreFault.ERROR_SERVER, soap.faultstring);
        }
    }

    public BookstoreFault getFault() {
        return fault;
    }

}
