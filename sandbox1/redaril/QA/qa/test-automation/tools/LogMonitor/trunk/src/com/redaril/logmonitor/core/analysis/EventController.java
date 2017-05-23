package com.redaril.logmonitor.core.analysis;

import com.redaril.logmonitor.util.config.Config;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 4/16/13
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventController implements Runnable {
    private static final Logger logger = Logger.getLogger(EventController.class);
    private static EventController instance = new EventController();
    private Map<String, Integer> events;
    private Object lock;
    private boolean isRun;

    private EventController() {
        isRun = true;
        (new Thread(new EventController())).start();
    }

    public static EventController getInstance() { return instance; }

    @Override
    public void run() {
        while (this.isRun) {
            try {
                Thread.sleep(120000);
            } catch (InterruptedException ie) {
                logger.fatal("can't sleep for a while");
            }
        }
    }

    public void done() {
        this.isRun = false;
    }


    private synchronized void updateEvent(String name) {

    }

    public synchronized boolean isEventAllowed(String name) {
        updateEvent(name);
        return Integer.parseInt(Config.getInstance().getMaxEventPace()) > events.get(name).intValue();
    }
}
