package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB;

import com.redaril.nio.client.Protocol;

public abstract class RTBProtocol<T> implements Protocol {


    @Override
    public int getBufferSize() {
        return 10000;
    }

    @Override
    public boolean needReconnect(Exception e) {
        return true;
    }

    public abstract T createBidRequest();

}
