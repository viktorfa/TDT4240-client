package no.group16.softark.tdt4240.softarkclient;

import android.content.Context;
import android.graphics.Point;
import android.view.SurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tien on 4/14/2016.
 */
public abstract class Controller {
    protected Logic gameLogic;
    protected IGameView gameView;

    protected Controller(Context context, IGameView gameView, Logic gamelogic){
        this.gameView =  gameView;
        this.gameLogic = gamelogic;
        onInit();
    }

    /**
     * When it's this player's turn to draw
     * @param json
     */
    abstract protected void handleNewWord(JSONObject json);

    /**
     * When it's another player's turn to draw
     * @param json
     * @throws JSONException
     */
    abstract protected void handleNewKeyboard(JSONObject json) throws JSONException;

    abstract protected void handleCheckAnswerResponse(JSONObject json);

    abstract protected void handleTimeUpdate(JSONObject json);

    abstract protected void handlePlayerLeft(JSONObject json);

    abstract protected void handleGameOverNotification();

    abstract protected void handleGameActivityStop();

    abstract public void onInit();

    public Logic getGameLogic() {
        return gameLogic;
    }

    public void setGameLogic(Logic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public IGameView getGameView() {
        return gameView;
    }

    public void setGameView(IGameView gameView) {
        this.gameView = gameView;
    }


}
