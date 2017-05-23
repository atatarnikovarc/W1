package com.redaril.dmptf.tests.support.etl.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Record extends ArrayList<Field> {

    @XmlElement(name = "field")
    public List<Field> getFields() {
        return this;
    }

    public Integer getSize() {
        return this.size();
    }

    public Field getFieldByName(String name) {
        Integer size = this.size();
        Integer i = 0;
        Field field;
        while (i < size) {
            field = this.get(i);
            if (field.getName().equalsIgnoreCase(name))
                return field;
            i++;
        }
        return null;
    }
}