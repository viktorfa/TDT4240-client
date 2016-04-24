package no.group16.softark.tdt4240.softarkclient;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateGameActivity extends AppCompatActivity implements IReceiver {

    Context context;
    Spinner categorySpinner;
    Button doneButton;
    EditText hostNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        getSupportActionBar().hide();

        categorySpinner = (Spinner)findViewById(R.id.wordCatSpinner);
        doneButton = (Button)findViewById(R.id.newGameDoneBtn);
        hostNameText = (EditText)findViewById(R.id.newGameUsername);

        String[] wordCategories = getIntent().getStringArrayExtra("wordCategories");

        Spinner spinner = (Spinner) findViewById(R.id.wordCatSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wordCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        context = this;

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "createRoomRequest");
                    obj.put("wordListName", categorySpinner.getSelectedItem().toString());
                    obj.put("playerName", hostNameText.getText());
                    GameManager.getInstance().getServerHandler().queueMessage(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Intent intent = new Intent(context, LobbyActivity.class);
                //intent.putExtra("roomPin", roomId);
                //startActivity(intent);
            }
        });

        GameManager.getInstance().getServerHandler().registerListener("createRoomResponse", this);
    }


    @Override
    public void onReceive(final JSONObject json) throws JSONException {

        String type = json.getString("type");

        if(type.equals("createRoomResponse")) {
            String roomId = json.getString("gamePin");

            Intent intent = new Intent(this, LobbyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("gamePin", roomId);
            bundle.putBoolean("asHost", true);
            intent.putExtras(bundle);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

    }
}
