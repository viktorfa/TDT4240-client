package no.group16.softark.tdt4240.softarkclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
        scoreList = sortByValues(scoreList);

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
