package no.group16.softark.tdt4240.softarkclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by tien on 4/14/2016.
 */
public class CanvasRenderer implements IRenderer {
    private HashMap<String, DrawingTool> drawingTools;
    private ArrayList<DrawingPath> drawingPaths = new ArrayList<DrawingPath>();
    private Paint paint = new Paint();
    SurfaceView surfaceView;
    Canvas canvas;

    public CanvasRenderer(View context) {
        this.surfaceView = (SurfaceView)(context).findViewById(R.id.surfaceView);
        paint.setStyle(Paint.Style.STROKE);

        drawingTools = new HashMap<String, DrawingTool>();
        drawingTools.put(Eraser.id, new Eraser());
        drawingTools.put(Pencil.id, new Pencil());

        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onUpdate(ArrayList<DrawingPath> paths) {
        drawingPaths = paths;
    }


    @Override
    public void onRender() {
        canvas = surfaceView.getHolder().lockCanvas();

        if(canvas != null) {
            canvas.drawColor(Color.WHITE);

            for(DrawingPath drawingPath : drawingPaths) {
                DrawingTool tool = drawingTools.get(drawingPath.drawingToolId);
                int toolWidth = (int)(canvas.getWidth() * tool.getToolSize());  // Relative to screen
                paint.setStrokeWidth(toolWidth);
                paint.setColor(tool.color);

                Path path = new Path();

                if(drawingPath.points.size() == 2
                        && drawingPath.points.get(0).x == drawingPath.points.get(1).x
                        && drawingPath.points.get(0).y == drawingPath.points.get(1).y ) {

                    float normX = drawingPath.points.get(0).x;
                    float normY = drawingPath.points.get(0).y;

                    float screenX = -((-1.0f - normX) / 2.0f) * canvas.getWidth();
                    float screenY = -((-1.0f - normY) / 2.0f) * canvas.getHeight();

                    path.moveTo((int)screenX, (int)screenY);
                    path.lineTo((int)screenX+5, (int)screenY);
                } else {
                    for(int i = 0; i < drawingPath.points.size(); i++) {
                        float normX = drawingPath.points.get(i).x;
                        float normY = drawingPath.points.get(i).y;
                        float screenX = -((-1.0f - normX) / 2.0f) * canvas.getWidth();
                        float screenY = -((-1.0f - normY) / 2.0f) * canvas.getHeight();

                        if(i == 0)
                            path.moveTo((int)screenX, (int)screenY);
                        else
                            path.lineTo((int)screenX, (int)screenY);
                    }
                }
                canvas.drawPath(path, paint);
            }
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
        }

    }

    @Override
    public void setDrawerListener(View.OnTouchListener listener) {
        surfaceView.setOnTouchListener(listener);
    }

    @Override
    public int getDrawingAreaWidth() {
        return surfaceView.getWidth();
    }

    @Override
    public int getDrawingAreaHeight() {
        return surfaceView.getHeight();

    }


}
