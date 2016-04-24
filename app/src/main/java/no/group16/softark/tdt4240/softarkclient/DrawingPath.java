package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by Even on 17.04.2016.
 */
public class DrawingPath {
    String drawingToolId;
    int colorId;
    ArrayList<PointF> points;

    public DrawingPath(ArrayList<PointF> points, String drawingToolId, int colorId) {
        this.colorId = colorId;
        this.drawingToolId = drawingToolId;
        this.points = points;
    }


    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public ArrayList<PointF> getPoints() {
        return points;
    }

    public void setPath(ArrayList<PointF> points) {
        this.points = points;
    }

    public String getDrawingToolId() {
        return drawingToolId;
    }

    public void setDrawingToolId(String drawingToolId) {
        this.drawingToolId = drawingToolId;
    }
}