package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Rect;

/**
 * Created by tien on 4/14/2016.
 */
public class GameManager {
    private static GameManager ourInstance = new GameManager();

    private ServerHandler serverHandler;

    public static GameManager getInstance() {
        return ourInstance;
    }

    private GameManager() {
        serverHandler = new KyroNetServerHandler();
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

}
