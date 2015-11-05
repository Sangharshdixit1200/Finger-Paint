package uk.ac.st_andrews.cs.rbwilliams.fingerpaint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

/**
 * Created by rbwilliams on 23/10/2015.
 */
public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;
    private int paintColor = 0xFF660000;

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private float brushSize;
    private float lastBrushSize;

    private boolean erase = false;

    private boolean freeHand = true;

    private boolean circle = false;
    //private int radius = 5;
    private Paint circlePaint;
    private float cx, cy;

    private boolean square = false;
    private SparseArray<PointF> mActivePointers;
    private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW };
    private Paint squarePaint;
    private float x1, y1, x2, y2;


    private boolean triangle = false;


    private boolean multiTouch = false;
    private Paint multiTouchPaint;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){

        mActivePointers = new SparseArray<PointF>();

        // Initialise brushes
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint =  new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        circlePaint = new Paint();
        circlePaint.setColor(paintColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);

        squarePaint = new Paint();
        squarePaint.setColor(paintColor);
        squarePaint.setAntiAlias(true);
        squarePaint.setStyle(Paint.Style.FILL);

        multiTouchPaint = new Paint();
        multiTouchPaint.setAntiAlias(true);
        multiTouchPaint.setStyle(Paint.Style.STROKE);
        multiTouchPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        //view given size
        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        if(freeHand) {
            canvas.drawPath(drawPath, drawPaint);
        } else if(circle){
            canvas.drawPath(drawPath, circlePaint);
        } else if (square){
            canvas.drawPath(drawPath, squarePaint);

        } else if (triangle){

        } else if (multiTouch){
            // draw all pointers
            for (int size = mActivePointers.size(), i = 0; i < size; i++) {
                PointF point = mActivePointers.valueAt(i);
                if (point != null)
                    multiTouchPaint.setColor(colors[i % 9]);
                drawPath.addCircle(point.x, point.y, 60, Path.Direction.CW);
                drawCanvas.drawPath(drawPath, multiTouchPaint);
            }

            drawPath.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get coordinates of users touch
        float touchX = event.getX();
        float touchY = event.getY();

        int pointerIndex = event.getActionIndex();

        int pointerId = event.getPointerId(pointerIndex);

        int maskedAction = event.getActionMasked();


        if(freeHand || erase) {
            // switch statement for free hand drawing
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        } else if (circle){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cx = touchX;
                    cy = touchY;
                    //drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltax = touchX - cx;
                    float deltay = touchY - cy;
                    double radius = Math.sqrt(deltax*deltax + deltay*deltay);

                    drawPath.addCircle(touchX, touchY, (float)radius, Path.Direction.CW);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, circlePaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        } else if (square){
            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN:{
                    x1 = event.getX(pointerIndex);
                    y1 = event.getY(pointerIndex);
                    x2 = event.getX(pointerIndex);
                    y2 = event.getY(pointerIndex);
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {
                    x2 = event.getX(pointerIndex);
                    y2 = event.getY(pointerIndex);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    drawPath.addRect(x1, y1, x2, y2, Path.Direction.CW);
                    drawCanvas.drawPath(drawPath, squarePaint);
                    drawPath.reset();
                    break;
                }
            }
        } else if (triangle){

        } else if (multiTouch){
            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    PointF f = new PointF();
                    f.x = event.getX(pointerIndex);
                    f.y = event.getY(pointerIndex);
                    mActivePointers.put(pointerId, f);
                    break;
                }
                case MotionEvent.ACTION_MOVE: { // a pointer was moved
                    for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                        PointF point = mActivePointers.get(event.getPointerId(i));
                        if (point != null) {
                            point.x = event.getX(i);
                            point.y = event.getY(i);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {

                    mActivePointers.remove(pointerId);

                    break;
                }
            }
        }




        // call the onDraw() method
        invalidate();
        return true;

    }

    public void setColor(String newColor){
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        circlePaint.setColor(paintColor);
        squarePaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        //update size
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        erase = isErase;
        if(erase){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setFreeHand(boolean isFreeHand){
        freeHand = isFreeHand;
    }

    public void setShape(String shape){
        switch (shape) {
            case "circle":
                square = false;
                triangle = false;
                multiTouch = false;
                circle = true;
                break;
            case "square":
                circle = false;
                triangle = false;
                multiTouch = false;
                square = true;
                break;
            case "triangle":
                circle = false;
                square = false;
                multiTouch = false;
                triangle = true;
                break;
            case "false":
                circle = false;
                square = false;
                multiTouch = false;
                triangle = false;
                break;
            case "multi_touch":
                circle = false;
                square = false;
                triangle = false;
                multiTouch = true;
                break;
        }

    }
}
