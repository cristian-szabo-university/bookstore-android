package com.videogamelab.webservices;

public class BookstoreFault {

    public static final String ERROR_CLIENT = "Client";
    public static final String ERROR_SERVER = "Server";

    private String code;
    private String error;

    public BookstoreFault() {}

    public BookstoreFault(String code, String error) {
        this.code = code;

        if (error == null || error.isEmpty()) {
            this.error = "unknown error";
        } else {
            this.error = error;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) { this.code = code; }

    public String getError() {
        return error;
    }

    public void setError(String error) { this.error = error; }

}
