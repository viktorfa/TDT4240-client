package no.group16.softark.tdt4240.softarkclient;

import android.os.AsyncTask;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


/**
 * Created by tien on 4/14/2016.
 */

/**
 * Info:
 * We set up a very barebone server to test the communication. For sending/receiving we just used
 * the String class ( kryo.register(String.class) ) and sent JSON objects as String objects.
 */
public class KyroNetServerHandler extends ServerHandler {
    Client client = new Client();
    ClientTask clientTask = null;


    public KyroNetServerHandler() {
        super();
    }

    /**
     * This function is called whenever a message is dequeued from incomingMessageQueue
     * @param json
     */
    @Override
    public void onIncomingMessageFromQueue(JSONObject json) {
        super.onIncomingMessageFromQueue(json);
        System.out.println("Received: " + json.toString());
    }

    /**
     * This function is called whenever a message is dequeued from outgoingMessageQueue
     * Messages should be sent to server from here.
     * @param json
     */
    @Override
    public void onMessageReadyToBeSent(JSONObject json) {
        client.sendTCP(json.toString());
        System.out.println("Sent: " + json.toString() );
    }

    @Override
    public Boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public void connect(String ip, int port) {
        if(clientTask == null) {
            clientTask = new ClientTask(ip, port);
            clientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    /**
     * This part connects to the server and listen for data for ever.
     * It should queue the received messages to incomingMessageQueue
     */
    private class ClientTask extends AsyncTask<Void, Void, Void> {

        int port;
        String ip;

        public ClientTask(String ip, int port) {
            this.ip =  ip;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... params) {
            client.start();
            try {
                client.connect(20000, ip, port, 54777);
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("type", "connectionEvent");
                    msg.put("event", "connected");
                    queueIncomingMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (final IOException ex) {
                try {
                    JSONObject errorMsg = new JSONObject();
                    errorMsg.put("type", "connectionEvent");
                    errorMsg.put("event", "error");
                    errorMsg.put("message", ex.getMessage());
                    queueIncomingMessage(errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //ex.printStackTrace();
                clientTask = null;
            }

            Kryo kryo = client.getKryo();
            kryo.register(String.class);

            // When a new string is received from server
            client.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    super.connected(connection);

                    JSONObject errorMsg = new JSONObject();
                    try {
                        errorMsg.put("type", "connectionEvent");
                        errorMsg.put("event", "connected");
                        queueIncomingMessage(errorMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void received (Connection connection, Object object) {
                    if (object instanceof String) {
                        String msg = (String)object;
                        try {
                            JSONObject reader = new JSONObject(msg);    // Parse it as JSON
                            queueIncomingMessage(reader);               // queue it
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    super.disconnected(connection);
                    JSONObject errorMsg = new JSONObject();
                    try {
                        errorMsg.put("type", "connectionEvent");
                        errorMsg.put("event", "connectionLost");
                        queueIncomingMessage(errorMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    connection.close();
                    connection = null;
                    clientTask = null;
                }

            });

            return null;
        }
    }


}
