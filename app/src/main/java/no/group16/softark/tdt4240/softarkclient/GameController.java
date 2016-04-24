package no.group16.softark.tdt4240.softarkclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tien on 4/14/2016.
 */
public class GameController extends Controller implements IReceiver {

    Context context;

    public GameController(Context context, ArrayList<String> players){
        super(context);
        this.context = context;

        // Listen for these server events:
        GameManager.getInstance().getServerHandler().registerListener("newKeyboard", this);
        GameManager.getInstance().getServerHandler().registerListener("newWord", this);
        GameManager.getInstance().getServerHandler().registerListener("checkAnswerResponse", this);
        GameManager.getInstance().getServerHandler().registerListener("gameOverNotification", this);
        GameManager.getInstance().getServerHandler().registerListener("newPathDrawn", this);
        GameManager.getInstance().getServerHandler().registerListener("timeUpdate", this);
        GameManager.getInstance().getServerHandler().registerListener("playerLeftGameRoom", this);

        HashMap<String, Integer> scoreList = new HashMap<>();
        for(String player : players) {
            scoreList.put(player, 0);
        }

        updateScores(scoreList);
    }

    @Override
    public void onInit() {

        gameView.setClearBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameLogic.getMyTurnToDraw()) {
                    gameLogic.onUserClearCanvas();
                    gameView.getRenderer().onRender();
                }
            }
        });

        gameView.setDrawListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int canvWidth = getGameView().getRenderer().getDrawingAreaWidth();
                int canvHeight = getGameView().getRenderer().getDrawingAreaHeight();

                if(gameLogic.getMyTurnToDraw()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            gameLogic.onDrawStart(event.getX(), event.getY(), canvWidth, canvHeight);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            gameLogic.onDrawProgress(event.getX(), event.getY(), canvWidth, canvHeight);
                            gameView.getRenderer().onUpdate(gameLogic.getPaths());
                            gameView.getRenderer().onRender();
                            break;
                        case MotionEvent.ACTION_UP:
                            gameLogic.onDrawStop(event.getX(), event.getY(), canvWidth, canvHeight);
                            gameView.getRenderer().onUpdate(gameLogic.getPaths());
                            gameView.getRenderer().onRender();
                            break;
                    }
                }
                return true;
            }
        });

        gameView.addToolButton(R.drawable.eraser, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLogic.setDrawingToolId(Eraser.id);
            }
        });
        gameView.addToolButton(R.drawable.pen, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLogic.setDrawingToolId(Pencil.id);
            }
        });
        gameLogic.setDrawingToolId(Pencil.id);



        gameLogic.onReadyToStartGame();
        updateScores(gameLogic.getPlayerScores());
    }



    @Override
    protected void handleNewWord(JSONObject json) {
        try {
            String word = json.getString("word");
            gameLogic.onMyTurnToDraw();
            gameView.onNewWordToDraw(word);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameView.getRenderer().onUpdate(gameLogic.getPaths());
        gameView.getRenderer().onRender();
    }

    @Override
    public void handleNewKeyboard(JSONObject json) throws JSONException {
        String newKeboard = json.getString("keyboard");
        String drawer = json.getString("drawer");

        gameLogic.onNewWordToGuess(newKeboard, drawer);
        gameView.onNewWordToGuess(drawer);
        gameView.generateKeyboard(newKeboard, gameLogic.getMAX_LETTERS_PER_ROW(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.moveLetterBtn(v, gameLogic.getMAX_LETTERS_PER_ROW());
                gameLogic.sendAnswerCheck(gameView.getEnteredWord());
            }
        });
        gameView.getRenderer().onRender();
    }

    @Override
    protected void handleCheckAnswerResponse(JSONObject json) {
        try {
            String result = json.getString("result");

            if(result.equals("right")) {
                String correctWord = json.getString("correctWord");
                String guesser = json.getString("guesser");
                JSONArray newScores = json.getJSONArray("newPlayerScores");

                HashMap<String, Integer> scores = new HashMap<>();
                for(int i = 0; i < newScores.length(); i++) {
                    JSONObject obj = (JSONObject) newScores.get(i);
                    String player = obj.getString("name");
                    int score = obj.getInt("score");
                    scores.put(player, score);
                }
                updateScores(scores);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTimeUpdate(JSONObject json) {
        Integer time;

        try {
            time = json.getInt("time");
        } catch (JSONException e) {
            time = -1;
            e.printStackTrace();
        }
        gameLogic.onTimeUpdate(time);
        gameView.updateTimerTextView(gameLogic.getTimeAsString());
    }

    @Override
    protected void handlePlayerLeft(JSONObject json) {
        try {
            String name = json.getString("name");
            this.gameLogic.getPlayerScores().remove(name);
            this.gameView.updatePlayerListTextView(this.gameLogic.getPlayerScoresAsString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleGameOverNotification() {
        Intent summaryActivityIntent = new Intent(context, GameSummaryActivity.class);
        Bundle bundle = new Bundle();

        String playerList = gameLogic.getPlayerScoresAsString();
        gameView.updatePlayerListTextView(playerList);
        summaryActivityIntent.putExtra("playerScores", gameLogic.getPlayerScores());
        summaryActivityIntent.setFlags(summaryActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(summaryActivityIntent);

    }

    @Override
    protected void handleGameActivityStop() {
        gameLogic.onLeavingGame();
    }

    @Override
    public void onReceive(JSONObject json) throws JSONException {
        String type = json.getString("type");

        if(type.equals("newKeyboard")) {
            handleNewKeyboard(json);
        } if(type.equals("newWord")) {
            handleNewWord(json);
        } else if(type.equals("gameOverNotification")) {
            handleGameOverNotification();
        } else if(type.equals("checkAnswerResponse")) {
            handleCheckAnswerResponse(json);
        } else if(type.equals("newPathDrawn")) {
            gameLogic.onNewPathByServer(json);
            gameView.getRenderer().onUpdate(gameLogic.getPaths());
            gameView.getRenderer().onRender();
        }  else if(type.equals("timeUpdate")) {
            handleTimeUpdate(json);
        } else if(type.equals("playerLeftGameRoom")) {
            handlePlayerLeft(json);
        }

    }

    private void updateScores(HashMap<String, Integer> scoreList) {
        gameLogic.setPlayerList(scoreList);

        String playerList = "";
        for (String key : gameLogic.getPlayerScores().keySet()) {
            String player = key;
            int score = gameLogic.getPlayerScores().get(key);
            playerList += player + " (" + score + ")\n";
        }
        gameView.updatePlayerListTextView(playerList);
    }
}
