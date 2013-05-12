package com.playtech.proov.http;


import java.io.Serializable;

public class KeyValue implements Serializable {

    private static final long serialVersionUID = 1;

    protected String key;
    protected String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
