package no.group16.softark.tdt4240.softarkclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tien on 4/15/2016.
 */
public class GameView extends RelativeLayout implements IGameView {

    IRenderer renderer;

    private TableRow keyRow1;
    private TableRow keyRow2;
    private TableRow enteredLettersRow;

    private TextView playerListTextView;
    private TextView wordTextView;
    private TextView whoIsDrawingTextView;
    private TextView clockTextView;

    private ImageButton clearButton;

    private LinearLayout toolContainer;

    public GameView(Context context) {
        super(context);

        inflate(getContext(), R.layout.activity_game, this);

        keyRow1 = (TableRow)findViewById(R.id.keyboardRow1);
        keyRow2 = (TableRow)findViewById(R.id.keyboardRow2);
        enteredLettersRow = (TableRow)findViewById(R.id.enteredLettersRow);

        playerListTextView = (TextView)findViewById(R.id.playersTextView);
        wordTextView = (TextView)findViewById(R.id.wordToDrawTextView);
        whoIsDrawingTextView = (TextView)findViewById(R.id.whoIsDrawingTextView);
        clockTextView = (TextView)findViewById(R.id.clockTextView);

        clearButton = (ImageButton)findViewById(R.id.deleteDrawingBtn);

        renderer = new CanvasRenderer(this);

        toolContainer = (LinearLayout)findViewById(R.id.toolContainer);

    }

    /**
     * Takes a string (should already be shuffled) and adds the containing letters to the keyboard
     * @param string
     */
    @Override
    public void generateKeyboard(String string, final int MAX_CHAR_PER_ROW, OnClickListener handleButtonPress) {
        string = string.toLowerCase();
        keyRow1.removeAllViews();
        keyRow2.removeAllViews();
        enteredLettersRow.removeAllViews();

        for(int i = 0; i < string.length(); i++) {
            ImageButton imgBtn = new ImageButton(getContext());
            imgBtn.setBackgroundColor(Color.TRANSPARENT);
            char c = string.charAt(i);
            imgBtn.setImageResource(getResources().getIdentifier(c + "_grey", "drawable", getContext().getPackageName()));
            imgBtn.setMinimumHeight(0);
            imgBtn.setMinimumWidth(0);
            imgBtn.setPadding(2,4,2,2);

            if(i < MAX_CHAR_PER_ROW)
                keyRow1.addView(imgBtn);
            else
                keyRow2.addView(imgBtn);

            imgBtn.setTag(c);   // The letter is stored in the tag
            imgBtn.setOnClickListener(handleButtonPress);
        }
    }

    @Override
    public void moveLetterBtn(android.view.View v, final int MAX_CHAR_PER_ROW){
        ImageButton btn = (ImageButton)v;

        if(btn.getParent() != enteredLettersRow) {
            ((TableRow)v.getParent()).removeView(v);
            enteredLettersRow.addView(btn);
        }
        else{
            ((TableRow)v.getParent()).removeView(v);

            if(keyRow1.getChildCount() < MAX_CHAR_PER_ROW)
                keyRow1.addView(btn);
            else
                keyRow2.addView(btn);
        }
    }

    @Override
    public IRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void updatePlayerListTextView(String text){
        this.playerListTextView.setText(text);
    }

    @Override
    public void updateTimerTextView(String text) {
        clockTextView.setText(text);
    }

    @Override
    public void setDrawListener(OnTouchListener touchListener) {
        renderer.setDrawerListener(touchListener);
    }

    @Override
    public void setClearBtnListener(OnClickListener clickListener) {
        clearButton.setOnClickListener(clickListener);
    }

    @Override
    public void onNewWordToDraw(String newWord) {
        this.keyRow1.removeAllViews();
        this.keyRow2.removeAllViews();
        this.enteredLettersRow.removeAllViews();
        this.whoIsDrawingTextView.setText(getResources().getText(R.string.drawing_now) + "\n" + getResources().getString(R.string.you));
        this.wordTextView.setText(getResources().getText(R.string.your_turn) + " " + newWord.toUpperCase());
        this.wordTextView.setVisibility(VISIBLE);
        this.clearButton.setVisibility(VISIBLE);
        this.toolContainer.setVisibility(VISIBLE);
    }

    @Override
    public void onNewWordToGuess(String whoIsDrawing) {
        this.whoIsDrawingTextView.setText(getResources().getText(R.string.drawing_now) + "\n" + whoIsDrawing);
        this.clearButton.setVisibility(GONE);
        this.wordTextView.setVisibility(GONE);
        this.toolContainer.setVisibility(GONE);

    }

    @Override
    public void onWordGuessedCorrectly(String answer, String guesser) {
        this.keyRow1.removeAllViews();
        this.keyRow2.removeAllViews();
        this.enteredLettersRow.removeAllViews();
        this.wordTextView.setText(guesser + " " +  getResources().getText(R.string.guessed_right) + ": " + answer.toUpperCase());
    }

    @Override
    public String getEnteredWord() {
        String typedWord = "";

        for (int i = 0; i < enteredLettersRow.getChildCount(); i++) {
            ImageButton btn = (ImageButton)enteredLettersRow.getChildAt(i);
            typedWord += btn.getTag();
        }

        return typedWord;
    }

    @Override
    public void addToolButton(int drawable, View.OnClickListener clickListener) {
        ImageButton button = new ImageButton(getContext());
        button.setImageResource(drawable);
        button.setOnClickListener(clickListener);
        this.toolContainer.addView(button);
    }


}
