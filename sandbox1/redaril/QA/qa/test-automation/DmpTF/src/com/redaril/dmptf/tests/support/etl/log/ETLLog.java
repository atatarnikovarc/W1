package com.redaril.dmptf.tests.support.etl.log;

import com.redaril.dmptf.tests.support.etl.model.Record;

public abstract class ETLLog {
    protected Integer size;
    protected Record record;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
