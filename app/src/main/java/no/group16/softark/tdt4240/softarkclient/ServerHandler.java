package no.group16.softark.tdt4240.softarkclient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by tien on 4/14/2016.
 */
public abstract class ServerHandler   {

    LinkedList<JSONObject> outgoingMessageQueue;
    LinkedList<JSONObject> incomingMessageQueue;
    SenderTask senderTask;
    ReceiverTask receiverTask;

    HashMap<String, ArrayList<IReceiver>> listenerMap;

    public ServerHandler() {
        listenerMap = new HashMap<>();
        outgoingMessageQueue = new LinkedList<>();
        incomingMessageQueue = new LinkedList<>();

        senderTask = new SenderTask();
        receiverTask = new ReceiverTask();

        senderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        receiverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void registerListener(String eventKey, IReceiver receiver) {
        ArrayList classes;
        if(listenerMap.containsKey(eventKey)) {
            classes = listenerMap.get(eventKey);
            if(!classes.contains(receiver))
                classes.add(receiver);
        } else {
            classes = new ArrayList();
            classes.add(receiver);
            listenerMap.put(eventKey, classes);
        }
    }

    public void removeListener(String eventKey, IReceiver receiver) {
        if(listenerMap.containsKey(eventKey)) {
            ArrayList classes = listenerMap.get(eventKey);
            if(classes.contains(receiver))
                classes.remove(receiver);
        }
    }


    public void queueMessage(JSONObject json) {
        outgoingMessageQueue.add(json);
    }

    protected void queueIncomingMessage(JSONObject json) {
        incomingMessageQueue.add(json);
    }

    public Queue<JSONObject> getIncomingMessageQueue() {
        return incomingMessageQueue;
    }


    protected class SenderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while(true) {

                while(outgoingMessageQueue.isEmpty() == false) {
                    // TODO: dequeue outgoingMessageQueue and send to server
                    // serverConnection
                    if(isConnected()) {
                        JSONObject obj = outgoingMessageQueue.remove();
                        onMessageReadyToBeSent(obj);
                    }
                }

                if(/*disconnectNow == true*/ false)
                    break;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
    }

    protected class ReceiverTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                while(incomingMessageQueue.isEmpty() == false) {
                    JSONObject obj = incomingMessageQueue.remove();
                    onIncomingMessageFromQueue(obj);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * This function is called whenever a message is dequeued from incomingMessageQueue
     * @param json
     */
    protected void onIncomingMessageFromQueue(JSONObject json) {
        try {

            String type = json.getString("type");
            if(listenerMap.containsKey(type)) {
                for(IReceiver r : listenerMap.get(type)) {
                    final IReceiver rec = r;
                    final JSONObject jsonFinal = json;

                    if(rec instanceof GameController) {
                        RelativeLayout view = (RelativeLayout)((GameView)((GameController)rec).getGameView());
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    rec.onReceive(jsonFinal);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if(rec instanceof Activity) {
                        ((Activity)rec).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    rec.onReceive(jsonFinal);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called whenever a message is dequeued from outgoingMessageQueue
     * @param json
     */
    abstract protected void onMessageReadyToBeSent(JSONObject json);

    abstract public Boolean isConnected();

    abstract public void connect(String ip, int port);


}
