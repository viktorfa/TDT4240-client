package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tien on 4/15/2016.
 */
public abstract class Logic {

    private final int MAX_LETTERS_PER_ROW;

    public Logic(int maxLetterPerRow){
        this.MAX_LETTERS_PER_ROW = maxLetterPerRow;
    }
    public int getMAX_LETTERS_PER_ROW() {
        return MAX_LETTERS_PER_ROW;
    }
    //abstract public void addPath(DrawingPath path);

    abstract public boolean getMyTurnToDraw();
    abstract public String getActiveToolId();

    abstract public void onDrawStart(float x, float y, int canvasWidth, int canvasHeight);
    abstract public void onDrawProgress(float x, float y, int canvasWidth, int canvasHeight);
    abstract public void onDrawStop(float x, float y, int canvasWidth, int canvasHeight);
    abstract public void onNewWordToGuess(String keyboard, String drawer);
    abstract public void onMyTurnToDraw();
    abstract public void onNewPathByServer(JSONObject json);
    abstract public void onReadyToStartGame();
    abstract public void onUserClearCanvas();
    abstract public void onTimeUpdate(int seconds);
    abstract public void onLeavingGame();

    abstract public void sendAnswerCheck(String word);
    abstract public HashMap<String, Integer> getPlayerScores();
    abstract public String getPlayerScoresAsString();

    abstract public void setPlayerList(HashMap<String, Integer> players);
    abstract public void setDrawingToolId(String id);

    abstract ArrayList<DrawingPath> getPaths();

    abstract String getTimeAsString();


}
