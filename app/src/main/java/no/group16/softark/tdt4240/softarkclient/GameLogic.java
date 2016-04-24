package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.BoringLayout;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by tien on 4/15/2016.
 */
public class GameLogic extends Logic {

    DrawingPath tmpPath;
    int tmpPathIndex = -1;

    ArrayList<DrawingPath> paths;
    HashMap<String, Integer> playerScores;

    String selectedCharacters;
    String availableCharacters;

    String activeToolId;



    Boolean isGameOwner;

    Boolean myTurnToDraw = false;
    String whoIsDrawing;
    String keyboard;

    int time = -1;


    public GameLogic(){
        super(14);
        paths = new ArrayList<>();
        playerScores = new HashMap<String, Integer>() ;
    }



    public void setWhoIsDrawing(String whoIsDrawing) {
        this.whoIsDrawing = whoIsDrawing;
    }

    @Override
    public boolean getMyTurnToDraw() {
        return myTurnToDraw;
    }

    @Override
    public void onDrawStart(float x, float y, int canvWidth, int canvHeight) {
        float nx = (x /  canvWidth) * 2.0f - 1.0f;  // Normalized x coordinate
        float ny = (y / canvHeight) * 2.0f - 1.0f; // Normalized y coordinate

        tmpPath = new DrawingPath(new ArrayList<PointF>(), activeToolId, Color.BLACK); //TODO
        tmpPath.points.add(new PointF(nx, ny));
        onTemporaryPathByUser(tmpPath);
        tmpPathIndex = paths.size()-1;
    }

    @Override
    public void onDrawProgress(float x, float y, int canvWidth, int canvHeight) {
        float nx = (x /  canvWidth) * 2.0f - 1.0f;  // Normalized x coordinate
        float ny = (y / canvHeight) * 2.0f - 1.0f; // Normalized y coordinate

        if(tmpPath.points.size() % 10 == 0) {
            onNewPathByUser(tmpPath);
            PointF lastPt = tmpPath.points.get(tmpPath.points.size()-1);
            tmpPath = new DrawingPath(new ArrayList<PointF>(), activeToolId, Color.BLACK); //TODO
            tmpPath.points.add(lastPt);
            tmpPath.points.add(new PointF(nx, ny));
        } else {
            tmpPath.points.add(new PointF(nx, ny));
            onTemporaryPathByUser(tmpPath);
        }
    }

    @Override
    public void onDrawStop(float x, float y, int canvWidth, int canvHeight) {
        float nx = (x /  canvWidth) * 2.0f - 1.0f;  // Normalized x coordinate
        float ny = (y / canvHeight) * 2.0f - 1.0f; // Normalized y coordinate

        tmpPath.points.add(new PointF(nx, ny));
        onNewPathByUser(tmpPath);
        tmpPathIndex = -1;
    }

    @Override
    public void onNewWordToGuess(String keyboard, String drawer) {
        this.whoIsDrawing = drawer;
        this.keyboard = keyboard;
        this.myTurnToDraw = false;
        this.paths.clear();
    }

    @Override
    public void onMyTurnToDraw() {
        this.whoIsDrawing = "";
        this.keyboard = "";
        this.myTurnToDraw = true;
        this.paths.clear();
    }

    @Override
    public void onNewPathByServer(JSONObject json) {
        try {
            String command = json.getString("command");

            if(command.equals("clearCanvas")) {
                this.paths.clear();
            } else if(command.equals("drawLine")) {
                JSONArray points = json.getJSONArray("points");
                ArrayList<PointF> pointList = new ArrayList<>();
                for(int i = 0; i < points.length(); i+=2) {
                    float x, y;
                    x = ((Number) points.get(i)).floatValue();
                    y = ((Number) points.get(i+1)).floatValue();
                    pointList.add(new PointF(x, y));
                }
                String toolId = json.getString("drawingTool");
                String penSize = json.getString("drawingTool");
                DrawingPath drawingPath = new DrawingPath(pointList, toolId, Color.BLACK);
                paths.add(drawingPath);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadyToStartGame() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "readyToStartGame");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameManager.getInstance().getServerHandler().queueMessage(msg);
    }

    @Override
    public void onUserClearCanvas() {
        this.paths.clear();
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "newPathDrawn");
            msg.put("command", "clearCanvas");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameManager.getInstance().getServerHandler().queueMessage(msg);
    }

    @Override
    public void onTimeUpdate(int seconds) {
        this.time = seconds;
    }

    @Override
    public void onLeavingGame() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "leavingGame");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameManager.getInstance().getServerHandler().queueMessage(msg);
    }


    protected void onNewPathByUser(DrawingPath drawingPath) {
        JSONObject msg = new JSONObject();
        try {
            JSONArray jsonPoints = new JSONArray();
            for(int i = 0; i < drawingPath.points.size(); i++) {
                jsonPoints.put((float)drawingPath.points.get(i).x);
                jsonPoints.put((float)drawingPath.points.get(i).y);
            }
            msg.put("type", "newPathDrawn");
            msg.put("command", "drawLine");
            msg.put("drawingTool", drawingPath.getDrawingToolId());
            msg.put("size", 5);
            msg.put("color", drawingPath.colorId);
            msg.put("points", jsonPoints);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GameManager.getInstance().getServerHandler().queueMessage(msg);

        paths.add(drawingPath);
    }


    @Override
    public void sendAnswerCheck(String word) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "checkAnswerRequest");
            msg.put("word", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameManager.getInstance().getServerHandler().queueMessage(msg);
    }

    protected void onTemporaryPathByUser(DrawingPath drawingPath) {
        if(tmpPathIndex == -1)
            paths.add(drawingPath);
        else
            paths.set(tmpPathIndex, drawingPath);
    }

    public void setMyTurnToDraw(Boolean myTurnToDraw) {
        this.myTurnToDraw = myTurnToDraw;
    }

    @Override
    public HashMap<String, Integer> getPlayerScores() {
        return playerScores;
    }

    @Override
    public String getPlayerScoresAsString() {
        String playerList = "";
        for (String key : playerScores.keySet()) {
            String player = key;
            int score = playerScores.get(key);
            playerList += player + " (" + score + ")\n";
        }
        return playerList;
    }

    public Boolean getGameOwner() {
        return isGameOwner;
    }

    public void setGameOwner(Boolean gameOwner) {
        isGameOwner = gameOwner;
    }

    public String getSelectedCharacters() {
        return selectedCharacters;
    }

    public void setSelectedCharacters(String characters) {
        this.selectedCharacters = characters;
    }

    public String getAvailableCharacters() {
        return availableCharacters;
    }

    public void setAvailableCharacters(String characters) {
        this.availableCharacters = characters;
    }

    @Override
    public void setPlayerList(HashMap<String, Integer> playersScores) {
        this.playerScores = sortByValues(playersScores);
    }

    @Override
    String getTimeAsString() {
        final int min = time/60;
        final int sec = time-(min*60);
        final String strMin = String.format("%02d", min);
        final String strSec = String.format("%02d", sec);
        return String.format("%s:%s",strMin,strSec);
    }

    @Override
    public void setDrawingToolId(String id) {
        this.activeToolId = id;
    }

    @Override
    ArrayList<DrawingPath> getPaths() {
        return paths;
    }

    @Override
    public String getActiveToolId() {
        return activeToolId;
    }


    // http://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        ListIterator it = list.listIterator(list.size());
        HashMap sortedHashMap = new LinkedHashMap();

        while(it.hasPrevious()) {
            Map.Entry entry = (Map.Entry) it.previous();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }
}
