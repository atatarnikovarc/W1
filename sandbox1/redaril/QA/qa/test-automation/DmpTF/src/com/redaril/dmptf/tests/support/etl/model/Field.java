package com.redaril.dmptf.tests.support.etl.model;

import org.jetbrains.annotations.Nullable;

public class Field {
    private String type;
    private String value;
    private Integer number;
    private String name;

    //getter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

}