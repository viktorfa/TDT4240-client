package no.group16.softark.tdt4240.softarkclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tien on 4/15/2016.
 */
public interface IReceiver {

    void onReceive(final JSONObject json) throws JSONException;

}
