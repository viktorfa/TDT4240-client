package no.group16.softark.tdt4240.softarkclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameSummaryActivity extends AppCompatActivity {

    Button doneBtn;
    TextView scoreListTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);
        hideSystemStatusBar();

        doneBtn = (Button) findViewById(R.id.gameSummaryDoneBtn);
        scoreListTxt = (TextView) findViewById(R.id.summaryScoreListTxt);
        HashMap<String, Integer> scoreList = (HashMap<String, Integer>)getIntent().getSerializableExtra("playerScores");

        String fullscoreString = "";
        for(String key : scoreList.keySet()){
            fullscoreString += key + " -> " + scoreList.get(key) + "\n";
        }
        scoreListTxt.setText(fullscoreString);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void hideSystemStatusBar(){
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
