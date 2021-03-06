package com.studyio.hellosocket.domain;

/**
 * @author jiayq
 * @Date 2021-03-06
 */
public class Messager {

    private Integer port;

    private String message;

    public Messager() {

    }

    public Messager(Integer port, String message) {
        this.port = port;
        this.message = message;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        if (port != null) {
            return port + " : " + message;
        } else {
            return "";
        }
    }
}
