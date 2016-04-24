package no.group16.softark.tdt4240.softarkclient;

import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements IReceiver {

    Button addNameButton;
    ViewFlipper flipper;
    TextView playerListLabel;
    TextView roomOwnerLabel;
    TextView gamePinLabel;
    EditText usernameInput;
    Button startGameButton;

    ArrayList<String> players;
    String gamePin = "???";
    String hostName = "";
    Boolean host = false;

    Boolean gameIsStarting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        players  = new ArrayList<>();

        addNameButton = (Button)findViewById(R.id.addMeButton);
        flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        usernameInput = (EditText)findViewById(R.id.userNameInput);
        roomOwnerLabel = (TextView)findViewById(R.id.roomOwnerLabel);
        gamePinLabel = (TextView)findViewById(R.id.gamePinLabel);
        playerListLabel = (TextView)findViewById(R.id.playerListLabel);
        startGameButton = (Button)findViewById(R.id.startGameBtn);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("gamePin")) {
            gamePin = bundle.getString("gamePin");
            hostName = bundle.getString("gameOwner");

            roomOwnerLabel.setText(getResources().getText(R.string.gameowner_lbltxt) + " \"" + hostName + "\"");

            if(bundle.containsKey("asHost")) {
                host = true;
                gamePinLabel.setText(getResources().getText(R.string.gamepin_lbltxt) + gamePin);
                flipper.setDisplayedChild(1);
                startGameButton.setVisibility(View.VISIBLE);
                playerListLabel.setText(usernameInput.getText().toString());

            } else {
                startGameButton.setVisibility(View.INVISIBLE);
            }
        }

        addNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "setPlayerNameRequest");
                    obj.put("name", usernameInput.getText());
                    GameManager.getInstance().getServerHandler().queueMessage(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "startGameFromLobbyRequest");
                    obj.put("gamePin", gamePin);
                    GameManager.getInstance().getServerHandler().queueMessage(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        GameManager.getInstance().getServerHandler().registerListener("setPlayerNameResponse", this);
        GameManager.getInstance().getServerHandler().registerListener("playerJoinedNotification", this);
        GameManager.getInstance().getServerHandler().registerListener("startGameFromLobbyResponse", this);
        GameManager.getInstance().getServerHandler().registerListener("playerLeftGameRoom", this);

    }

    @Override
    public void onReceive(final JSONObject json) throws JSONException {

        String type = json.getString("type");

        if(type.equals("setPlayerNameResponse")) {
            String result = json.getString("result");
            if (result.equals("accepted")) {
                flipper.setDisplayedChild(1);
                gamePinLabel.setText(getResources().getText(R.string.gamepin_lbltxt) + gamePin);
            } else if (result.equals("taken")) {
                Toast.makeText(this, "That name is already taken", Toast.LENGTH_LONG).show();
            } else if (result.equals("inappropriate")) {
                Toast.makeText(this, "You dirty bastard", Toast.LENGTH_LONG).show();
            }

        } else if(type.equals("playerJoinedNotification"))  {
            String list = "";
            this.players = new ArrayList<>();
            JSONArray playersJson = json.getJSONArray("players");
            for (int i = 0; i < playersJson.length(); i++) {
                list += playersJson.get(i) + "\n";
                this.players.add(playersJson.get(i).toString());
            }
            playerListLabel.setText(list);

        } else if(type.equals("startGameFromLobbyResponse"))  {
            gameIsStarting = true;
            Intent intent = new Intent(this, GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("players", this.players);

            //bundle.putString("keyboard", json.getString("keyboard"));

            intent.putExtras(bundle);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        } else if(type.equals("playerLeftGameRoom"))  {
                //{"name":"host","newHost":"gg","type":"playerLeftGameRoom"}

            String name = json.getString("name");

            if(json.has("newHost")) {
                String newHost = json.getString("newHost");
                if(usernameInput.getText().toString().equals(newHost)) {
                    host = true;
                    roomOwnerLabel.setText(getResources().getText(R.string.gameowner_lbltxt) + " \"" + hostName + "\"");
                    startGameButton.setVisibility(View.VISIBLE);
                    hostName = newHost;
                }
            }

            String list = "";

            if(players != null) {
                for(int i = 0; i < players.size(); i++) {
                    if(players.get(i).equals(name)) {
                        players.remove(i);
                        break;
                    }
                }
                for(int i = 0; i < players.size(); i++) {
                    list += players.get(i) + "\n";
                }

                playerListLabel.setText(list);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(gameIsStarting == false) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("type", "leavingGame");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GameManager.getInstance().getServerHandler().queueMessage(msg);
        }
    }
}
