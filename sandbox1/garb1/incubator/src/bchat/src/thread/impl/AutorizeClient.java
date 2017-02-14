package bchat.src.thread.impl;

import bchat.src.model.ClientObject;
import bchat.src.model.EventType;
import bchat.src.thread.AbstractThread;

import java.util.Vector;

public class AutorizeClient extends AbstractThread {
    private Vector clientsList;
    private ClientObject clientObject;
    private int listSize;

    public AutorizeClient(Vector clientsList, ClientObject clientObject) {
        this.clientsList = clientsList;
        this.clientObject = clientObject;
        listSize = clientsList.size();
        Thread autorizeClientThread = new Thread(this);
        autorizeClientThread.start();
    }

    @Override
    public void run() {
        boolean isauthorized = false;
        synchronized (clientsList) {
            for (int i = 0; i < listSize; i++) {
                ClientObject tmpObject = (ClientObject) clientsList.get(i);
                //if((clientObject.getClientIP().getHostAddress()
                // != tmpObject.getClientIP().getHostAddress()) &&
                //   (clientObject.getNickName() != tmpObject.getNickName())) {
                if (!clientObject.getNickName().equals(tmpObject.getNickName()))
                    isauthorized = true;
                else {
                    isauthorized = false;
                    break;
                }
            }
        }

        if (isauthorized) {
            EventType eventObject = new EventType(4, clientObject);
            setChanged();
            notifyObservers(eventObject);
            clearChanged();
        } else {
            clientObject.setNickName(null);
            EventType eventObject = new EventType(4, clientObject);
            setChanged();
            notifyObservers(eventObject);
            clearChanged();
        }
    }

    protected void finalize() {
        //System.out.println("Thread " + Thread.currentThread().getName() + "finished");
    }
}