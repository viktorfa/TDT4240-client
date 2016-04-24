package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Canvas;

/**
 * Created by tien on 4/14/2016.
 */
public abstract class DrawingTool {
    final String toolId;
    float relativeSize;
    int color;

    public int getColor() {
        return color;
    }

    public DrawingTool(String toolId) {
        this.toolId = toolId;
    }

    public String getToolId() {
        return toolId;
    }

    public float getToolSize() {
        return relativeSize;
    }

}
