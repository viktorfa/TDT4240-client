package no.group16.softark.tdt4240.softarkclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    Controller gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemStatusBar();

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> players = bundle.getStringArrayList("players");
        gameController = new GameController(this, players);

        setContentView((GameView)gameController.getGameView());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void hideSystemStatusBar(){
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStop() {
        gameController.handleGameActivityStop();     //TODO: ok?
        super.onStop();
    }

    /**
     * MAYBE THIS SHOULD BE DONE ON THE SERVER INSTEAD, SO ALL USERS GET THE SAME?
     * Take the original word, add some random letters and shuffle all
     * @param originalWord
     * @return Shuffled string containing the original word + random letters
     */
    /*private String generateLetters(String originalWord) {
        String letterSet = originalWord;
        Random r = new Random();
        while(letterSet.length() < TOT_AVAIL_CHAR) {
            char c = (char)(r.nextInt(26) + 'a');
            letterSet += c;
        }
        return shuffle(letterSet);
    }*/

}
