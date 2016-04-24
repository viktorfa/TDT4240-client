package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Color;

/**
 * Created by tien on 4/14/2016.
 */
public class Eraser extends  DrawingTool{

    public static String id = "drawingtool.eraser";


    public Eraser() {
        super(id);

        this.relativeSize = 0.1f;
        this.color = Color.WHITE;
    }

}
