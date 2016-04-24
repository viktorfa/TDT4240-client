package no.group16.softark.tdt4240.softarkclient;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by tien on 4/14/2016.
 */
public interface IRenderer {

    public void onUpdate(ArrayList<DrawingPath> paths);
    public void onRender();
    public void setDrawerListener(View.OnTouchListener listener);
    public int getDrawingAreaWidth();
    public int getDrawingAreaHeight();

}
