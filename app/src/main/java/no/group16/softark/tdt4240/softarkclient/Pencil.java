package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Color;

/**
 * Created by tien on 4/14/2016.
 */
public class Pencil extends DrawingTool {
    public static String id = "drawingtool.pencil";
    public Pencil() {
        super(id);
        this.relativeSize = 0.01f;
        this.color = Color.BLACK;
    }
}
